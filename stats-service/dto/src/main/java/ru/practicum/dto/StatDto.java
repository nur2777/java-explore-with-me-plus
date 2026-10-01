package ru.practicum.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatDto {

    /**
     *  Название сервиса
     */
    private String app;

    /**
     *  URI сервиса
     */
    private String uri;

    /**
     *  Количество просмотров
     */
    private Long hits;
}
