package com.gearvn.admin.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {
	@Autowired
	private CategoryService categoryService;

//	@PostMapping("/categories/check_duplicate")
//	public String checkDuplicate(String name, String alias) {
//		name = StringUtils.isEmpty(name) ? "" : name;
//		alias = StringUtils.isEmpty(alias) ? "" : alias;
//		return this.categoryService.isNameAliasUnique(name, alias) ? "OK" : "Duplicated";
//	}
//
//	@PostMapping("/categories/check_duplicate_update")
//	public String checkDuplicateCategoryUpdate(Integer id, String name, String alias) throws Exception {
//	    Category category = this.categoryService.getCategoryById(id);
//		name = StringUtils.isEmpty(name) ? "" : name;
//		alias = StringUtils.isEmpty(alias) ? "" : alias;
//
//	    // Nếu cả name và alias đều không thay đổi thì chấp nhận
//	    if (name.equals(category.getName()) && alias.equals(category.getAlias())) {
//	        return "OK";
//	    }
//
//	    // trả về true nếu có thay đổi, false nếu ko thay đổi
//	    boolean aliasChanged = !alias.equals(category.getAlias());
//	    boolean nameChanged = !name.equals(category.getName());
//
//	    // 1 trong 2 false thì false hết
//	    // trả về true nếu ko duy nhất, false nếu duy nhất
//	    boolean aliasDuplicate = aliasChanged && !this.categoryService.isAliasUnique(alias);
//	    boolean nameDuplicate = nameChanged && !this.categoryService.isNameUnique(name);
//
//	    // Nếu alias hoặc name bị trùng, trả về "Duplicated"
//	    if (aliasDuplicate || nameDuplicate) {
//	        return "Duplicated";
//	    }
//
//	    // Nếu alias hoặc name thay đổi nhưng không trùng, chấp nhận cập nhật
//	    return "OK";
//	}

	@PostMapping("/categories/check_duplicate")
	public String checkDuplicate(Integer id, String name, String alias) {

		return this.categoryService.checkUnique(id, name, alias);
	}
}
