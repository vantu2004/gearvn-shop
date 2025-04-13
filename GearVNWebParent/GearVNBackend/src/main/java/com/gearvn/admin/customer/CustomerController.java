package com.gearvn.admin.customer;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gearvn.admin.paging.PagingAndSortingHelper;
import com.gearvn.admin.paging.PagingAndSortingParam;
import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.Customer;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@GetMapping("/customers")
	public String getCustomersFirstPage(Model model) {
		return "redirect:/customers/page/1?sortField=firstName&sortType=asc";
	}

	@GetMapping("/customers/page/{currentPage}")
	public String getCustomerPage_pageable(@PathVariable("currentPage") int currentPage,
			@PagingAndSortingParam(listName = "listCustomers", moduleUrl = "/customers") PagingAndSortingHelper helper) {

		this.customerService.getAllCustomersPageable(currentPage, helper);

		return "customers/list_customers";
	}

	@GetMapping("/customers/detail/{customerId}")
	public String getViewDetailPage(Model model, @PathVariable("customerId") Integer customerId,
			RedirectAttributes redirectAttributes) {
		try {
			Customer customer = this.customerService.getCustomerById(customerId);
			model.addAttribute("customer", customer);

			return "customers/view_customer";
		} catch (Exception e) {
			// TODO: handle exception
			redirectAttributes.addFlashAttribute("message", e.getMessage());

			return "redirect:/customers";
		}
	}

	@GetMapping("/customers/update/{customerId}")
	public String getUpdateCustomerPage(@PathVariable("customerId") Integer customerId, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Customer customer = this.customerService.getCustomerById(customerId);
			List<Country> countries = this.customerService.getAllCountries();

			model.addAttribute("customer", customer);
			model.addAttribute("countries", countries);

			return "customers/update_customer";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/customers";
		}
	}

	@PostMapping("/customers/update")
	public String handleUpdateCustomers(Model model, @Valid Customer customer, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) throws Exception {
		// validation
		if (bindingResult.hasErrors()) {
			List<Country> countries = this.customerService.getAllCountries();
			model.addAttribute("countries", countries);

			return "customers/update_customer";
		}

		this.customerService.handleSaveCustomer(customer);

		redirectAttributes.addFlashAttribute("message", "The customer has been saved successfully.");

		return "redirect:/customers";
	}

	@GetMapping("/customers/delete/{customerId}")
	public String deleteBrand(@PathVariable("customerId") Integer customerId, RedirectAttributes redirectAttributes) {
		try {
			this.customerService.deleteCustomerById(customerId);

			redirectAttributes.addFlashAttribute("message",
					"The customer ID " + customerId + " has been deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/customers";
	}

	@GetMapping("/customers/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Customer> listCustomers = this.customerService.getAllCustomers();
		CustomerCsvExporter exporter = new CustomerCsvExporter();
		exporter.export(listCustomers, response);
	}
}
