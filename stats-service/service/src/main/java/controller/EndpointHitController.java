package controller;

import dto.HitDTO;
import dto.UriStatDTO;

import java.util.ArrayList;
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
     * @param start обязательный, дата и время начала диапазона за который нужно
     *              выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
     * @param end обязательный, Дата и время конца диапазона за который нужно
     *            выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
     * @param uris Список uri для которых нужно выгрузить статистику
     * @param unique Нужно ли учитывать только уникальные посещения (только с уникальным ip)
     * @return объект
     */
    List<UriStatDTO> getStats(String start, String end, ArrayList<String> uris, boolean unique);
}
