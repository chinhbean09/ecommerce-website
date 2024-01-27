package com.chinhbean.shopapp.services.category;

import com.chinhbean.shopapp.dtos.CategoryDTO;
import com.chinhbean.shopapp.models.Category;
import com.chinhbean.shopapp.models.Product;
import com.chinhbean.shopapp.repositories.CategoryRepository;
import com.chinhbean.shopapp.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    //dependency inject
    //injection thông qua constructor là một cách để cung cấp đối tượng CategoryRepository cho CategoryService mà không cần phải tạo nó trong CategoryService.
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

//    public CategoryService (CategoryRepository categoryRepository){
//        this.categoryRepository = categoryRepository;
//    }


    @Override
    @Transactional

    public Category createCategory(CategoryDTO categoryDTO) {
        //builder.build có nghĩa là tạo ra 1 đối tượng rỗng sau đó khởi tạo từng thành phần
        Category newCategory = Category.builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional

    public Category updateCategory(long categoryId, CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existingCategory);
        return existingCategory;
    }

    @Override
    @Transactional
    public Category deleteCategory(long id) throws Exception {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        List<Product> products = productRepository.findByCategory(category);
        if (!products.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated products");
        } else {
            categoryRepository.deleteById(id);
            return  category;
        }
    }
}
