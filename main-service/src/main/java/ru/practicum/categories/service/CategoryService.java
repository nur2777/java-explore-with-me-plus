package ru.practicum.categories.service;

import ru.practicum.categories.dto.CategoryDto;

import java.util.Collection;

public interface CategoryService {

    CategoryDto addNewCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto updateCategory);

    Collection<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);
}
