package kr.ac.hansung.cse.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import kr.ac.hansung.cse.model.CategoryForm;
import kr.ac.hansung.cse.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 카테고리 관리와 관련된 요청을 처리하는 컨트롤러
@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    // 카테고리 목록 조회
    @GetMapping // GET /categories → 목록
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categoryList"; }

    // 카테고리 등록
    @GetMapping("/create") // GET → 등록 폼 표시
    public String showCreateForm(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        return "categoryForm"; }

    // 카테고리 등록 처리
    @PostMapping("/create") // POST → 등록 처리
    public String createCategory(
            // 입력 데이터 유효성 검증
            @Valid @ModelAttribute CategoryForm categoryForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) return "categoryForm"; // 검증 실패

        try {
            // 서비스 계층을 호출하여 실제 카테고리 저장 로직 수행
            categoryService.createCategory(categoryForm.getName());
            redirectAttributes.addFlashAttribute("successMessage", "등록 완료");
        } catch (DuplicateCategoryException e) {
            // 중복 예외 → BindingResult에 필드 오류 등록 후 폼 재표시
            bindingResult.rejectValue("name", "duplicate", e.getMessage());
            return "categoryForm"; }
        // 모든 처리가 완료되면 목록 페이지로 리다이렉트
        return "redirect:/categories"; }

    // 카테고리 삭제 처리
    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            // 서비스 계층을 호출아혀 카테고리 삭제
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "삭제 완료");
        } catch (IllegalStateException e) {
            // 연결된 상품 있을 때 → Flash로 오류 메시지 전달
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        // 목록 페이지로 돌아가서 메시지 표시
        return "redirect:/categories";
    }
}