package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dao.CategoryRepository;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.mapping.CategoryMap;
import ru.practicum.categories.model.Category;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.exception.NotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.Collection;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto addNewCategory(CategoryDto categoryDto) {
        Category newCategory = CategoryMap.categoryDtoToCategory(categoryDto);

        if (categoryRepository.existsByName(newCategory.getName())) {
            throw new ClientErrorException(String.format(
                    "Категория с name '%s' уже существует. Создание категории с одинаковым name запрещено!",
                    newCategory.getName()));
        }

        return CategoryMap.categoryToCategoryDto(categoryRepository.save(newCategory));
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Категория с id " + catId + " не найдена");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto updateCategory) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id " + catId + " не найдена"));

        if (categoryRepository.existsByNameAndIdNot(updateCategory.getName(), catId)) {
            throw new ClientErrorException(String.format(
                    "Категория с name '%s' уже существует", updateCategory.getName()));
        }

        category.setName(updateCategory.getName());
        return CategoryMap.categoryToCategoryDto(categoryRepository.save(category));
    }

    @Override
    public Collection<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).getContent().stream()
                .map(CategoryMap::categoryToCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id " + catId + " не найдена"));
        return CategoryMap.categoryToCategoryDto(category);
    }
}
