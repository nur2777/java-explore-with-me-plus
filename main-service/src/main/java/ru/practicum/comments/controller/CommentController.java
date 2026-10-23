package ru.practicum.comments.controller;

import ru.practicum.comments.dto.CommentDTO;
import ru.practicum.comments.dto.NewCommentDTO;

/**
 * Интерфейс для контроллера по работе с комментариями
 */
public interface CommentController {
    /**
     * Эндпоинт на добавление комментария
     * @param userId идентификатор автора
     * @param eventId идентификатор события
     * @param newComment новый комментарий
     * @return объект созданного комментария
     */
    CommentDTO addNewComment(Long userId, Long eventId, NewCommentDTO newComment);

    /**
     * Эндпоинт изменения комментария (может только автор)
     * @param userId идентификатор автора
     * @param commentId идентификатор комментария
     * @param updateComment новый текст комментария
     * @return объект изменённого комментария
     */
    CommentDTO updateComment(Long userId, Long commentId, NewCommentDTO updateComment);

    /**
     * Эндпоинт удаления комментария (может только автор)
     * @param userId идентификатор автора
     * @param commentId идентификатор комментария
     */
    void deleteComment(Long userId, Long commentId);
}
