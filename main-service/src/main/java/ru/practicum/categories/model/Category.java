package ru.practicum.categories.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Модель данных категории
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "categories")
public class Category {

    /**
     * Идентификатор категории
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Имя категории
     */
    private String name;
}
