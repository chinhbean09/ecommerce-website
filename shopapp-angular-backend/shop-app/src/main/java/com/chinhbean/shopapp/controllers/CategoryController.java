package com.chinhbean.shopapp.controllers;

import com.chinhbean.shopapp.components.LocalizationUtils;
import com.chinhbean.shopapp.dtos.CategoryDTO;
import com.chinhbean.shopapp.models.Category;
import com.chinhbean.shopapp.responses.CategoryResponse;
import com.chinhbean.shopapp.responses.UpdateCategoryResponse;
import com.chinhbean.shopapp.services.CategoryService;
import com.chinhbean.shopapp.utils.MessageKeys;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/categories")
//@Validated
//Dependency Inj
@RequiredArgsConstructor

public class CategoryController {
    //Dependency Inj
    private final CategoryService categoryService;
    private final LocaleResolver localeResolver;
    private final MessageSource messageSource;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")

    public ResponseEntity<CategoryResponse> createCategory(
            //data nằm trong reqbody
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result)
    {
        CategoryResponse categoryResponse = new Categor yResponse();
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    //java8, biến field lỗi thành 1 đối tượng stream, trong đó ta có thể duyệt qua danh sách lỗi, và
                    //chỉ lấy 1 lỗi nào đó và ánh xạ sang 1 mảng khác
                    .stream()
                    //ánh xạ sang 1 mảng khác, bên trong là biểu thức lamda của từng trường fielderror
                    .map(FieldError::getDefaultMessage)
                    .toList();
            categoryResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_FAILED));
            categoryResponse.setErrors(errorMessages);
            return ResponseEntity.badRequest().body(categoryResponse);
        }
        Category category = categoryService.createCategory(categoryDTO);
        categoryResponse.setCategory(category);
        return ResponseEntity.ok(categoryResponse);
    }

    //Hiện tất cả các categories
    @GetMapping("") //http://localhost:8080/api/v1/categories?page=1&limit=10
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam("page")     int page,
            @RequestParam("limit")    int limit
    ) {

        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    //Nếu tham số truyền vào là 1 object thì sao ? => Data Transfer Object = Request Object
    @PutMapping("/{id}")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(@PathVariable Long id,
                                                 @Valid
                                                 @RequestBody CategoryDTO categoryDTO) {
        UpdateCategoryResponse updateCategoryResponse = new UpdateCategoryResponse();
        categoryService.updateCategory(id, categoryDTO);
        updateCategoryResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY));
        return ResponseEntity.ok(updateCategoryResponse);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY));
    }
}
