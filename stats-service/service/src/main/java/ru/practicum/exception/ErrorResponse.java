package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    /**
     *  Название ошибки
     */
    private String error;
    /**
     * Подробное описание
     */
    private String description;

}