package kr.ac.hansung.cse.service;

import java.util.List;
import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

// 카테고리 관련 비지니스 로직을 처리하는 서비스 클래스
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // 모든 카테고리 목록 조회하는 getAllCategories()메서드
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    // 새로운 카테고리 등록하는 createCategory()메서드
    @Transactional
    public Category createCategory(String name) {
        categoryRepository.findByName(name)
                .ifPresent(c -> {
                    throw new DuplicateCategoryException(name);
                });
        return categoryRepository.save(new Category(name));
    }

    // 카테고리를 삭제하는 deleteCategory()메서드
    @Transactional
    public void deleteCategory(Long id) {
        long count = categoryRepository.countProductsByCategoryId(id);
        if (count > 0) throw new IllegalStateException(
                "상품 " + count + "개가 연결되어 있어 삭제할 수 없습니다.");
        categoryRepository.delete(id);
    }
}
