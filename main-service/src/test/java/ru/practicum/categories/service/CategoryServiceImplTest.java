package ru.practicum.categories.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.categories.dao.CategoryRepository;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.model.Event;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

    @Test
    void addNewCategory_shouldSaveAndReturnDto() {
        when(categoryRepository.existsByName("Концерты")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category(1L, "Концерты"));

        CategoryDto result = categoryService.addNewCategory(new CategoryDto(null, "Концерты"));

        assertEquals(1L, result.getId());
        assertEquals("Концерты", result.getName());
    }

    @Test
    void addNewCategory_whenNameExists_shouldThrowConflict() {
        when(categoryRepository.existsByName("Концерты")).thenReturn(true);

        assertThrows(ClientErrorException.class,
                () -> categoryService.addNewCategory(new CategoryDto(null, "Концерты")));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_shouldChangeName() {
        Category existing = category(1L, "Старое");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNameAndIdNot("Новое", 1L)).thenReturn(false);
        when(categoryRepository.save(existing)).thenReturn(existing);

        CategoryDto result = categoryService.updateCategory(1L, new CategoryDto(null, "Новое"));

        assertEquals(1L, result.getId());
        assertEquals("Новое", result.getName());
    }

    @Test
    void updateCategory_whenNotFound_shouldThrowNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> categoryService.updateCategory(99L, new CategoryDto(null, "Новое")));
    }

    @Test
    void updateCategory_whenNameTakenByAnother_shouldThrowConflict() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, "Старое")));
        when(categoryRepository.existsByNameAndIdNot("Занятое", 1L)).thenReturn(true);

        assertThrows(ClientErrorException.class,
                () -> categoryService.updateCategory(1L, new CategoryDto(null, "Занятое")));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_shouldDelete() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(eventRepository.findByCategoryId(1L)).thenReturn(new ArrayList<>());

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_whenNotFound_shouldThrowNotFound() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(99L));
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void deleteCategory_whenHasEvents_shouldThrowConflict() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(eventRepository.findByCategoryId(1L)).thenReturn(List.of(new Event()));

        assertThrows(ClientErrorException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void getCategories_shouldReturnPage() {
        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(category(1L, "А"), category(2L, "Б"))));

        Collection<CategoryDto> result = categoryService.getCategories(0, 10);

        assertEquals(2, result.size());
    }

    @Test
    void getCategories_whenEmpty_shouldReturnEmptyList() {
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        assertTrue(categoryService.getCategories(0, 10).isEmpty());
    }

    @Test
    void getCategoryById_shouldReturnDto() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, "Концерты")));

        CategoryDto result = categoryService.getCategoryById(1L);

        assertEquals("Концерты", result.getName());
    }

    @Test
    void getCategoryById_whenNotFound_shouldThrowNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(99L));
    }
}
