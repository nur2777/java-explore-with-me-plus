package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDTO;
import ru.practicum.comments.dto.NewCommentDTO;

import java.util.List;

/**
 * Интерфейс реализует логику CRUD-операций для сущности Комментарий
 */
public interface CommentService {

    /**
     * Метод на добавление комментария
     * @param userId идентификатор автора
     * @param eventId идентификатор события
     * @param newComment новый комментарий
     * @return объект созданного комментария
     */
    CommentDTO addNewComment(Long userId, Long eventId, NewCommentDTO newComment);

    /**
     * Метод изменения текста комментария. Изменять комментарий может только его автор
     * @param userId идентификатор автора
     * @param commentId идентификатор комментария
     * @param updateComment новый текст комментария
     * @return объект изменённого комментария
     */
    CommentDTO updateComment(Long userId, Long commentId, NewCommentDTO updateComment);

    /**
     * Метод удаления комментария. Удалять комментарий может только его автор
     * @param userId идентификатор автора
     * @param commentId идентификатор комментария
     */
    void deleteComment(Long userId, Long commentId);

    /**
     * Метод получения комментариев по событию.
     * @param eventId идентификатор события
     */
    List<CommentDTO> getCommentsByEvent(Long eventId);

    /**
     * Метод получения комментария.
     * @param commentId идентификатор комментария
     */
    CommentDTO getCommentById(Long commentId);
}
