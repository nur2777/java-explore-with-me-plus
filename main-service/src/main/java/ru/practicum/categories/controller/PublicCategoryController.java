package ru.practicum.categories.controller;

import ru.practicum.categories.dto.CategoryDto;

import java.util.Collection;

public interface PublicCategoryController {

    Collection<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);
}
