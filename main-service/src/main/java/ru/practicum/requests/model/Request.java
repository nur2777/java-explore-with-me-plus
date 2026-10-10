package ru.practicum.requests.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;

/**
 * Модель данных для заявки на участие
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "requests")
public class Request {
    /**
     * Идентификатор заявки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Событие на которое подана заявка
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private Event event;

    /**
     * Пользователь, который хочет принять участие в событии
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    @ToString.Exclude
    private User requester;

    /**
     * Статус заявки
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;

    /**
     * Дата и время создания заявки
     */
    @Column(name = "creation_date")
    private LocalDateTime created = LocalDateTime.now();
}
