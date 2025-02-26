package com.gearvn.admin.category;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.gearvn.admin.common.UploadImageService;
import com.gearvn.common.entity.Category;

import jakarta.servlet.http.HttpServletResponse;
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
	public String getCategoryFirstPage(Model model) {
		return getCategoryPage_pageable(1, model, "asc", null);
	}

	@GetMapping("/categories/page/{pageNumber}")
	public String getCategoryPage_pageable(@PathVariable("pageNumber") int pageNumber, Model model,
			@RequestParam("sortType") String sortType,
			@RequestParam(name = "keyword", required = false) String keyword) {

		// lấy totalPages, totalElements
		CategoryPageInfo categoryPageInfo = new CategoryPageInfo();
		List<Category> categories = this.categoryService.getAllCategories_pageable(sortType, pageNumber,
				categoryPageInfo, keyword);

		int totalPages = categoryPageInfo.getTotalPages();
		long totalElements = categoryPageInfo.getTotalElements();

		long startCount = (pageNumber - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
		if (endCount > totalElements) {
			endCount = totalElements;
		}

		long startPage = (pageNumber - 2 < 1) ? 1 : pageNumber - 2;
		long endPage = (pageNumber + 2 > totalPages) ? totalPages : pageNumber + 2;

		model.addAttribute("listCategories", categories);

		// pagination
		model.addAttribute("totalElements", totalElements);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);

		// sortType
		String reverseSortType = sortType.equals("asc") ? "desc" : "asc";
		// mặc dù ko cần dùng bên list_categories nhưng vẫn truyền để refactor code (tận dụng fragments)
		model.addAttribute("sortField", "name");
		model.addAttribute("sortType", sortType);
		model.addAttribute("reverseSortType", reverseSortType);

		// filter
		model.addAttribute("keyword", keyword);

		return "categories/list_categories";
	}

	@GetMapping("/categories/create")
	public String getCreateCategoryPage(Model model) {
		List<Category> hierarchicalCategories = this.categoryService.getAllCategories("asc");

		model.addAttribute("category", new Category());
		model.addAttribute("hierarchicalCategories", hierarchicalCategories);

		return "categories/create_category";
	}

	@PostMapping("/categories/save")
	public String handleSaveCategory(Model model, @Valid Category category, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, @RequestParam("multipartFile") MultipartFile multipartFile) {
		// validation
		if (bindingResult.hasErrors()) {
			List<Category> hierarchicalCategories = this.categoryService.getAllCategories("asc");
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
			List<Category> hierarchicalCategories = this.categoryService.getAllCategories("asc");

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
			List<Category> hierarchicalCategories = this.categoryService.getAllCategories("asc");
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
	
	@GetMapping("/categories/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Category> listCategories = this.categoryService.getAllCategories("asc");
		CategoryCsvExporter exporter = new CategoryCsvExporter();
		exporter.export(listCategories, response);
	}
}
