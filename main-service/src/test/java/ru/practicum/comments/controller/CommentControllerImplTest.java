package ru.practicum.comments.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.comments.dto.CommentDTO;
import ru.practicum.comments.dto.NewCommentDTO;
import ru.practicum.comments.service.CommentService;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.exception.NotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentControllerImpl.class)
class CommentControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @Test
    void updateComment_shouldReturn200() throws Exception {
        CommentDTO dto = CommentDTO.builder()
                .id(100L)
                .eventId(10L)
                .userId(1L)
                .text("Новый текст")
                .build();
        when(commentService.updateComment(eq(1L), eq(100L), any())).thenReturn(dto);

        mockMvc.perform(patch("/users/1/comments/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewCommentDTO("Новый текст"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.text").value("Новый текст"));
    }

    @Test
    void updateComment_withBlankText_shouldReturn400() throws Exception {
        mockMvc.perform(patch("/users/1/comments/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewCommentDTO("   "))))
                .andExpect(status().isBadRequest());
        verify(commentService, never()).updateComment(any(), any(), any());
    }

    @Test
    void updateComment_withTooLongText_shouldReturn400() throws Exception {
        mockMvc.perform(patch("/users/1/comments/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewCommentDTO("a".repeat(1001)))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateComment_whenNotAuthor_shouldReturn409() throws Exception {
        when(commentService.updateComment(eq(2L), eq(100L), any()))
                .thenThrow(new ClientErrorException("not author"));

        mockMvc.perform(patch("/users/2/comments/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewCommentDTO("Текст"))))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteComment_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/users/1/comments/100"))
                .andExpect(status().isNoContent());
        verify(commentService).deleteComment(1L, 100L);
    }

    @Test
    void deleteComment_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new NotFoundException("not found")).when(commentService).deleteComment(1L, 999L);

        mockMvc.perform(delete("/users/1/comments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteComment_whenNotAuthor_shouldReturn409() throws Exception {
        doThrow(new ClientErrorException("not author")).when(commentService).deleteComment(2L, 100L);

        mockMvc.perform(delete("/users/2/comments/100"))
                .andExpect(status().isConflict());
    }
}
