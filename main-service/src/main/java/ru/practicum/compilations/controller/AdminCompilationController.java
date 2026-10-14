package ru.practicum.compilations.controller;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationDto;

public interface AdminCompilationController {

    /**
     * Эндпоинт добавления новой подборки событий
     * @param newCompilationDto  данные о новой подборке
     * @return объект подборки
     */
    CompilationDto addNewCompilation(NewCompilationDto newCompilationDto);

    /**
     * Эндпоинт обновления  подборки событий
     * @param updateCompilationDto  данные о новой подборке
     * @return объект подборки
     */
    CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto);

    /**
     * Эндпоинт удаления подборки событий
     * @param compId идентификатор подборки
     */
    void deleteCompilation(Long compId);
}
