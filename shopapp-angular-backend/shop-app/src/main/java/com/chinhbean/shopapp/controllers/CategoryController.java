package com.chinhbean.shopapp.controllers;

import com.chinhbean.shopapp.dtos.CategoryDTO;
import com.chinhbean.shopapp.models.Category;
import com.chinhbean.shopapp.services.CategoryService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("")

    public ResponseEntity<?> createCategory(
            //data nằm trong reqbody
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result)
    {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    //java8, biến field lỗi thành 1 đối tượng stream, trong đó ta có thể duyệt qua danh sách lỗi, và
                    //chỉ lấy 1 lỗi nào đó và ánh xạ sang 1 mảng khác
                    .stream()
                    //ánh xạ sang 1 mảng khác, bên trong là biểu thức lamda của từng trường fielderror
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        categoryService.createCategory(categoryDTO);

        return ResponseEntity.ok("Insert category successfully");

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
    @PostMapping("")
    //Nếu tham số truyền vào là 1 object thì sao ? => Data Transfer Object = Request Object
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id,
                                                 @Valid
                                                 @RequestBody CategoryDTO categoryDTO) {
        categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok("Update category successfully");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Delete category successfully");
    }
}
