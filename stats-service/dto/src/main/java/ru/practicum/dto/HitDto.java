package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor

public class HitDto {
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * Идентификатор запроса
     */
    private Long id;
    /**
     *  Идентификатор сервиса для которого записывается информация
     */
    @NotNull(message = "Идентификатор сервиса не может быть пустым")
    @NotBlank(message = "Идентификатор сервиса не может быть пустым")
    @Size(max = 100, message = "Длина идентификатора должна быть максимум 100 символов")
    private String app;

    /**
     *  URI для которого был осуществлен запрос
     */
    @NotNull(message = "URI запроса не может быть пустым")
    @NotBlank(message = "URI запроса не может быть пустым")
    @Size(max = 100, message = "Длина URI запроса должна быть максимум 100 символов")
    private String uri;

    /**
     * IP-адрес пользователя, осуществившего запрос
     */
    @NotNull(message = "IP-адрес пользователя не может быть пустым")
    @NotBlank(message = "IP-адрес пользователя не может быть пустым")
    @Size(max = 39, message = "Длина URI запроса должна быть максимум 39 символов")
    private String ip;

    /**
     *  Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
     */
    @NotNull(message = "Дата и время, когда был совершен запрос не могут быть пустыми")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime timestamp;
}
