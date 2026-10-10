package ru.practicum.categories.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CategoryControllerImpl.class, PublicCategoryControllerImpl.class})
class CategoryControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    void add_shouldReturn201() throws Exception {
        when(categoryService.addNewCategory(any())).thenReturn(new CategoryDto(1L, "Концерты"));

        mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryDto(null, "Концерты"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Концерты"));
    }

    @Test
    void add_withBlankName_shouldReturn400() throws Exception {
        mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"   \"}"))
                .andExpect(status().isBadRequest());
        verify(categoryService, never()).addNewCategory(any());
    }

    @Test
    void add_withTooLongName_shouldReturn400() throws Exception {
        String name = "a".repeat(51);
        mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryDto(null, name))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_whenNameExists_shouldReturn409() throws Exception {
        when(categoryService.addNewCategory(any())).thenThrow(new ClientErrorException("exists"));

        mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryDto(null, "Концерты"))))
                .andExpect(status().isConflict());
    }

    @Test
    void update_shouldReturn200() throws Exception {
        when(categoryService.updateCategory(eq(1L), any())).thenReturn(new CategoryDto(1L, "Новое"));

        mockMvc.perform(patch("/admin/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryDto(null, "Новое"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новое"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/admin/categories/1"))
                .andExpect(status().isNoContent());
        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void delete_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new NotFoundException("not found")).when(categoryService).deleteCategory(99L);

        mockMvc.perform(delete("/admin/categories/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCategories_shouldUseDefaultFromAndSize() throws Exception {
        when(categoryService.getCategories(0, 10)).thenReturn(List.of(new CategoryDto(1L, "Концерты")));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(categoryService).getCategories(0, 10);
    }

    @Test
    void getCategoryById_whenNotFound_shouldReturn404() throws Exception {
        when(categoryService.getCategoryById(99L)).thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/categories/99"))
                .andExpect(status().isNotFound());
    }
}
