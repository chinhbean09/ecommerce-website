package com.chinhbean.shopapp.services.category;

import com.chinhbean.shopapp.dtos.CategoryDTO;
import com.chinhbean.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO category);
    Category getCategoryById(long id);
    List<Category> getAllCategories();
    Category updateCategory(long categoryId, CategoryDTO category);
    Category deleteCategory(long id) throws Exception;
}
