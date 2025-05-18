package com.gearvn.admin.order;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gearvn.admin.paging.PagingAndSortingHelper;
import com.gearvn.admin.paging.PagingAndSortingParam;
import com.gearvn.admin.security.GearvnUserDetails;
import com.gearvn.admin.setting.SettingService;
import com.gearvn.common.entity.order.Order;
import com.gearvn.common.entity.setting.Setting;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class OrderController {
	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortType=desc";

	@Autowired
	private OrderService orderService;

	@Autowired
	private SettingService settingService;

	@GetMapping("/orders")
	public String getAllOrdersFirstPage() {
		return defaultRedirectURL;
	}

	@GetMapping("/orders/page/{currentPage}")
	public String getAllOrdersPageable(
			@PagingAndSortingParam(listName = "listOrders", moduleUrl = "/orders") PagingAndSortingHelper helper,
			@PathVariable(name = "currentPage") int currentPage, HttpServletRequest request,
			@AuthenticationPrincipal GearvnUserDetails gearvnUserDetails) {

		this.orderService.getAllOrdersPageable(currentPage, helper);

		// lấy hết setting liên quan CURRENCY và gửi lên client
		loadCurrencySetting(request);

		// nếu user chỉ có đúng role shipper thì chuyển về đúng page cho shipper
		if (!gearvnUserDetails.hasRole("Admin") && !gearvnUserDetails.hasRole("Salesperson")
				&& gearvnUserDetails.hasRole("Shipper")) {
			return "orders/orders_shipper";
		}

		return "orders/list_orders";
	}

	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySettings = this.settingService.getCurrencySettings();

		for (Setting setting : currencySettings) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
	}

	@GetMapping("/orders/detail/{orderId}")
	public String getOrderDetailsPage(@PathVariable("orderId") Integer orderId, Model model,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {
		try {
			Order order = this.orderService.getOrderById(orderId);

			loadCurrencySetting(request);

			model.addAttribute("order", order);

			return "orders/order_details";
		} catch (NoSuchElementException e) {
			// TODO: handle exception
			redirectAttributes.addAttribute("message", e.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/orders/delete/{orderId}")
	public String deleteOrderById(@PathVariable("orderId") Integer orderId, RedirectAttributes redirectAttributes) {
		try {
			this.orderService.deleteOrderById(orderId);

			redirectAttributes.addFlashAttribute("message",
					"The order ID " + orderId + " has been deleted successfully.");
		} catch (Exception e) {
			// TODO: handle exception
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return defaultRedirectURL;
	}

	@GetMapping("/orders/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Order> listOrders = this.orderService.getAllOrders();
		OrderCsvExporter exporter = new OrderCsvExporter();
		exporter.export(listOrders, response);
	}
}
