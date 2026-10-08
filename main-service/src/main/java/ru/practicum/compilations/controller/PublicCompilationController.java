package ru.practicum.compilations.controller;

import ru.practicum.compilations.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationController {

    /**
     * Эндпоинт получения подборок событий
     * @param pinned искать только закрепленные/не закрепленные подборки
     * @param from количество элементов, которые нужно пропустить
     * @param size количество элементов в наборе
     * @return список подборок
     */
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    /**
     * Эндпоинт получения подборки событий по id
     * @param compId идентификатор подборки
     * @return подборка
     */
    CompilationDto getCompilationById(Long compId);
}
