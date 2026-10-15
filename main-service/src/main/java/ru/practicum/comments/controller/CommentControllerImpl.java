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

    @PostMapping("/{eventId}")
    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO addNewComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody NewCommentDTO newCommentDTO) {
        return commentService.addNewComment(userId, eventId, newCommentDTO);
    }

}
