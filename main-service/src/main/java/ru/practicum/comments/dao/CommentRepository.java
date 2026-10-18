package ru.practicum.comments.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.model.Comment;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByEventIdOrderByCreationDateDesc(Long eventId);
}
