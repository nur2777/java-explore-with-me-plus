package ru.practicum.compilations.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationDto;
import ru.practicum.compilations.service.CompilationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationControllerImpl implements AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addNewCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.addNewCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    @Override
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                  @Valid @RequestBody UpdateCompilationDto updateCompilationDto) {
        return compilationService.updateCompilation(compId,updateCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }
}
