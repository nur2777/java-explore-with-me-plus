package ru.practicum.controller;

import ru.practicum.dto.HitDTO;
import ru.practicum.dto.UriStatDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.EndpointHitService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.DATE_TIME_PATTERN;

@RestController
@Slf4j
@RequestMapping()
public class EndpointHitControllerImpl implements EndpointHitController {

    private final EndpointHitService endpointHitService;

    @Autowired
    public EndpointHitControllerImpl(EndpointHitService endpointHitService) {
        this.endpointHitService = endpointHitService;
    }

    @Override
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @RequestBody HitDTO newHit) {
        endpointHitService.addHit(newHit);
    }

    @Override
    @GetMapping("/stats")
    public List<UriStatDTO> getStats(@RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime start,
                                     @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime end,
                                     @RequestParam(required = false) List<String> uris,
                                     @RequestParam(defaultValue = "false") boolean unique) {
        return endpointHitService.getStats(start, end, uris, unique);
    }
}
