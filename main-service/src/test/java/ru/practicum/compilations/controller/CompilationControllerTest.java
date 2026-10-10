package ru.practicum.compilations.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.CompilationService;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PublicCompilationControllerImpl.class, AdminCompilationControllerImpl.class})
class CompilationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompilationService compilationService;

    private CompilationDto dto(Long id, boolean pinned) {
        return CompilationDto.builder()
                .id(id)
                .title("Подборка " + id)
                .pinned(pinned)
                .events(Set.of())
                .build();
    }

    @Test
    void getCompilations_shouldUseDefaults() throws Exception {
        when(compilationService.getCompilations(null, 0, 10)).thenReturn(List.of(dto(1L, true)));

        mockMvc.perform(get("/compilations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Подборка 1"));
        verify(compilationService).getCompilations(null, 0, 10);
    }

    @Test
    void getCompilations_shouldPassPinnedFromAndSize() throws Exception {
        when(compilationService.getCompilations(true, 5, 5)).thenReturn(List.of());

        mockMvc.perform(get("/compilations")
                        .param("pinned", "true")
                        .param("from", "5")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(compilationService).getCompilations(true, 5, 5);
    }

    @Test
    void getCompilationById_shouldReturn200() throws Exception {
        when(compilationService.getCompilationById(1L)).thenReturn(dto(1L, false));

        mockMvc.perform(get("/compilations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.pinned").value(false))
                .andExpect(jsonPath("$.events").isArray());
    }

    @Test
    void getCompilationById_whenNotFound_shouldReturn404() throws Exception {
        when(compilationService.getCompilationById(99L)).thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/compilations/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCompilation_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/admin/compilations/1"))
                .andExpect(status().isNoContent());
        verify(compilationService).deleteCompilation(1L);
    }

    @Test
    void deleteCompilation_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new NotFoundException("not found")).when(compilationService).deleteCompilation(99L);

        mockMvc.perform(delete("/admin/compilations/99"))
                .andExpect(status().isNotFound());
    }
}
