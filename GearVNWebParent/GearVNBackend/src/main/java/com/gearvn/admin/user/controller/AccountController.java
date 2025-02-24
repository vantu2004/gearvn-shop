package com.gearvn.admin.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gearvn.admin.security.GearvnUserDetails;
import com.gearvn.admin.user.service.UploadImageService;
import com.gearvn.admin.user.service.UserService;
import com.gearvn.common.entity.Role;
import com.gearvn.common.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AccountController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UploadImageService uploadImageService;
	
	/*
	 * khi user đăng nhập thành công, GearvnUserDetailsService trả về 1 UserDetails,
	 * nhưng UserDetails này đã được custom lại bởi GearvnUserDetails, lúc này
	 * GearvnUserDetails đc lưu vào security context, @AuthenticationPrincipal giúp
	 * lấy GearvnUserDetails đã lưu ra
	 */
	@GetMapping("/account")
	public String getDetailsPage(@AuthenticationPrincipal GearvnUserDetails gearvnUserDetails, Model model) {
		String email = gearvnUserDetails.getEmail();
		User user = this.userService.getUserByEmail(email);
		List<Role> listRoles = this.userService.getAllRoles();

		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);

		return "users/user_update_form";
	}

	@PostMapping("/account/update")
	public String handleUpdateUser(@AuthenticationPrincipal GearvnUserDetails gearvnUserDetails, Model model, @Valid User user, BindingResult newUserBindingResult,
			RedirectAttributes redirectAttributes, @RequestParam("avatar") MultipartFile multipartFile, HttpServletRequest request)
			throws Exception {

		User oldUser = this.userService.getUserById(user.getId());
		
		// validation
		if (newUserBindingResult.hasErrors()) {
			List<Role> listRoles = this.userService.getAllRoles();
			model.addAttribute("listRoles", listRoles);

			user.setPhotos(oldUser.getPhotos());
			
			return "users/user_update_form";
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

		// update data mới cho bên UserDetails của securityContext
		gearvnUserDetails.setFirstName(user.getFirstName());
		gearvnUserDetails.setLastName(user.getLastName());
		gearvnUserDetails.setPhotos(user.getPhotos());

		// Cập nhật lại securityContext vào session
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
		}

		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");

		return "redirect:/account";
	}

}
