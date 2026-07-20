package divar.aut.backend.entity;

import jakarta.persistence.*;

/**
 * Represents a city where advertisements can be listed.
 * Cities are predefined by administrators and used to localize ads.
 * The name must be unique across all cities.
 */
@Entity
@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    protected City() {
    }

    public City(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
