package ru.practicum.users.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.UserDTO;
import ru.practicum.users.service.UserServiceImpl;

import java.util.Collection;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/admin/users")
public class UserControllerImpl implements UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserControllerImpl(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO add(@Valid @RequestBody UserDTO newUser) {
        return userService.addNewUser(newUser);
    }

    @DeleteMapping("/{userId}")
    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Valid @PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping
    @Override
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDTO> getUsers(@RequestParam(required = false) List<Long> ids,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return userService.getUsers(ids,from,size);
    }
}
