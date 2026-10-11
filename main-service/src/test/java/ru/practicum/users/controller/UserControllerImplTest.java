
package ru.practicum.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.users.dto.UserDTO;
import ru.practicum.users.service.UserServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerImplTest {

    private MockMvc mockMvc;
    private UserServiceImpl userService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userService = mock(UserServiceImpl.class);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserControllerImpl(userService))
                .build();
    }

    @Test
    void addUserShouldReturnCreated() throws Exception {
        UserDTO input = UserDTO.builder()
                .name("Daniel")
                .email("daniel@mail.ru")
                .build();

        UserDTO saved = UserDTO.builder()
                .id(1L)
                .name("Daniel")
                .email("daniel@mail.ru")
                .build();

        when(userService.addNewUser(any(UserDTO.class)))
                .thenReturn(saved);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Daniel"))
                .andExpect(jsonPath("$.email").value("daniel@mail.ru"));

        verify(userService).addNewUser(any(UserDTO.class));
    }

    @Test
    void addUserShouldRejectBlankName() throws Exception {
        UserDTO input = UserDTO.builder()
                .name("")
                .email("daniel@mail.ru")
                .build();

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void addUserShouldRejectInvalidEmail() throws Exception {
        UserDTO input = UserDTO.builder()
                .name("Daniel")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void addUserShouldRejectMissingEmail() throws Exception {
        String json = """
                {
                  "name": "Daniel"
                }
                """;

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void getUsersShouldReturnList() throws Exception {
        UserDTO user = UserDTO.builder()
                .id(1L)
                .name("Daniel")
                .email("daniel@mail.ru")
                .build();

        when(userService.getUsers(isNull(), eq(0), eq(10)))
                .thenReturn(List.of(user));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Daniel"));

        verify(userService).getUsers(null, 0, 10);
    }

    @Test
    void getUsersShouldAcceptIds() throws Exception {
        when(userService.getUsers(eq(List.of(1L, 2L)), eq(0), eq(10)))
                .thenReturn(List.of());

        mockMvc.perform(get("/admin/users")
                        .param("ids", "1,2"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService).getUsers(List.of(1L, 2L), 0, 10);
    }

    @Test
    void getUsersShouldAcceptPagination() throws Exception {
        when(userService.getUsers(isNull(), eq(20), eq(10)))
                .thenReturn(List.of());

        mockMvc.perform(get("/admin/users")
                        .param("from", "20")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(userService).getUsers(null, 20, 10);
    }

    @Test
    void deleteUserShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUserShouldRejectInvalidId() throws Exception {
        mockMvc.perform(delete("/admin/users/abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}
