package com.hung.practice.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.hung.practice.dto.request.AuthenticationRequest;
import com.hung.practice.dto.request.IntrospectRequest;
import com.hung.practice.dto.request.LogoutRequest;
import com.hung.practice.dto.request.RefreshRequest;
import com.hung.practice.dto.response.AuthenticationResponse;
import com.hung.practice.dto.response.IntrospectResponse;
import com.hung.practice.entity.InvalidatedToken;
import com.hung.practice.entity.UserEntity;
import com.hung.practice.enums.ErrorCode;
import com.hung.practice.exception.AppException;
import com.hung.practice.repository.InvalidatedTokenRepository;
import com.hung.practice.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationService {

    final UserRepository userRepository;
    final InvalidatedTokenRepository invalidatedTokenRepository;

    @Value("${jwt.signerKey}")
    String signerKey;

    @Value("${git.repo}")
    String gitRepo;

    @Value("${jwt.token.expires}")
    int tokenExpires;

    @Value("${jwt.token.refresh.expires}")
    int tokenRefreshExpires;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        // Check username
        Optional<UserEntity> userEntity = userRepository.findByUsername(request.getUsername());
        if (userEntity.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXITS);
        }

        // Check password
        PasswordEncoder encoder = new BCryptPasswordEncoder(10);
        boolean auth = encoder.matches(request.getPassword(), userEntity.get().getPassword());
        if (!auth) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        // Get token
        var token = generateToken(userEntity.get());

        return AuthenticationResponse.builder().authenticated(true).token(token).build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.getToken();
        boolean isValid = false;

        verifyToken(token, false);
        isValid = true;

        return IntrospectResponse.builder().valid(isValid).build();
    }

    private String generateToken(UserEntity userEntity) {
        // Header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // PayLoad
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userEntity.getUsername())
                .issuer(gitRepo)
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(tokenExpires, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(userEntity))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error(ErrorCode.GENERATE_TOKEN_FAILED.getMessage());
            throw new AppException(ErrorCode.GENERATE_TOKEN_FAILED);
        }
    }

    private String buildScope(UserEntity userEntity) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(userEntity.getRoles())) {
            userEntity.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
                }
            });
        }
        return stringJoiner.toString();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {

        // Create a new JWSVerifier using MAC(Message Authentication Code).
        JWSVerifier verifier = new MACVerifier((signerKey.getBytes()));

        // Get information in token
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (isRefresh) {
            expireTime = new Date(signedJWT
                    .getJWTClaimsSet()
                    .getIssueTime()
                    .toInstant()
                    .plus(tokenRefreshExpires, ChronoUnit.SECONDS)
                    .toEpochMilli());
        }

        // Check valid token
        boolean verified = signedJWT.verify(verifier);
        if (!(verified && expireTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Check logout token
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.TOKEN_LOGOUT);
        }

        return signedJWT;
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        String token = request.getToken();
        var signedJWT = verifyToken(token, false);

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .jwtID(signedJWT.getJWTClaimsSet().getJWTID())
                .expiredTime(signedJWT.getJWTClaimsSet().getExpirationTime())
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    public AuthenticationResponse refresh(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .jwtID(signedJWT.getJWTClaimsSet().getJWTID())
                .expiredTime(signedJWT.getJWTClaimsSet().getExpirationTime())
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        String username = signedJWT.getJWTClaimsSet().getSubject();

        UserEntity userEntity =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXITS));

        // Get token
        var refreshToken = generateToken(userEntity);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(refreshToken)
                .build();
    }
}
