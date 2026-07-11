package divar.aut.backend.service;

import divar.aut.backend.dto.CategoryRequest;
import divar.aut.backend.dto.CategoryResponse;
import divar.aut.backend.entity.Category;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> listAll() {
        return categoryRepository.findAllByOrderByNameAsc().stream().map(CategoryResponse::new).toList();
    }

    public CategoryResponse create(CategoryRequest request) {
        String name = request.getName().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw ApiException.badRequest("Category already exists");
        }
        return new CategoryResponse(categoryRepository.save(new Category(name)));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Category not found"));
        String name = request.getName().trim();
        if (!category.getName().equalsIgnoreCase(name) && categoryRepository.existsByNameIgnoreCase(name)) {
            throw ApiException.badRequest("Category already exists");
        }
        category.setName(name);
        return new CategoryResponse(categoryRepository.save(category));
    }

}
