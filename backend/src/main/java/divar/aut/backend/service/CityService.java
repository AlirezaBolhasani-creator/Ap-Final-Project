package divar.aut.backend.service;

import divar.aut.backend.dto.CityResponse;
import divar.aut.backend.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<CityResponse> listAll() {
        return cityRepository.findAllByOrderByNameAsc().stream()
                .map(CityResponse::new)
                .toList();
    }
}
