package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDTO;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }

    @GetMapping
    public List<CommentDTO> getComments(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return commentService.getCommentsForAdmin(
                eventId, userId, text, from, size
        );
    }
}