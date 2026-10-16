package ru.practicum.comments.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.comments.dao.CommentRepository;
import ru.practicum.comments.dto.CommentDTO;
import ru.practicum.comments.dto.NewCommentDTO;
import ru.practicum.comments.model.Comment;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.model.Event;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dao.RequestRepository;
import ru.practicum.users.dao.UserRepository;
import ru.practicum.users.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User author;
    private User stranger;
    private Comment comment;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setName("Автор");

        stranger = new User();
        stranger.setId(2L);
        stranger.setName("Чужой");

        Event event = new Event();
        event.setId(10L);

        comment = new Comment();
        comment.setId(100L);
        comment.setUser(author);
        comment.setEvent(event);
        comment.setText("Старый текст");
    }

    @Test
    void updateComment_shouldChangeText() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentDTO result = commentService.updateComment(1L, 100L, new NewCommentDTO("Новый текст"));

        assertEquals(100L, result.getId());
        assertEquals("Новый текст", result.getText());
        assertEquals(1L, result.getUserId());
        assertEquals(10L, result.getEventId());
    }

    @Test
    void updateComment_whenUserNotFound_shouldThrowNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> commentService.updateComment(99L, 100L, new NewCommentDTO("Текст")));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_whenCommentNotFound_shouldThrowNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> commentService.updateComment(1L, 999L, new NewCommentDTO("Текст")));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_whenNotAuthor_shouldThrowConflict() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(stranger));
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertThrows(ClientErrorException.class,
                () -> commentService.updateComment(2L, 100L, new NewCommentDTO("Текст")));
        assertEquals("Старый текст", comment.getText());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_shouldDelete() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, 100L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_whenCommentNotFound_shouldThrowNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.deleteComment(1L, 999L));
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_whenNotAuthor_shouldThrowConflict() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(stranger));
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertThrows(ClientErrorException.class, () -> commentService.deleteComment(2L, 100L));
        verify(commentRepository, never()).delete(any());
    }
}
