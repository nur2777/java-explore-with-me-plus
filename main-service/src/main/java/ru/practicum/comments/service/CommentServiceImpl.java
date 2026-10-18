package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dao.CommentRepository;
import ru.practicum.comments.dto.CommentDTO;
import ru.practicum.comments.dto.NewCommentDTO;
import ru.practicum.comments.mapping.CommentMap;
import ru.practicum.comments.model.Comment;
import ru.practicum.common.EwmUtils;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dao.RequestRepository;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.users.dao.UserRepository;
import ru.practicum.users.model.User;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public CommentDTO addNewComment(Long userId, Long eventId, NewCommentDTO newCommentDTO) {
        User user = userCheck(userId);
        Event event = eventCheck(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ClientErrorException("Комментарий можно добавлять только к опубликованным событиям.");
        }
        Request request = requestRepository.findByRequesterIdAndEventId(userId,eventId)
                .orElseThrow(() -> new ClientErrorException("Заявка для пользователя с id " + userId + " на событие " + eventId + " не найдена "));
        if (!request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ClientErrorException("Комментарий можно добавлять только к событиям у которых одобрена заявка на участие.");
        }
        Comment newComment = new Comment();
        newComment.setUser(user);
        newComment.setEvent(event);
        newComment.setText(newCommentDTO.getText());
        Comment comment = commentRepository.save(newComment);
        return CommentMap.commentToCommentDTO(comment);
    }

    @Override
    @Transactional
    public CommentDTO updateComment(Long userId, Long commentId, NewCommentDTO updateComment) {
        userCheck(userId);
        Comment comment = commentCheck(commentId);
        authorCheck(comment, userId);

        comment.setText(updateComment.getText());
        return CommentMap.commentToCommentDTO(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        userCheck(userId);
        Comment comment = commentCheck(commentId);
        authorCheck(comment, userId);

        commentRepository.delete(comment);
    }

    /** Метод проверяет идентификатор и существование комментария
     * @param commentId - идентификатор комментария
     * @return - объект комментария
     */
    private Comment commentCheck(Long commentId) {
        EwmUtils.idIsNullCheck(commentId, "Идентификатор комментария commentId");
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id " + commentId + " не найден "));
    }

    /** Метод проверяет, что пользователь является автором комментария
     * @param comment - комментарий
     * @param userId - идентификатор пользователя
     */
    private void authorCheck(Comment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new ClientErrorException("Изменять и удалять комментарий может только его автор.");
        }
    }

    /** Метод проверяет идентификатор и существование пользователя
     * @param userId - идентификатор пользователя
     * @return объект пользователя
     */
    private ru.practicum.users.model.User userCheck(Long userId) {
        EwmUtils.idIsNullCheck(userId,"Идентификатор пользователя userId");
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден "));
    }

    /** Метод проверяет идентификатор и существование события
     * @param eventId - идентификатор события
     * @return - объект события
     */
    private Event eventCheck(Long eventId) {
        EwmUtils.idIsNullCheck(eventId,"Идентификатор события eventId");
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено "));
    }

    @Override
    public List<CommentDTO> getCommentsByEvent(Long eventId) {
        Event event = eventCheck(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ClientErrorException("Комментарии можно получать только у опубликованных событий.");
        }
        return commentRepository
                .findAllByEventIdOrderByCreationDateDesc(eventId)
                .stream()
                .map(CommentMap::commentToCommentDTO)
                .toList();
    }

    @Override
    public CommentDTO getCommentById(Long commentId) {
        Comment comment = commentCheck(commentId);

        return CommentMap.commentToCommentDTO(comment);
    }
}
