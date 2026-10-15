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

}
