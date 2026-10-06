package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.DATE_TIME_PATTERN;

/**
 * Сведения об ошибке
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    /**
     * Список стектрейсов или описания ошибок
     */
    private List<String> errors;

    /**
     * Сообщение об ошибке
     */
    private String message;

    /**
     * Общее описание причины ошибки
     */
    private String reason;

    /**
     * Код статуса HTTP-ответа
     */
    private HttpStatus status;

    /**
     * Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiError(List<String> errors, String message, String reason, HttpStatus status) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = status;
    }
}