package com.gearvn.site.security;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//	Đánh dấu class này là class cấu hình chức năng giống file XML
@Configuration
//	Đánh dấu class ở dạng Spring MVC
//  @EnableWebMvc
//	Triển khai interface WebMvcConfigure để tùy chỉnh cấu hình Spring MVC
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
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

}
