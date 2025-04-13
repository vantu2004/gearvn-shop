package com.gearvn.admin.security;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gearvn.admin.paging.PagingAndSortingArgumentResolve;

//	Đánh dấu class này là class cấu hình chức năng giống file XML
@Configuration
//	Đánh dấu class ở dạng Spring MVC
//  @EnableWebMvc
//	Triển khai interface WebMvcConfigure để tùy chỉnh cấu hình Spring MVC
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		exposeDirectory("user-photos", registry);
		exposeDirectory("../category-images", registry);
		exposeDirectory("../brand-logos", registry);
		exposeDirectory("../product-images", registry);
		exposeDirectory("../site-logo", registry);

	}

	private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
		// trỏ đến thư mục của pathParttern
		Path path = Paths.get(pathPattern);
		String absolutePath = path.toFile().getAbsolutePath();

		// ví dụ pathPattern là "user-photos" -> "user-photos/**"
		// pathPartter là "../user-photos" -> "user-photos/**"
		String logicalPath = pathPattern.replace("../", "") + "/**";

		// addResourceHandler cho quyền truy cập tài nguyên bên trong logicPath
		// addResourceLocations chỉ định đường dẫn đến thư mục được cấp quyền
		registry.addResourceHandler(logicalPath).addResourceLocations("file:/" + absolutePath + "/");
	}

	/*
	 * Là một phương thức của interface WebMvcConfigurer, cho phép thêm các
	 * HandlerMethodArgumentResolver tùy chỉnh vào danh sách mà Spring MVC sử dụng.
	 * Là cách đăng ký thủ công một custom argument resolver với Spring MVC.
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		// TODO Auto-generated method stub
		resolvers.add(new PagingAndSortingArgumentResolve());
	}

}
