package ru.practicum.controller;

import ru.practicum.dto.HitDTO;
import ru.practicum.dto.UriStatDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс для контроллера по работе с сервисом статистики
 */
public interface EndpointHitController {
    /**
     * Эндпоинт на создание запроса
     * @param newHit данные нового запроса
     */
    void addHit(HitDTO newHit);

    /**
     * Эндпоинт получения статистики по заданным параметрам
     * @param start обязательный, дата и время начала диапазона за который нужно выгрузить статистику
     * @param end обязательный, Дата и время конца диапазона за который нужно выгрузить статистику
     * @param uris Список uri для которых нужно выгрузить статистику
     * @param unique Нужно ли учитывать только уникальные посещения (только с уникальным ip)
     * @return объект
     */
    List<UriStatDTO> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
