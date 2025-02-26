package com.gearvn.admin.category;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.gearvn.admin.common.UploadImageService;
import com.gearvn.common.entity.Category;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CategoryController {
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UploadImageService uploadImageService;

	@GetMapping("/categories")
	public String getCategoryPage(Model model, @Param("sortType") String sortType) {
		if (StringUtils.isEmpty(sortType)) {
			sortType = "asc";
		} else {
			sortType = sortType.equals("asc") ? "desc" : "asc";
		}

		List<Category> categories = this.categoryService.getAllCategories(sortType);

		model.addAttribute("reverseSortType", sortType);
		model.addAttribute("listCategories", categories);

		return "categories/list_categories";
	}

	@GetMapping("/categories/create")
	public String getCreateCategoryPage(Model model) {
		List<Category> hierarchicalCategories = this.categoryService.getHierarchicalCategories("asc");

		model.addAttribute("category", new Category());
		model.addAttribute("hierarchicalCategories", hierarchicalCategories);

		return "categories/create_category";
	}

	@PostMapping("/categories/save")
	public String handleSaveCategory(Model model, @Valid Category category, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, @RequestParam("multipartFile") MultipartFile multipartFile) {
		// validation
		if (bindingResult.hasErrors()) {
			List<Category> hierarchicalCategories = this.categoryService.getHierarchicalCategories("asc");
			model.addAttribute("hierarchicalCategories", hierarchicalCategories);

			return "categories/create_category";
		}

		if (!multipartFile.isEmpty()) {
			String targetFolder = "../category-images";
			String fileName = this.uploadImageService.handleSaveUploadFile(multipartFile, targetFolder);
			category.setImage(fileName);
		}

		this.categoryService.handleSaveCategory(category);

		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully.");
		return "redirect:/categories";
	}

	@GetMapping("/categories/update/{id}")
	public String getUpdateCategoryPage(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Category category = this.categoryService.getCategoryById(id);
			List<Category> hierarchicalCategories = this.categoryService.getHierarchicalCategories("asc");

			model.addAttribute("hierarchicalCategories", hierarchicalCategories);
			model.addAttribute("category", category);

			return "categories/update_category";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Could not find any category with id " + id);
			return "redirect:/categories";
		}
	}

	@PostMapping("/categories/update")
	public String handleUpdateCategories(Model model, @Valid Category category, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, @RequestParam("multipartFile") MultipartFile multipartFile)
			throws Exception {
		// validation
		if (bindingResult.hasErrors()) {
			List<Category> hierarchicalCategories = this.categoryService.getHierarchicalCategories("asc");
			model.addAttribute("hierarchicalCategories", hierarchicalCategories);

			return "categories/update_category";
		}

		if (!multipartFile.isEmpty()) {
			String targetFolder = "../category-images";
			// xóa ảnh cũ trước khi thêm ảnh mới, đảm bảo ảnh tồn tại mới xóa
			if (!StringUtils.isEmpty(category.getImage())) {
				this.uploadImageService.deletePhotos(targetFolder, category.getImage());
			}
			String fileName = this.uploadImageService.handleSaveUploadFile(multipartFile, targetFolder);
			category.setImage(fileName);
		}

		this.categoryService.handleSaveCategory(category);

		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully.");

		return "redirect:/categories";
	}

	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			String targetFolder = "../category-images";
			Category category = this.categoryService.getCategoryById(id);
			String imageName = category.getImage();
			this.categoryService.deleteCategoryById(id);
			/*
			 * nên để xóa ảnh sau vì trường hợp xóa category thất bại thì ném ngoại lệ trc
			 * thay vì xóa ảnh
			 */
			this.uploadImageService.deletePhotos(targetFolder, imageName);

			redirectAttributes.addFlashAttribute("message",
					"The category ID " + id + " has been deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Could not find any category with id " + id);
		}

		return "redirect:/categories";
	}
}
