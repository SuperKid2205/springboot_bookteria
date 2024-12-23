package com.hung.practice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hung.practice.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
