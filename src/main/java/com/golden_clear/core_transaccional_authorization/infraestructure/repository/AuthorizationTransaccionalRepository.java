package com.golden_clear.core_transaccional_authorization.infraestructure.repository;

import com.golden_clear.core_transaccional_authorization.infraestructure.entity.AuthorizationTransaccional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthorizationTransaccionalRepository extends JpaRepository<AuthorizationTransaccional, String> {
}
