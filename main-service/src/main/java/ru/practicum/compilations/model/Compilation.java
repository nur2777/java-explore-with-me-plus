package ru.practicum.compilations.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.events.model.Event;

import java.util.HashSet;
import java.util.Set;

/**
 * Модель данных подборки событий
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "compilations")
public class Compilation {

    /**
     * Идентификатор подборки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Заголовок подборки
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Закреплена ли подборка на главной странице сайта
     */
    @Column(name = "pinned", nullable = false)
    private Boolean pinned;

    /**
     * События, входящие в подборку
     */
    @ManyToMany
    @JoinTable(name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events = new HashSet<>();
}
