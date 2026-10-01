package ru.practicum.mapping;

import ru.practicum.dto.HitDTO;
import ru.practicum.model.EndpointHit;

public class EndpointHitMap {

    public static EndpointHit HitDtoToEndpointHit(HitDTO hitDTO) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(hitDTO.getApp());
        endpointHit.setUri(hitDTO.getUri());
        endpointHit.setIp(hitDTO.getIp());
        endpointHit.setHitDatetime(hitDTO.getTimestamp());
        return endpointHit;
    }
}
