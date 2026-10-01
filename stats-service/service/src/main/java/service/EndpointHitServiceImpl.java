package service;

import dto.HitDTO;
import dto.UriStatDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EndpointHitServiceImpl implements EndpointHitService {
    @Override
    public void addHit(HitDTO hitDTO) {
        //TODO
    }

    @Override
    public List<UriStatDTO> getStats(String start, String end, ArrayList<String> uris, boolean unique) {
        return List.of();
    }
}
