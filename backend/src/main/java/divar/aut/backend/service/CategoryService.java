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
        Category category = new Category(name);

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> ApiException.notFound("Parent category not found"));
            category.setParent(parent);
        }

        return new CategoryResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Category not found"));
        String name = request.getName().trim();

        if (!category.getName().equalsIgnoreCase(name) && categoryRepository.existsByNameIgnoreCase(name)) {
            throw ApiException.badRequest("Category already exists");
        }
        category.setName(name);

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw ApiException.badRequest("A category cannot be its own parent");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> ApiException.notFound("Parent category not found"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return new CategoryResponse(categoryRepository.save(category));
    }

}
