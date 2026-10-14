package ru.practicum.categories.controller;

import ru.practicum.categories.dto.CategoryDto;

public interface CategoryController {

    /**
     * Эндпоинт на добавление новой категории
     * @param newCategory новая категория
     * @return объект созданной категории
     */
    CategoryDto add(CategoryDto newCategory);

    /** Эндпоинт удаления категории
     * @param catId идентфикатор категории
     */
    void delete(Long catId);

    /** Эндпоинт изменения категории
     * @param catId идентфикатор категории
     * @param updateCategory новая категория
     */
    CategoryDto update(Long catId, CategoryDto updateCategory);
}
