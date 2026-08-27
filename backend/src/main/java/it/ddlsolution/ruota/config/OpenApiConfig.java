package it.ddlsolution.ruota.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${swagger.server.url}")
    private String serverUrl;

    private final Environment environment;

    public OpenApiConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public OpenAPI apiWithJWTBearerAuth() {
        final String securitySchemeName = "bearerAuth";

        Server swaggerServer = new Server();
        swaggerServer.setUrl(serverUrl);
        swaggerServer.setDescription("Swagger Server");

        // Ottieni il profilo attivo
        String[] activeProfiles = environment.getActiveProfiles();
        String profile = activeProfiles.length > 0 ? activeProfiles[0] : "default";
        String titleWithProfile = String.format("API [%s]", profile.toUpperCase());

        return new OpenAPI()
                .servers(List.of(swaggerServer))
                .info(new Info()
                        .title(titleWithProfile)
                        .version("1.0")
                        .description("API"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)));
    }

}

