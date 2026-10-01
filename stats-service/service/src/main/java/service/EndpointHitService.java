package service;

import dto.HitDTO;
import dto.UriStatDTO;

import java.util.ArrayList;
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
    List<UriStatDTO> getStats(String start, String end, ArrayList<String> uris, boolean unique);
}
