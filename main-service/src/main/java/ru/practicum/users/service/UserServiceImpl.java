package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.users.dao.UserRepository;
import ru.practicum.users.dto.UserDTO;
import ru.practicum.users.mapping.UserMap;
import ru.practicum.users.model.User;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    public static final int PAGE_SIZE = 32;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDTO addNewUser(UserDTO userDTO) {
        User newUser = UserMap.userDTOToUser(userDTO);
        if (emailIsDuplicate(newUser.getEmail())) {
            throw new ClientErrorException(String.format("Пользователь с e-mail '{}' уже существует." +
                    "Создание пользователей с одинаковым Email запрещено!",newUser.getEmail()));
        }
        return UserMap.userToUserDTO(userRepository.save(newUser));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден "));
        userRepository.deleteById(userId);
    }

    @Override
    public Collection<UserDTO> getUsers(List<Long> userIds, Integer from, Integer size) {
        List<User> resultList;
        if (userIds != null && !userIds.isEmpty()) {
            resultList = userRepository.findAllByIdIn(userIds);
        } else if (from != null && size != null) {
            Pageable page = PageRequest.of(from / size, size);
            Page<User> userPage = userRepository.findAll(page);
            resultList = userPage.getContent()
                    .stream()
                    .toList();
        } else {
            log.error("В метод getUsers переданы неверные параметры userIds=null, from={}, size={}", from, size);
            throw new ValidationException("Некорректно переданы параметры в метод getUsers");
        }
        return resultList.stream()
                .map(UserMap::userToUserDTO)
                .toList();
    }

    /** Проверка на дубликат. Существования пользователя с таким же email
     * проверку выполняем постранично, что бы не выполнять полный селект из БД
     * @param email - почта которую надо проверить
     * @return true - если пользователь с такой почтой уже есть, иначе false
     */
    private boolean emailIsDuplicate(String email) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(0, PAGE_SIZE, sortById);
        do {
            Page<User> userPage = userRepository.findAll(page);
            List<User> sameUsers = userPage.getContent()
                    .stream()
                    .filter(user -> user.getEmail().equals(email))
                    .toList();
            if (!sameUsers.isEmpty()) {
                log.info("Дубликат");
                return true;
            }
            if (userPage.hasNext()) {
                page = PageRequest.of(userPage.getNumber() + 1, userPage.getSize(), userPage.getSort());
            } else {
                page = null;
            }
        } while (page != null);
        return false;
    }
}
