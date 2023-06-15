package com.todorest.endpoint;

import com.todorest.dto.CategoryDto;
import com.todorest.dto.CreateCategoryRequestDto;
import com.todorest.entity.Category;
import com.todorest.mapper.CategoryMapper;
import com.todorest.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * CategoryEndpoint (իրան մենակ ադմինը կրա դիմե)
 * /category {POST} - սարքել կատեգորիա
 * /category {GET} - վերադարձնել բոլոր կատեգորիաները
 * /category/{id} {DELETE} - ջնջել կատեգորիան իդ-ով
 */

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryEndpoint {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @PostMapping
    public ResponseEntity<CreateCategoryRequestDto> createCategory(@RequestBody CreateCategoryRequestDto category){
        categoryRepository.save(categoryMapper.map(category));
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> returnAllCategories(){
        List<Category> all = categoryRepository.findAll();
        if (all.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : all) {
            categoryDtos.add(categoryMapper.mapToDto(category));
        }

        return ResponseEntity.ok(categoryDtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable("id") int id){
        if(categoryRepository.existsById(id)){
            categoryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
