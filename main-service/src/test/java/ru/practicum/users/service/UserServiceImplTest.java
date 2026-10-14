
package ru.practicum.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.users.dao.UserRepository;
import ru.practicum.users.dto.UserDTO;
import ru.practicum.users.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Daniel");
        user.setEmail("daniel@mail.ru");
    }

    @Test
    void addNewUserShouldSaveUser() {
        UserDTO dto = UserDTO.builder()
                .name("Daniel")
                .email("daniel@mail.ru")
                .build();

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        UserDTO result = userService.addNewUser(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Daniel", result.getName());
        assertEquals("daniel@mail.ru", result.getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void addNewUserShouldRejectDuplicateEmail() {
        UserDTO dto = UserDTO.builder()
                .name("Daniel")
                .email("daniel@mail.ru")
                .build();

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        assertThrows(ClientErrorException.class,
                () -> userService.addNewUser(dto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserShouldDeleteExistingUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserShouldThrowWhenNotFound() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.deleteUser(99L));

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void getUsersShouldReturnUsersByIds() {
        when(userRepository.findAllByIdIn(List.of(1L)))
                .thenReturn(List.of(user));

        var result = userService.getUsers(List.of(1L), 0, 10);

        assertEquals(1, result.size());
        assertEquals("Daniel", result.iterator().next().getName());

        verify(userRepository).findAllByIdIn(List.of(1L));
    }

    @Test
    void getUsersShouldReturnPagedUsers() {
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        var result = userService.getUsers(null, 0, 10);

        assertEquals(1, result.size());
        assertEquals(1L, result.iterator().next().getId());

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(userRepository).findAll(captor.capture());

        Pageable pageable = captor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    void getUsersShouldUseCorrectPageNumber() {
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        userService.getUsers(null, 20, 10);

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(userRepository).findAll(captor.capture());

        Pageable pageable = captor.getValue();

        assertEquals(2, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    void getUsersShouldThrowForMissingPagination() {
        assertThrows(ValidationException.class,
                () -> userService.getUsers(null, null, null));

        verifyNoInteractions(userRepository);
    }
}
