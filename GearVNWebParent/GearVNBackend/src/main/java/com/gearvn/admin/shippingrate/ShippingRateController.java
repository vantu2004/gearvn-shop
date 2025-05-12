package com.gearvn.admin.shippingrate;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gearvn.admin.paging.PagingAndSortingHelper;
import com.gearvn.admin.paging.PagingAndSortingParam;
import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.ShippingRate;

import jakarta.validation.Valid;

@Controller
public class ShippingRateController {
	private String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=country&sortType=asc";

	@Autowired
	private ShippingRateService shippingRateService;

	@GetMapping("/shipping_rates")
	public String getShippingRateFirstPage() {
		return defaultRedirectURL;
	}

	@GetMapping("/shipping_rates/page/{currentPage}")
	public String getShippingRatePage_pageable(
			@PagingAndSortingParam(listName = "listShippingRates", moduleUrl = "/shipping_rates") PagingAndSortingHelper helper,
			@PathVariable(name = "currentPage") int currentPage) {
		this.shippingRateService.getAllShippingRatesByPage(currentPage, helper);
		return "shipping_rates/list_shipping_rates";
	}

	@GetMapping("/shipping_rates/create")
	public String getCreateShippingRatePage(Model model) {
		List<Country> countries = this.shippingRateService.getAllCountries();

		model.addAttribute("shippingRate", new ShippingRate());
		model.addAttribute("countries", countries);

		return "shipping_rates/create_shipping_rate";
	}

	// BindingResult buộc phải ngay sau @Valid nếu ko sẽ lỗi
	@PostMapping("/shipping_rates/save")
	public String handleSaveShippingRate(@Valid @ModelAttribute("shippingRate") ShippingRate shippingRate,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) throws Exception {
		try {
			// validation
			if (bindingResult.hasErrors()) {
				List<Country> countries = this.shippingRateService.getAllCountries();
				model.addAttribute("countries", countries);

				// vì create và update dùng chung nên khi đá lỗi thì phải đã cụ thể về trang nào
				if (shippingRate.getId() != null) {
					return "shipping_rates/update_shipping_rate";
				}
				return "shipping_rates/create_shipping_rate";
			}

			this.shippingRateService.handleSaveShippingRate(shippingRate);
			redirectAttributes.addFlashAttribute("message", "The shipping rate has been saved successfully.");
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;
	}

	@GetMapping("/shipping_rates/update/{id}")
	public String getUpdateShippingRatePage(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			ShippingRate shippingRate = this.shippingRateService.getShippingRateById(id);
			List<Country> countries = this.shippingRateService.getAllCountries();

			model.addAttribute("countries", countries);
			model.addAttribute("shippingRate", shippingRate);

			return "shipping_rates/update_shipping_rate";
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/shipping_rates/delete/{id}")
	public String handleDeleteShippingRate(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			this.shippingRateService.deleteShippingRate(id);
			redirectAttributes.addFlashAttribute("message", "The shipping rate id " + id + " has been deleted.");
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;
	}
}
