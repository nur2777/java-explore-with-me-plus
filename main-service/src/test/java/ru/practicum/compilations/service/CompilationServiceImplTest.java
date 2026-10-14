package ru.practicum.compilations.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.categories.model.Category;
import ru.practicum.compilations.dao.CompilationRepository;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompilationServiceImplTest {

    @Mock
    private CompilationRepository compilationRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CompilationServiceImpl compilationService;

    private Compilation compilation(Long id, boolean pinned) {
        Compilation compilation = new Compilation();
        compilation.setId(id);
        compilation.setTitle("Подборка " + id);
        compilation.setPinned(pinned);
        compilation.setEvents(new HashSet<>());
        return compilation;
    }

    private Event event() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Концерты");

        User initiator = new User();
        initiator.setId(1L);
        initiator.setName("Даша");

        Event event = new Event();
        event.setId(10L);
        event.setTitle("Концерт");
        event.setAnnotation("Краткое описание");
        event.setPaid(true);
        event.setEventDate(LocalDateTime.now().plusDays(5));
        event.setCategory(category);
        event.setInitiator(initiator);
        return event;
    }

    @Test
    void getCompilations_withoutPinned_shouldReturnAll() {
        when(compilationRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(compilation(1L, true), compilation(2L, false))));

        List<CompilationDto> result = compilationService.getCompilations(null, 0, 10);

        assertEquals(2, result.size());
        verify(compilationRepository, never()).findAllByPinned(anyBoolean(), any());
    }

    @Test
    void getCompilations_withPinned_shouldFilter() {
        when(compilationRepository.findAllByPinned(eq(true), any(Pageable.class)))
                .thenReturn(List.of(compilation(1L, true)));

        List<CompilationDto> result = compilationService.getCompilations(true, 0, 10);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getPinned());
        verify(compilationRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getCompilations_whenEmpty_shouldReturnEmptyList() {
        when(compilationRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        assertTrue(compilationService.getCompilations(null, 0, 10).isEmpty());
    }

    @Test
    void getCompilationById_shouldReturnDtoWithEvents() {
        Compilation compilation = compilation(1L, true);
        compilation.setEvents(new HashSet<>(Set.of(event())));
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));

        CompilationDto result = compilationService.getCompilationById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Подборка 1", result.getTitle());
        assertEquals(1, result.getEvents().size());
        EventShortDto eventDto = result.getEvents().iterator().next();
        assertEquals(10L, eventDto.getId());
        assertEquals("Концерты", eventDto.getCategory().getName());
        assertEquals("Даша", eventDto.getInitiator().getName());
    }

    @Test
    void getCompilationById_whenNotFound_shouldThrowNotFound() {
        when(compilationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> compilationService.getCompilationById(99L));
    }

    @Test
    void deleteCompilation_shouldDelete() {
        when(compilationRepository.existsById(1L)).thenReturn(true);

        compilationService.deleteCompilation(1L);

        verify(compilationRepository).deleteById(1L);
    }

    @Test
    void deleteCompilation_whenNotFound_shouldThrowNotFound() {
        when(compilationRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> compilationService.deleteCompilation(99L));
        verify(compilationRepository, never()).deleteById(any());
    }
}
