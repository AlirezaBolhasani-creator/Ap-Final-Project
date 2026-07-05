package divar.aut.backend.dto;

import divar.aut.backend.entity.City;

public class CityResponse {
    private Long id;
    private String name;

    public CityResponse(City city) {
        this.id = city.getId();
        this.name = city.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
