package ru.practicum.comments.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDTO;
import ru.practicum.comments.dto.NewCommentDTO;
import ru.practicum.comments.service.CommentService;


@RestController
@RequestMapping("/users/{userId}/comments")
public class CommentControllerImpl implements CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentControllerImpl(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO addNewComment(@PathVariable Long userId,
                                    @RequestParam Long eventId,
                                    @Valid @RequestBody NewCommentDTO newCommentDTO) {
        return commentService.addNewComment(userId, eventId, newCommentDTO);
    }

    @PatchMapping("/{commentId}")
    @Override
    public CommentDTO updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @Valid @RequestBody NewCommentDTO updateComment) {
        return commentService.updateComment(userId, commentId, updateComment);
    }

    @DeleteMapping("/{commentId}")
    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
    }

}
