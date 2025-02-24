package com.gearvn.admin.user.export;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jakarta.servlet.http.HttpServletResponse;

public class AbstractExporter {
	public void setResponseHeader(HttpServletResponse response, String firstFileName, String extension, String contentType) {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
		String fileName = firstFileName + timeStamp + extension;
		
		response.setContentType(contentType);
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName;
		response.setHeader(headerKey, headerValue);
	}
}
