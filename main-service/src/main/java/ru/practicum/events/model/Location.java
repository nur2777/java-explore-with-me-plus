package ru.practicum.events.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * Модель для географических координат места проведения события
 */
@Embeddable
@Getter
@Setter
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
