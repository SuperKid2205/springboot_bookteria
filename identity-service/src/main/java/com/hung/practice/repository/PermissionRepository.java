package com.hung.practice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hung.practice.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
