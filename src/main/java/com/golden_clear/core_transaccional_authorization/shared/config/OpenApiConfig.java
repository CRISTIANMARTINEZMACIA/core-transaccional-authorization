package com.golden_clear.core_transaccional_authorization.shared.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Core Transaccional Authorization API",
                version = "0.0.1",
                description = "Servicio encargado de autorizar transacciones con tarjeta contra una red de tarjetas (simulada).",
                contact = @Contact(name = "Golden Clear")
        )
)
public class OpenApiConfig {
}
