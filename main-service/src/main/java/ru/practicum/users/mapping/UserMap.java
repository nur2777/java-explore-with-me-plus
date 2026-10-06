package ru.practicum.users.mapping;

import ru.practicum.users.dto.UserDTO;
import ru.practicum.users.model.User;

public class UserMap {

    public static User userDTOToUser(UserDTO userDTO) {
        User user = new User();
        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        return user;
    }

    public static UserDTO userToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
