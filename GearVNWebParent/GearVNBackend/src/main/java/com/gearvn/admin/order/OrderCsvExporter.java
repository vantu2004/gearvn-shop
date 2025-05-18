package com.gearvn.admin.order;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.gearvn.admin.common.AbstractExporter;
import com.gearvn.common.entity.order.Order;

import jakarta.servlet.http.HttpServletResponse;

public class OrderCsvExporter extends AbstractExporter {
	public void export(List<Order> listOrders, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "orders_", ".csv", "text/csv");

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = { "Order ID", "Email", "Full Name", "Order Time", "Shipping Cost", "Product Cost",
				"Subtotal", "Tax", "Total", "Deliver Days", "Deliver Date", "Payment Method", "Status", "Country" };

		String[] fieldMapping = { "id", "emailCsv", "fullNameCsv", "orderTimeCsv", "shippingCost", "productCost",
				"subtotal", "tax", "total", "deliverDays", "deliverDateCsv", "paymentMethod", "status", "country" };

		csvWriter.writeHeader(csvHeader);

		for (Order order : listOrders) {
			csvWriter.write(order, fieldMapping);
		}

		csvWriter.close();
	}
}
