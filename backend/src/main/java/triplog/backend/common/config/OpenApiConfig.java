package triplog.backend.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {

        String securityJwtName = "JWT_Auth";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityJwtName);

        Components components = new Components().addSecuritySchemes(securityJwtName,
                new SecurityScheme()
                        .name(securityJwtName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        Info info = new Info()
                .title("Triplog API Docs")
                .description("Triplog 프로젝트 백엔드 API 명세서입니다.")
                .version("v0.0.1");

        return new OpenAPI()
                .info(info)
                .addServersItem(new Server().url("http://localhost:8080"))
                .addServersItem(new Server().url("https://triplog11.store"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
