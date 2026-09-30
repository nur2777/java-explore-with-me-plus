package controller;

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
     * @param TODO
     * @return объект
     */
    StatsDTO getStats();
}
