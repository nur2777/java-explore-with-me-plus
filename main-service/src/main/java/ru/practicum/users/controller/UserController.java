package ru.practicum.users.controller;


import ru.practicum.users.dto.UserDTO;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс для контроллера по работе с пользователями
 */
public interface UserController {
    /**
     * Эндпоинт на добавление пользователя
     * @param newUser новый пользователь
     * @return объект созданного пользователя
     */
    UserDTO add(UserDTO newUser);

    /** Эндпоинт удаления пользователя
     * @param userId идентфикатор пользователя
     */
    void deleteUser(Long userId);

    /**
     * Эндпоинт получения списка пользователей
     * @param userIds id пользователей
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * @return список пользователей
     */
    Collection<UserDTO> getAllUsers(List<Long> userIds, Integer from, Integer size);

}
