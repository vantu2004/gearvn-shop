package com.gearvn.admin.paging;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagingAndSortingHelper {

	private ModelAndViewContainer model;

	private String listName;

	private String sortField;
	private String sortType;
	private String keyword;

	public PagingAndSortingHelper(ModelAndViewContainer model, String listName, String sortField, String sortType,
			String keyword) {
		this.model = model;
		this.listName = listName;

		this.sortField = sortField;
		this.sortType = sortType;
		this.keyword = keyword;
	}

	public void updateModelAttributes(int pageNumber, Page<?> page) {
		List<?> listItem = page.getContent();
		int pageSize = page.getSize();

		long totalElements = page.getTotalElements();
		long totalPages = page.getTotalPages();

		long startCount = (pageNumber - 1) * pageSize + 1;
		long endCount = startCount + pageSize - 1;
		if (endCount > totalElements) {
			endCount = totalElements;
		}

		long startPage = (pageNumber - 2 < 1) ? 1 : pageNumber - 2;
		long endPage = (pageNumber + 2 > totalPages) ? totalPages : pageNumber + 2;

		model.addAttribute(this.listName, listItem);

		// pagination
		model.addAttribute("totalElements", totalElements);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
	}

	// dùng cho user/brand/customer
	/*
	 * vì các repo khác đang extends searchPaging nên bên service kia thì truyền
	 * repo cụ thể còn bên này thì chỉ cần nhận searchPaging
	 */
	public void listEntities(int currentPage, int pageSize, SearchPaging<?, Integer> searchPaging) {
		Pageable pageable = this.createPageable(currentPage, pageSize);

		Page<?> page = null;

		if (StringUtils.isEmpty(this.keyword)) {
			page = searchPaging.findAll(pageable);
		} else {
			/**
			 * searchPaging là baseRepo, các repo khác extends từ repo này và override lại
			 * findAll, ko dùng @Override bên trong interface được nhưng bản chất vẫn là
			 * override
			 */
			page = searchPaging.findAll(this.keyword, pageable);
		}

		/**
		 * - @PagingAndSortingParam đánh dấu một tham số trong controller cần được xử lý
		 * bởi resolver tùy chỉnh.
		 * 
		 * - PagingAndSortingArgumentResolve kiểm tra tham số trong controller
		 * dùng @PagingAndSortingParam ko, có hàm supportsParameter để check và hàm
		 * resolveArgument để lấy dữ liệu từ request và trong annotation đã truyền vào.
		 * 
		 * - PagingAndSortingHelper xử lý logic
		 */
		updateModelAttributes(currentPage, page);
	}

	public Pageable createPageable(int currentPage, int pageSize) {
		Sort sort = Sort.by(this.sortField);
		sort = this.sortType.equals("asc") ? sort.ascending() : sort.descending();

		return PageRequest.of(currentPage - 1, pageSize, sort);
	}

	public String getKeyword() {
		return keyword;
	}

}
