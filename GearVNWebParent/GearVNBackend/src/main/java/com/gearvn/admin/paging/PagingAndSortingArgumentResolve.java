package com.gearvn.admin.paging;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/*
 * Lớp này giúp tự định nghĩa cách lấy giá trị từ request và truyền vào tham số
 * của controller, thường dùng khi có annotation tùy chỉnh như
 * @PagingAndSortingParam. Class này được gọi khi trong controller có phương
 * thức sử dụng Annotation tự định nghĩa (spring quét qua các param)
 */
public class PagingAndSortingArgumentResolve implements HandlerMethodArgumentResolver {

	/*
	 * Hàm này dùng để xác định xem tham số đó có nên được xử lý bởi resolver này
	 * không. ví dụ return
	 * parameter.hasParameterAnnotation(PagingAndSortingParam.class); nếu tham số có
	 * annotation @PagingAndSortingParam, Spring sẽ gọi resolveArgument() để lấy giá
	 * trị.
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// TODO Auto-generated method stub
		return parameter.hasParameterAnnotation(PagingAndSortingParam.class);
	}

	/*
	 * Hàm này là nơi trích dữ liệu từ request (ví dụ: page, size, sort từ URL), sau
	 * đó trả về đối tượng phù hợp để gán vào tham số controller.
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer model, NativeWebRequest request,
			WebDataBinderFactory binderFactory) throws Exception {
		// TODO Auto-generated method stub

		PagingAndSortingParam pagingAndSortingParam = parameter.getParameterAnnotation(PagingAndSortingParam.class);

		String sortType = request.getParameter("sortType");
		String sortField = request.getParameter("sortField");
		String keyword = request.getParameter("keyword");

		// sort
		String reverseSortType = sortType.equals("asc") ? "desc" : "asc";
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortType", sortType);
		model.addAttribute("reverseSortType", reverseSortType);

		// filter
		model.addAttribute("keyword", keyword);

		// module chỉ cần add vào model và ko custom nên ko cần truyền
		model.addAttribute("moduleUrl", pagingAndSortingParam.moduleUrl());

		return new PagingAndSortingHelper(model, pagingAndSortingParam.listName(), sortField, sortType, keyword);
	}

}
