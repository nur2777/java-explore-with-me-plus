
package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comments.dto.CommentDTO;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDTO> getCommentsByEvent(
            @PathVariable Long eventId) {

        return commentService.getCommentsByEvent(eventId);
    }

    @GetMapping("/comments/{commentId}")
    public CommentDTO getCommentById(
            @PathVariable Long commentId) {

        return commentService.getCommentById(commentId);
    }
}
