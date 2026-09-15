package com.golden_clear.core_transaccional_authorization.infraestructure.repository;

import com.golden_clear.core_transaccional_authorization.infraestructure.entity.AuthorizationTransaccional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a persistencia de las transacciones autorizadas.
 * El identificador de la transacción (UUID) es generado por la base de datos/Hibernate.
 */
public interface AuthorizationTransaccionalRepository extends JpaRepository<AuthorizationTransaccional, String> {
}
