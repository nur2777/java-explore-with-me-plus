package ru.practicum.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EndpointHitServiceImpl implements EndpointHitService {
    @Override
    public void addHit(HitDto hitDTO) {
        //TODO
    }

    @Override
    public List<StatDto> getStats(String start, String end, ArrayList<String> uris, boolean unique) {
        return List.of();
    }
}
