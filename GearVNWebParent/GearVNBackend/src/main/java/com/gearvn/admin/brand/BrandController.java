package com.gearvn.admin.brand;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gearvn.admin.category.CategoryService;
import com.gearvn.admin.common.UploadImageService;
import com.gearvn.admin.paging.PagingAndSortingHelper;
import com.gearvn.admin.paging.PagingAndSortingParam;
import com.gearvn.common.entity.Brand;
import com.gearvn.common.entity.Category;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class BrandController {
	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UploadImageService uploadImageService;

	@GetMapping("/brands")
	public String getBrandsFirstPage(Model model) {
		return "redirect:/brands/page/1?sortField=name&sortType=asc";
	}

	@GetMapping("/brands/page/{currentPage}")
	public String getBrandPage_pageable(@PathVariable("currentPage") int currentPage,
			@PagingAndSortingParam(listName = "listBrands", moduleUrl = "/brands") PagingAndSortingHelper helper) {

		this.brandService.getAllBrands(currentPage, helper);

		return "brands/list_brands";
	}

	@GetMapping("/brands/create")
	public String getCreateBrandPage(Model model) {
		List<Category> categories = this.categoryService.getAllCategories("asc");

		model.addAttribute("brand", new Brand());
		model.addAttribute("categories", categories);

		return "brands/create_brand";
	}

	@PostMapping("/brands/save")
	public String handleSaveBrand(Model model, @Valid Brand brand, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, @RequestParam("multipartFile") MultipartFile multipartFile) {
		// validation
		if (bindingResult.hasErrors()) {
			List<Category> categories = this.categoryService.getAllCategories("asc");
			model.addAttribute("categories", categories);

			return "brands/create_brand";
		}

		if (!multipartFile.isEmpty()) {
			String targetFolder = "../brand-logos";
			String fileName = this.uploadImageService.handleSaveUploadFile(multipartFile, targetFolder);
			brand.setLogo(fileName);
		}

		this.brandService.handleSaveBrand(brand);

		redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully.");
		return "redirect:/brands";
	}

	@GetMapping("/brands/update/{id}")
	public String getUpdateCategoryPage(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Brand brand = this.brandService.getBrandById(id);
			List<Category> categories = this.categoryService.getAllCategories("asc");

			model.addAttribute("brand", brand);
			model.addAttribute("categories", categories);

			return "brands/update_brand";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Could not find any brand with id " + id);
			return "redirect:/brands";
		}
	}

	@PostMapping("/brands/update")
	public String handleUpdateCategories(Model model, @Valid Brand brand, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, @RequestParam("multipartFile") MultipartFile multipartFile)
			throws Exception {
		// validation
		if (bindingResult.hasErrors()) {
			List<Category> categories = this.categoryService.getAllCategories("asc");
			model.addAttribute("categories", categories);

			return "brands/update_brand";
		}

		if (!multipartFile.isEmpty()) {
			String targetFolder = "../brand-logos";
			// xóa ảnh cũ trước khi thêm ảnh mới, đảm bảo ảnh tồn tại mới xóa
			if (!StringUtils.isEmpty(brand.getLogo())) {
				this.uploadImageService.deletePhotos(targetFolder, brand.getLogo());
			}
			String fileName = this.uploadImageService.handleSaveUploadFile(multipartFile, targetFolder);
			brand.setLogo(fileName);
		}

		this.brandService.handleSaveBrand(brand);

		redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully.");

		return "redirect:/brands";
	}

	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			String targetFolder = "../brand-logos";
			Brand brand = this.brandService.getBrandById(id);
			String logoName = brand.getLogo();
			this.brandService.deleteBrandById(id);
			/*
			 * nên để xóa ảnh sau vì trường hợp xóa brand thất bại thì ném ngoại lệ trc thay
			 * vì xóa ảnh
			 */
			this.uploadImageService.deletePhotos(targetFolder, logoName);

			redirectAttributes.addFlashAttribute("message", "The brand ID " + id + " has been deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/brands";
	}

	@GetMapping("/brands/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Brand> listBrands = this.brandService.getAllBrands();
		BrandCsvExporter exporter = new BrandCsvExporter();
		exporter.export(listBrands, response);
	}
}
