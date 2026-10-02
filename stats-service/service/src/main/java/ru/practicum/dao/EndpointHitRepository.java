package ru.practicum.dao;

import ru.practicum.dto.StatDto;
import ru.practicum.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit,Long> {

    @Query("select new ru.practicum.dto.StatDto(app,uri,count(eh.id)) " +
             "from EndpointHit as eh " +
            "where eh.hitDatetime between ?1 AND ?2 " +
            "group by eh.app, eh.uri " +
            "order by count(eh.id) desc"
          )
    List<StatDto> getCountHitsByDatetimeWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatDto(app,uri,count(distinct eh.ip)) " +
            "from EndpointHit as eh " +
            "where eh.hitDatetime between ?1 AND ?2 " +
            "group by eh.app, eh.uri " +
            "order by count(distinct eh.ip) desc"
    )
    List<StatDto> getCountHitsByDatetimeWithoutUrisUniqueIP(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatDto(app,uri,count(eh.id)) " +
            "from EndpointHit as eh " +
            "where eh.hitDatetime between ?1 AND ?2 " +
            "and eh.uri IN ?3 " +
            "group by eh.app, eh.uri " +
            "order by count(eh.id) desc"
    )
    List<StatDto> getCountHitsByDatetimeWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.StatDto(app,uri,count(distinct eh.ip)) " +
            "from EndpointHit as eh " +
            "where eh.hitDatetime between ?1 AND ?2 " +
            "and eh.uri IN ?3 " +
            "group by eh.app, eh.uri " +
            "order by count(distinct eh.ip) desc"
    )
    List<StatDto> getCountHitsByDatetimeWithUrisUniqueIP(LocalDateTime start, LocalDateTime end, List<String> uris);
}
