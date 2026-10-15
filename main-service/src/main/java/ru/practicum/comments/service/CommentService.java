package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDTO;
import ru.practicum.comments.dto.NewCommentDTO;

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
}
