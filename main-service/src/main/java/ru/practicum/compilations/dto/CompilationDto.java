package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.dto.EventShortDto;

import java.util.Set;

/**
 * DTO подборки событий
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {

    /**
     * Идентификатор подборки
     */
    private Long id;

    /**
     * Закреплена ли подборка на главной странице сайта
     */
    private Boolean pinned;

    /**
     * Заголовок подборки
     */
    private String title;

    /**
     * Список событий, входящих в подборку
     */
    private Set<EventShortDto> events;
}
