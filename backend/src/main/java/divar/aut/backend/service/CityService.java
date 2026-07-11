package divar.aut.backend.service;

import divar.aut.backend.dto.CityRequest;
import divar.aut.backend.dto.CityResponse;
import divar.aut.backend.entity.City;
import divar.aut.backend.exception.ApiException;
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
        return cityRepository.findAllByOrderByNameAsc().stream().map(CityResponse::new).toList();
    }

    public CityResponse create(CityRequest request) {
        String name = request.getName().trim();
        if (cityRepository.existsByNameIgnoreCase(name)) {
            throw ApiException.badRequest("City already exists");
        }
        return new CityResponse(cityRepository.save(new City(name)));
    }

    public CityResponse update(Long id, CityRequest request) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("City not found"));
        String name = request.getName().trim();
        if (!city.getName().equalsIgnoreCase(name) && cityRepository.existsByNameIgnoreCase(name)) {
            throw ApiException.badRequest("City already exists");
        }
        city.setName(name);
        return new CityResponse(cityRepository.save(city));
    }

}
