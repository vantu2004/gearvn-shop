package com.gearvn.admin.user.controller;

import java.io.IOException;
import java.util.List;

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

import jakarta.servlet.http.HttpServletResponse;

import com.gearvn.admin.common.UploadImageService;
import com.gearvn.admin.paging.PagingAndSortingHelper;
import com.gearvn.admin.paging.PagingAndSortingParam;
import com.gearvn.admin.user.export.UserCsvExporter;
import com.gearvn.admin.user.export.UserExcelExporter;
import com.gearvn.admin.user.export.UserPdfExporter;
import com.gearvn.admin.user.service.UserService;
import com.gearvn.common.entity.Role;
import com.gearvn.common.entity.User;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private UploadImageService uploadImageService;

	// bắt buộc phải trả về String vì @GetMapping phải nhận tên template để render
	@GetMapping("/users")
	public String getFirstUserPage() {
		return "redirect:/users/page/1?sortField=firstName&sortType=asc";
	}

	@GetMapping("/users/page/{pageNumber}")
	public String getUserPageByPageNumber(@PathVariable("pageNumber") int pageNumber,
			@PagingAndSortingParam(listName = "listUsers", moduleUrl = "/users") PagingAndSortingHelper helper) {
		
		this.userService.pagination_getAllUsers(pageNumber, helper);

		return "users/users";
	}

	@GetMapping("/users/new")
	public String getCreateNewUserPage(Model model) {
		User user = new User();

		List<Role> listRoles = this.userService.getAllRoles();

		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);

		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String handleSaveUser(Model model, @Valid User user, BindingResult newUserBindingResult,
			RedirectAttributes redirectAttributes, @RequestParam("avatar") MultipartFile multipartFile)
			throws IOException {

		// validation
		if (newUserBindingResult.hasErrors()) {
			List<Role> listRoles = this.userService.getAllRoles();
			model.addAttribute("listRoles", listRoles);

			return "users/user_form";
		}

		if (!multipartFile.isEmpty()) {
			String targetFolder = "user-photos";
			String fileName = this.uploadImageService.handleSaveUploadFile(multipartFile, targetFolder);
			user.setPhotos(fileName);
		}

		this.userService.handleSaveUser(user);

		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
		return "redirect:/users";
	}

	@GetMapping("/users/update/{id}")
	public String getUpdateUserPage(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			User user = this.userService.getUserById(id);
			List<Role> listRoles = this.userService.getAllRoles();

			model.addAttribute("user", user);
			model.addAttribute("oldPassword", user.getPassword());
			model.addAttribute("listRoles", listRoles);

			return "users/admin_update_form";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Could not find any user with id " + id);
			return "redirect:/users";
		}
	}

	@PostMapping("/users/update")
	public String handleUpdateUser(Model model, @Valid User user, BindingResult newUserBindingResult,
			RedirectAttributes redirectAttributes, @RequestParam("avatar") MultipartFile multipartFile)
			throws Exception {

		User oldUser = this.userService.getUserById(user.getId());

		// validation
		if (newUserBindingResult.hasErrors()) {
			List<Role> listRoles = this.userService.getAllRoles();
			model.addAttribute("listRoles", listRoles);

			user.setPhotos(oldUser.getPhotos());

			return "users/admin_update_form";
		}

		String fileName = oldUser.getPhotos();
		/*
		 * nếu ko set như vậy thì trường hợp người dùng ko update ảnh gì thì vẫn đảm bảo
		 * là ảnh cũ
		 */
		user.setPhotos(fileName);
		if (!multipartFile.isEmpty()) {
			String targetFolder = "user-photos";
			// xóa ảnh cũ trước khi thêm ảnh mới, đảm bảo ảnh tồn tại mới xóa
			if (fileName != null && !fileName.isEmpty()) {
				this.uploadImageService.deletePhotos(targetFolder, fileName);
			}
			fileName = this.uploadImageService.handleSaveUploadFile(multipartFile, targetFolder);
			user.setPhotos(fileName);
		}

		this.userService.handleUpdateUser(oldUser, user);

		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
		// sau khi update xong thì trả về đúng user đã update
		String email = user.getEmail();
		return "redirect:/users/page/1?sortField=id&sortType=asc&keyword=" + email;
	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			String targetFolder = "user-photos";
			User user = this.userService.getUserById(id);
			String fileName = user.getPhotos();

			this.userService.deleteUser(id);
			if (fileName != null && !fileName.isEmpty()) {
				this.uploadImageService.deletePhotos(targetFolder, fileName);
			}

			redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Could not find any user with id " + id);
		}

		return "redirect:/users";
	}

	@GetMapping("/users/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		List<User> users = this.userService.getAllUsers();
		UserCsvExporter userCsvExporter = new UserCsvExporter();
		userCsvExporter.export(users, response);
	}

	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> users = this.userService.getAllUsers();
		UserExcelExporter userExcelExporter = new UserExcelExporter();
		userExcelExporter.export(users, response);
	}

	@GetMapping("/users/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws IOException {
		List<User> users = this.userService.getAllUsers();
		UserPdfExporter userPdfExporter = new UserPdfExporter();
		userPdfExporter.export(users, response);
	}
}
