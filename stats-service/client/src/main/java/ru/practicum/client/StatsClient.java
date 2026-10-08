package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.constants.Constants;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatsRequestDto;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class StatsClient {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN);

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClient(@Value("${stats-server.url}") String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    /**
     * Сохранить информацию о запросе к эндпоинту (POST /hit)
     */
    public void hit(HitDto hitDto) {
        try {
            restTemplate.postForEntity(serverUrl + "/hit", hitDto, Void.class);
        } catch (RestClientException e) {
            log.warn("Не удалось сохранить хит в сервис статистики: {}", e.getMessage());
        }
    }

    /**
     * Получить статистику по посещениям (GET /stats)
     */
    public List<StatDto> getStats(StatsRequestDto request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(serverUrl)
                .path("/stats")
                .queryParam("start", request.getStart().format(FORMATTER))
                .queryParam("end", request.getEnd().format(FORMATTER))
                .queryParam("unique", request.isUnique());

        if (request.getUris() != null && !request.getUris().isEmpty()) {
            builder.queryParam("uris", request.getUris().toArray());
        }

        URI uri = builder.build().encode().toUri();

        try {
            StatDto[] response = restTemplate.getForObject(uri, StatDto[].class);
            return response == null ? List.of() : Arrays.asList(response);
        } catch (RestClientException e) {
            log.warn("Не удалось получить статистику: {}", e.getMessage());
            return List.of();
        }
    }
}