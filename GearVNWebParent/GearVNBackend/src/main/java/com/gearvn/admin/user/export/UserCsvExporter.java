package com.gearvn.admin.user.export;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.gearvn.admin.user.AbstractExporter;
import com.gearvn.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

public class UserCsvExporter extends AbstractExporter {

	public void export(List<User> users, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "users_", ".csv", "text/csv");

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		// set header
		String[] csvHeader = { "User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled" };
		// set field mapping header
		String[] fieldMapping = { "id", "email", "firstName", "lastName", "roles", "enabled" };

		csvWriter.writeHeader(csvHeader);

		// write data
		for (User user : users) {
			csvWriter.write(user, fieldMapping);
		}

		csvWriter.close();
	}
}
