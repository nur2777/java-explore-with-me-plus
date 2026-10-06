package ru.practicum.events.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.categories.model.Category;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;

/**
 * Модельных данных для описания события
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "events")
public class Event {

    /**
     * Уникальный идентификатор события
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Инициатор, пользователь создавший событие
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    @ToString.Exclude
    private User initiator;

    /**
     * Краткое описание события
     */
    @Column(name = "annotation")
    private String annotation;

    /**
     * Категория события
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private Category category;

    /**
     * Полное описание события
     */
    @Column(name = "description")
    private String description;

    /**
     * Заголовок события
     */
    @Column(name = "title")
    private String title;

    /**
     * Дата и время на которые намечено событие
     */
    @Column(name = "event_date")
    private LocalDateTime eventDate;

    /**
     * Географические координаты места проведения события
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "lat",
                    column = @Column(name = "location_lat")
            ),
            @AttributeOverride(
                    name = "lon",
                    column = @Column(name = "location_lon")
            )
    })
    private Location location;

    /**
     * Нужно ли оплачивать участие в событии
     */
    @Column(name = "paid")
    private Boolean paid;

    /**
     * Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
     */
    @Column(name = "participant_limit")
    private Integer participantLimit;

    /**
     * Нужна ли пре-модерация заявок на участие.
     * Если true, то все заявки будут ожидать подтверждения инициатором события.
     * Если false - то будут подтверждаться автоматически.
     */
    @Column(name = "request_moderation")
    private Boolean requestModeration;

}
