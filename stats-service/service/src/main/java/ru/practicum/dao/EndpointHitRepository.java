package ru.practicum.dao;

import ru.practicum.dto.UriStatDTO;
import ru.practicum.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit,Long> {

    @Query("select new ru.practicum.dto.UriStatDTO(app,uri,count(eh.id)) " +
             "from EndpointHit as eh " +
            "where eh.hitDatetime between ?1 AND ?2 " +
            "group by eh.app, eh.uri " +
            "order by count(eh.id) desc"
          )
    List<UriStatDTO> getCountHitsByDatetimeWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.UriStatDTO(app,uri,count(distinct eh.ip)) " +
            "from EndpointHit as eh " +
            "where eh.hitDatetime between ?1 AND ?2 " +
            "group by eh.app, eh.uri " +
            "order by count(distinct eh.ip) desc"
    )
    List<UriStatDTO> getCountHitsByDatetimeWithoutUrisUniqueIP(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.UriStatDTO(app,uri,count(eh.id)) " +
            "from EndpointHit as eh " +
            "where eh.hitDatetime between ?1 AND ?2 " +
            "and eh.uri IN ?3 " +
            "group by eh.app, eh.uri " +
            "order by count(eh.id) desc"
    )
    List<UriStatDTO> getCountHitsByDatetimeWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.UriStatDTO(app,uri,count(distinct eh.ip)) " +
            "from EndpointHit as eh " +
            "where eh.hitDatetime between ?1 AND ?2 " +
            "and eh.uri IN ?3 " +
            "group by eh.app, eh.uri " +
            "order by count(distinct eh.ip) desc"
    )
    List<UriStatDTO> getCountHitsByDatetimeWithUrisUniqueIP(LocalDateTime start, LocalDateTime end, List<String> uris);
}
