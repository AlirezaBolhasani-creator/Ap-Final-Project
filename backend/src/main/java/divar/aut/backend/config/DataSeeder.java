package divar.aut.backend.config;

import divar.aut.backend.entity.Category;
import divar.aut.backend.entity.City;
import divar.aut.backend.repository.CategoryRepository;
import divar.aut.backend.repository.CityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Seeds basic categories/cities once on startup for a usable fresh DB. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;

    public DataSeeder(CategoryRepository categoryRepository, CityRepository cityRepository) {
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    public void run(String... args) {
        seedCategories();
        seedCities();
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) return;

        categoryRepository.save(new Category("املاک"));
        categoryRepository.save(new Category("وسایل نقلیه"));
        categoryRepository.save(new Category("کالای دیجیتال"));
        categoryRepository.save(new Category("خانه و آشپزخانه"));
        categoryRepository.save(new Category("خدمات"));
        categoryRepository.save(new Category("وسایل شخصی"));
    }

    private void seedCities() {
        if (cityRepository.count() > 0) return;

        cityRepository.save(new City("تهران"));
        cityRepository.save(new City("مشهد"));
        cityRepository.save(new City("اصفهان"));
        cityRepository.save(new City("شیراز"));
        cityRepository.save(new City("تبریز"));
    }
}
