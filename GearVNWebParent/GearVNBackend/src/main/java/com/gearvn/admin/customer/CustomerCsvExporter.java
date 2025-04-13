package com.gearvn.admin.customer;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.gearvn.admin.common.AbstractExporter;
import com.gearvn.common.entity.Customer;

import jakarta.servlet.http.HttpServletResponse;

public class CustomerCsvExporter extends AbstractExporter {
	public void export(List<Customer> listCustomers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "customers_", ".csv", "text/csv");

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = { "Customer ID", "Email", "First Name", "Last Name", "Phone Number", "Address Line 1",
				"Address Line 2", "City", "State", "Postal Code", "Country", "Enabled", "Created Time" };

		String[] fieldMapping = { "id", "email", "firstName", "lastName", "phoneNumber", "addressLine1", "addressLine2",
				"city", "state", "postalCode", "countryNameCsv", "enabled", "createdTimeCsv" };

		csvWriter.writeHeader(csvHeader);

		for (Customer customer : listCustomers) {
			csvWriter.write(customer, fieldMapping);
		}

		csvWriter.close();
	}
}
