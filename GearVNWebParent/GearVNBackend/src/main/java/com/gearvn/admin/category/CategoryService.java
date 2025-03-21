package com.gearvn.admin.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.Category;

@Service
public class CategoryService {
	public static final int ROOT_CATEGORIES_PER_PAGE = 4;
	@Autowired
	private CategoryRepository categoryRepository;

	public List<Category> getAllCategories_pageable(String sortType, int pageNumber, CategoryPageInfo categoryPageInfo,
			String keyword) {
		Pageable pageable = PageRequest.of(pageNumber - 1, ROOT_CATEGORIES_PER_PAGE, getSortType(sortType));

		Page<Category> pageableCategories;
		if (StringUtils.isEmpty(keyword)) {
			pageableCategories = this.categoryRepository.findRootCategories_Pageable(pageable);
		} else {
			pageableCategories = this.categoryRepository.findRootCategories_Pageable(keyword, pageable);
		}

		categoryPageInfo.setTotalPages(pageableCategories.getTotalPages());
		categoryPageInfo.setTotalElements(pageableCategories.getTotalElements());

		// lấy listRootCategories theo page
		List<Category> listCategories = pageableCategories.getContent();

		if (StringUtils.isEmpty(keyword)) {
			// dựa vào listRootCategories trả về cây category đã phân cấp trong 1 page
			return getHierarchicalCategories(listCategories, sortType);
		}
		// nếu user search, mặc định chỉ trả về listRootCategories
		else {
			return listCategories;
		}
	}

	public List<Category> getAllCategories(String sortType) {
		// lấy listRootCategories đc sort mặc định
		List<Category> listCategories = this.categoryRepository.findRootCategories(getSortType(sortType));
		List<Category> listHierarchicalCategories = getHierarchicalCategories(listCategories, sortType);
		
		// dựa vào listRootCategories trả về toàn bộ cây category đã dc phân cấp
		return listHierarchicalCategories;
	}

	private Sort getSortType(String sortType) {
		Sort sort = Sort.by("name");
		return sortType.equals("asc") ? sort.ascending() : sort.descending();
	}

	// public để bên brandService có thể truy cập được và lấy categories theo brand
	public List<Category> getHierarchicalCategories(List<Category> categories, String sortType) {
		List<Category> hierarchicalCategories = new ArrayList<>();

		// duyệt lần 1
		for (Category rootCategory : categories) {
			if (rootCategory.getParent() == null) {
				// tạo bản sao của category để thao tác ko bị ảnh hưởng bản gốc
				hierarchicalCategories.add(copyCategory(rootCategory));

				List<Category> sortedChildren = sortCategories(rootCategory.getChildren(), sortType);
				// duyệt lần 2, vẫn tạo bản sao category
				for (Category subCategory : sortedChildren) {
					Category copyCategory = copyCategory(subCategory);
					copyCategory.setName("--" + copyCategory.getName());

					hierarchicalCategories.add(copyCategory);

					listChildren(subCategory, 2, hierarchicalCategories, sortType);
				}
			}
		}
		return hierarchicalCategories;
	}

	private void listChildren(Category parent, int subLevel, List<Category> hierarchicalCategories, String sortType) {
		List<Category> sortedChildren = sortCategories(parent.getChildren(), sortType);

		for (Category subCategory : sortedChildren) {
			String namePrefix = "-".repeat(subLevel * 2);
			Category copyCategory = copyCategory(subCategory);
			copyCategory.setName(namePrefix + subCategory.getName());

			hierarchicalCategories.add(copyCategory);

			// duyệt lần 3++ (đệ quy)
			listChildren(subCategory, subLevel + 1, hierarchicalCategories, sortType);
		}
	}

	// sort subCategories, còn rootCategories đã được sort mặc định bên repository
	private List<Category> sortCategories(Collection<Category> categories, String sortType) {
		Comparator<Category> comparator = Comparator.comparing(Category::getName);
		if ("desc".equalsIgnoreCase(sortType)) {
			comparator = comparator.reversed();
		}
		return categories.stream().sorted(comparator).toList();
	}

	public void handleSaveCategory(Category category) {
		this.categoryRepository.save(category);
	}

	// shallow copy
	public Category copyCategory(Category category) {
		return Category.builder().id(category.getId()).name(category.getName()).alias(category.getAlias())
				.image(category.getImage()).enabled(category.isEnabled()).parent(category.getParent())
				.children(category.getChildren()).build();
	}

	public Category getCategoryById(Integer id) {
		Optional<Category> category = this.categoryRepository.findById(id);
		if (category.isPresent()) {
			return category.get();
		}
		throw new NoSuchElementException("Could not find any category with id " + id);
	}

	// dùng cho create và update
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);

		Category categoryByName = this.categoryRepository.findByName(name);

		// create
		if (isCreatingNew) {
			if (categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = this.categoryRepository.findByAlias(alias);
				if (categoryByAlias != null) {
					return "DuplicateAlias";
				}
			}
		}
		// update
		else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}

			Category categoryByAlias = this.categoryRepository.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}

		}

		return "OK";
	}

	public void deleteCategoryById(Integer id) {
		Long count = this.categoryRepository.countById(id);
		if (count == 0 || count == null) {
			throw new NoSuchElementException("Could not find any category with id " + id);
		}

		this.categoryRepository.deleteById(id);
	}
}
