package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dao.CompilationRepository;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.mapping.CompilationMap;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<Compilation> compilations = pinned == null
                ? compilationRepository.findAll(pageable).getContent()
                : compilationRepository.findAllByPinned(pinned, pageable);

        return compilations.stream()
                .map(CompilationMap::compilationToCompilationDto)
                .toList();
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id " + compId + " не найдена"));
        return CompilationMap.compilationToCompilationDto(compilation);
    }
}
