package ru.practicum.compilations.service;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    /**
     * Добавление новой подборки событий
     * @param newCompilationDto  данные о новой подборке
     * @return объект подборки
     */
    CompilationDto addNewCompilation(NewCompilationDto newCompilationDto);

    /**
     * Получение подборок событий
     * @param pinned искать только закрепленные/не закрепленные подборки (null - все)
     * @param from количество элементов, которые нужно пропустить
     * @param size количество элементов в наборе
     * @return список подборок
     */
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    /**
     * Получение подборки событий по id
     * @param compId идентификатор подборки
     * @return подборка
     */
    CompilationDto getCompilationById(Long compId);
}
