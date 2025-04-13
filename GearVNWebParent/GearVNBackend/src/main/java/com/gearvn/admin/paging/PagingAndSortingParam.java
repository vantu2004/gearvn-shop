package com.gearvn.admin.paging;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// Annotation tồn tại trong file .class và cả trong runtime, có thể truy cập bằng Java Reflection API.
@Retention(RUNTIME)
// Annotation này chỉ áp dụng được cho tham số của phương thức (method parameters).
@Target(PARAMETER)
public @interface PagingAndSortingParam {
	public String moduleUrl();

	public String listName();
}
