
package ru.practicum.users.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.users.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.ANY
)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    @Test
    void shouldSaveUser() {
        User user = createUser("Daniel", "daniel@test.ru");

        User saved = userRepository.saveAndFlush(user);

        assertNotNull(saved.getId());
        assertEquals("Daniel", saved.getName());
        assertEquals("daniel@test.ru", saved.getEmail());
    }

    @Test
    void shouldFindUserById() {
        User saved = userRepository.saveAndFlush(
                createUser("Daniel", "daniel@test.ru")
        );

        User found = userRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals(saved.getId(), found.getId());
        assertEquals("Daniel", found.getName());
        assertEquals("daniel@test.ru", found.getEmail());
    }

    @Test
    void shouldFindAllUsersByIds() {
        User first = userRepository.saveAndFlush(
                createUser("Daniel", "daniel@test.ru")
        );

        User second = userRepository.saveAndFlush(
                createUser("Alex", "alex@test.ru")
        );

        userRepository.saveAndFlush(
                createUser("Max", "max@test.ru")
        );

        List<User> result = userRepository.findAllByIdIn(
                List.of(first.getId(), second.getId())
        );

        assertEquals(2, result.size());
        assertTrue(result.stream()
                .anyMatch(user -> user.getId().equals(first.getId())));
        assertTrue(result.stream()
                .anyMatch(user -> user.getId().equals(second.getId())));
    }

    @Test
    void shouldReturnEmptyListForUnknownIds() {
        List<User> result = userRepository.findAllByIdIn(
                List.of(-100L, -200L)
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRejectDuplicateEmail() {
        userRepository.saveAndFlush(
                createUser("Daniel", "same@test.ru")
        );

        User duplicate = createUser("Alex", "same@test.ru");

        assertThrows(
                DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(duplicate)
        );
    }

    @Test
    void shouldAllowDifferentEmails() {
        userRepository.saveAndFlush(
                createUser("Daniel", "daniel@test.ru")
        );

        userRepository.saveAndFlush(
                createUser("Alex", "alex@test.ru")
        );

        assertEquals(2, userRepository.count());
    }

    @Test
    void shouldDeleteUserById() {
        User saved = userRepository.saveAndFlush(
                createUser("Daniel", "daniel@test.ru")
        );

        Long userId = saved.getId();

        userRepository.deleteById(userId);
        userRepository.flush();

        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void shouldUpdateUser() {
        User saved = userRepository.saveAndFlush(
                createUser("Daniel", "daniel@test.ru")
        );

        saved.setName("Updated Daniel");
        userRepository.saveAndFlush(saved);

        User updated = userRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals("Updated Daniel", updated.getName());
    }
}
