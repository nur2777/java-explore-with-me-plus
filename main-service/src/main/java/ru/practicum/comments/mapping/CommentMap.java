package ru.practicum.comments.mapping;

import ru.practicum.comments.dto.CommentDTO;
import ru.practicum.comments.model.Comment;

public class CommentMap {

    public static CommentDTO commentToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .userId(comment.getUser().getId())
                .text(comment.getText())
                .creationDate(comment.getCreationDate())
                .build();
    }
}
