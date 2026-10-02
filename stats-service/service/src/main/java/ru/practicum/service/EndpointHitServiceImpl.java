package ru.practicum.service;

import ru.practicum.dao.EndpointHitRepository;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.mapping.EndpointHitMap;
import ru.practicum.model.EndpointHit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class EndpointHitServiceImpl implements EndpointHitService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    @Transactional
    public void addHit(HitDto hitDTO) {
        if (hitDTO == null) {
            throw new ValidationException("Отсутствует структура с добавляемым запросом hitDTO");
        }
        EndpointHit endpointHit = EndpointHitMap.hitDtoToEndpointHit(hitDTO);
        endpointHitRepository.save(endpointHit);
    }

    @Override
    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start == null) {
            throw new ValidationException("Не указана дата начала диапазона поиска");
        }
        if (end == null) {
            throw new ValidationException("Не указана дата окончания диапазона поиска");
        }
        if (start.isAfter(end)) {
            throw new ValidationException("Неверное значение даты старта. Оно позже даты окончания");
        }
        if (uris != null && !uris.isEmpty()) {
            return (unique) ? endpointHitRepository.getCountHitsByDatetimeWithUrisUniqueIP(start, end, uris)
                    : endpointHitRepository.getCountHitsByDatetimeWithUris(start, end, uris);
        } else {
            return (unique) ? endpointHitRepository.getCountHitsByDatetimeWithoutUrisUniqueIP(start, end)
                    : endpointHitRepository.getCountHitsByDatetimeWithoutUris(start, end);
        }
    }
}
