package ru.practicum.service;

import ru.practicum.dto.HitDTO;
import ru.practicum.dto.UriStatDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс реализует логику операций для работы с запросами на эндпоинты
 */
public interface EndpointHitService {
    /** Метод создания запроса
     * @param hitDTO данные о запросе
     */
    void addHit(HitDTO hitDTO);

    /** Метод получения статистики по заданным параметрам
     * @return статистика в виде списка запросов
     */
    List<UriStatDTO> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
