package ru.practicum.users.service;

import ru.practicum.users.dto.UserDTO;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс реализует логику CRUD-операций для сущности Пользователь
 */
public interface UserService {

    /** Метод добавления нового пользователя
     * @param userDTO данные нового пользователя
     * @return объект нового пользователя
     */
    UserDTO addNewUser(UserDTO userDTO);


    /** Метод удаления пользователя
     * @param userId идентификатор пользователя
     */
    void deleteUser(Long userId);

    /** Метод получения списка пользователей
     * @param userIds id пользователей
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * @return список пользователей
     */
    Collection<UserDTO> getUsers(List<Long> userIds, Integer from, Integer size);
}
