package ru.practicum.events.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Модель для географических координат места проведения события
 */
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    /**
     * Широта места проведения события
     */
    private Float lat;

    /**
     * Долгота места проведения события
     */
    private Float lon;
}
