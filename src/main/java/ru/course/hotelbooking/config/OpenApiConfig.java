
package ru.course.hotelbooking.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Hotel Booking API",
                version = "1.0.0",
                description = """
            REST API сервиса бронирования отелей.

            Возможности:
            - управление отелями и номерами;
            - регистрация пользователей;
            - бронирование номеров;
            - поиск и фильтрация;
            - выставление рейтинга отелям;
            - экспорт статистики в CSV.

            Авторизация: HTTP Basic.
            Роли: USER и ADMIN.
            """
        ),
        security = @SecurityRequirement(name = "basicAuth")
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic",
        description = "Авторизация по логину и паролю"
)
public class OpenApiConfig {
}
