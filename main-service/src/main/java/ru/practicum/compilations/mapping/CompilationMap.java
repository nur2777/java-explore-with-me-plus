package ru.practicum.compilations.mapping;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.mapping.EventsMap;

import java.util.stream.Collectors;

public class CompilationMap {

    public static CompilationDto compilationToCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream()
                        .map(EventsMap::eventShortDtoFromEvent)
                        .collect(Collectors.toSet()))
                .build();
    }
}
