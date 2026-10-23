
package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comments.dto.CommentDTO;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDTO> getCommentsByEvent(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return commentService.getCommentsByEvent(eventId, from, size);
    }

    @GetMapping("/comments/{commentId}")
    public CommentDTO getCommentById(
            @PathVariable Long commentId) {

        return commentService.getCommentById(commentId);
    }
}
