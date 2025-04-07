package com.gearvn.admin.setting;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gearvn.admin.common.UploadImageService;
import com.gearvn.common.entity.Currency;
import com.gearvn.common.entity.Setting;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SettingController {
	@Autowired
	private SettingService settingService;

	@Autowired
	private CurrencyRepository currencyRepository;

	@Autowired
	private UploadImageService uploadImageService;

	@GetMapping("/settings")
	public String getSettingPage(Model model) {
		List<Setting> settings = this.settingService.getAllSettings();
		List<Currency> currencies = this.currencyRepository.findAllByOrderByNameAsc();

		for (Setting setting : settings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		model.addAttribute("currencies", currencies);

		return "settings/settings";
	}

	// cơ chế dirty checking giúp tự update mà ko cần gọi save
	@PostMapping("/settings/save_general")
	public String saveGeneralSettings(RedirectAttributes redirectAttributes,
			@RequestParam("multipartFile") MultipartFile multipartFile, HttpServletRequest request) {

		GeneralSettingBag generalSettingBag = this.settingService.getGeneralSettingBag();

		if (!multipartFile.isEmpty()) {
			String targetFolder = "../site-logo";
			String fileName = this.uploadImageService.handleSaveUploadFile(multipartFile, targetFolder);
			// xóa ảnh cũ
			if (generalSettingBag.getValue("SITE_LOGO") != null) {
				// tùy chỉnh lại SITE_LOGO để đảm bảo cóa đúng tên ảnh cũ
				this.uploadImageService.deletePhotos(targetFolder,
						generalSettingBag.getValue("SITE_LOGO").replaceFirst("^/site-logo/", ""));
			}

			/*
			 * update SITE_LOGO trong bảng Setting, trong Controller "/settings" thay vì
			 * truyền list settings thì lại truyền key-value bằng loop --> bên tag image kia
			 * lấy src nhờ vào key "SITE_LOGO" chứ ko phải từ đối tượng nên ko thể viết hàm
			 * getImagePath trong đối tượng --> hardcode đường dẫn vào db luôn
			 */
			generalSettingBag.updateSiteLogo("/site-logo/" + fileName);
		}

		/*
		 * có update trong combobox "Currency Type" --> update lại currency symbol. Lấy
		 * currencyId từ request sau đó thực hiện update symbol cho field
		 * CURRENCY_SYMBOL trong bảng Setting (ko phải trong bảng Currency)
		 */
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Currency currency = this.currencyRepository.findById(currencyId).orElse(null);
		if (currencyId != null) {
			generalSettingBag.updateCurrencySymbol(currency.getSymbol());
		}

		/*
		 * lấy tập generalSettings đã update SITE_LOGO, CURRENCY_SYMBOL trc đó tiếp tục
		 * update cho 7 field còn lại
		 */
		List<Setting> generalSettings = generalSettingBag.getSettings();
		for (Setting setting : generalSettings) {
			String value = request.getParameter(setting.getKey());
			if (!StringUtils.isBlank(value)) {
				setting.setValue(value);
			}
		}

		// this.settingService.saveAllSettings(generalSettingBag.getSettings());

		redirectAttributes.addFlashAttribute("message", "General settings have been saved.");

		return "redirect:/settings";
	}

	// cơ chế dirty checking giúp tự update mà ko cần gọi save
	@PostMapping("/settings/save_mailServer")
	public String saveMailServerSettings(RedirectAttributes redirectAttributes, HttpServletRequest request) {
		List<Setting> mailServerSettings = this.settingService.getMailServerSettings();
		for (Setting setting : mailServerSettings) {
			String value = request.getParameter(setting.getKey());
			if (!StringUtils.isBlank(value)) {
				setting.setValue(value);
			}
		}

		// this.settingService.saveAllSettings(mailServerSettings);

		redirectAttributes.addFlashAttribute("message", "Mail server settings have been saved.");

		return "redirect:/settings#mailServer";
	}

	// cơ chế dirty checking giúp tự update mà ko cần gọi save
	@PostMapping("/settings/save_mailTemplate")
	public String saveMailTemplateSettings(RedirectAttributes redirectAttributes, HttpServletRequest request) {
		List<Setting> mailTemplateSettings = this.settingService.getMailTemplateSettings();
		for (Setting setting : mailTemplateSettings) {
			String value = request.getParameter(setting.getKey());
			if (!StringUtils.isBlank(value)) {
				setting.setValue(value);
			}
		}

		// this.settingService.saveAllSettings(mailTemplateSettings);

		redirectAttributes.addFlashAttribute("message", "Mail template settings have been saved.");

		return "redirect:/settings#mailTemplates";
	}
}
