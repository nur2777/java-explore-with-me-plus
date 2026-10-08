package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Параметры запроса статистики (GET /stats)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsRequestDto {

    /**
     * Дата и время начала диапазона
     */
    private LocalDateTime start;

    /**
     * Дата и время конца диапазона
     */
    private LocalDateTime end;

    /**
     * Список uri, для которых нужна статистика (может быть пустым)
     */
    private List<String> uris;

    /**
     * Учитывать только уникальные посещения (с уникальным ip)
     */
    private boolean unique;
}
