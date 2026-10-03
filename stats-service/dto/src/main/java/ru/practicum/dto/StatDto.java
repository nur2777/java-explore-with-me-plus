package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
