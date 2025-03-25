package com.gearvn.site.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.Category;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	// lấy danh sách categories cuối cùng (ko có sub-categories)
	public List<Category> getListNoChildrenCategories() {
		List<Category> listNoChildrenCategories = new ArrayList<>();
		List<Category> listCategoriesEnabled = this.categoryRepository.findAllCategoriesEnabled();

		for (Category category : listCategoriesEnabled) {
			Set<Category> childrenCategories = category.getChildren();
			if (childrenCategories == null || childrenCategories.size() == 0) {
				listNoChildrenCategories.add(category);
			}
		}

		return listNoChildrenCategories;
	}

	// lấy category đã enabled bằng alias
	public Category getCategoryAliasEnabled(String alias) {
		Category category = this.categoryRepository.findByAliasEnabled(alias);
		if (category != null) {
			return category;
		}
		throw new NoSuchElementException("Could not find any category with alias " + alias);
	}

	// nhận vào category hiện tại và loop ngược để lấy các category parent của
	// category đó
	public List<Category> getCategoryParent(Category child) {
		List<Category> listParents = new ArrayList<>();

		Category parent = child.getParent();
		while (parent != null) {
			// vì loop ngược nên đảm bảo khi add là add parent ưu tiên lên trc
			listParents.add(0, parent);
			parent = parent.getParent();
		}

		// thêm child vào cuối list
		listParents.add(child);

		return listParents;
	}
}