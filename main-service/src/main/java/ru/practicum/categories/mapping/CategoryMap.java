package ru.practicum.categories.mapping;

import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;

public class CategoryMap {

    public static Category categoryDtoToCategory(CategoryDto categoryDto) {
        Category category = new Category();

        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }

        return category;
    }

    public static CategoryDto categoryToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
