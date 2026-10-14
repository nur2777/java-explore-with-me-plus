package ru.practicum.common;


import ru.practicum.exception.ValidationException;

/**
 * Класс для вспомогательных методов
 */
public final class EwmUtils {
    private EwmUtils() {

    }

    /** Метод проверки идентификатора на null
     * @param id идентификатор
     * @param msgPrefix  префикс для сообщения об ошибке
     */
    public static void idIsNullCheck(Long id, String msgPrefix) {
        if (id == null) {
            throw new ValidationException(msgPrefix + " должен быть заполнен!");
        }
    }
}
