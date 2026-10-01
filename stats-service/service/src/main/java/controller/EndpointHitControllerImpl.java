package controller;

import dto.HitDTO;
import dto.UriStatDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import service.EndpointHitService;

import java.util.ArrayList;
import java.util.List;

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
    public List<UriStatDTO> getStats(String start, String end, ArrayList<String> uris, boolean unique) {
        return endpointHitService.getStats(start, end, uris, unique);
    }
}
