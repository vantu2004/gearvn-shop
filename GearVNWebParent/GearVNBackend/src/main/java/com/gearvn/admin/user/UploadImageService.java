package com.gearvn.admin.user;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadImageService {

	public String handleSaveUploadFile(MultipartFile multipartFile, String targetFolder) {
		String fileName = "";
		try {
			byte[] bytes = multipartFile.getBytes();
			Path uploadPath = Paths.get(targetFolder);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			fileName = System.currentTimeMillis() + "-" + multipartFile.getOriginalFilename();

			String absolutePath = uploadPath.toAbsolutePath().toString();
			File serverFile = new File(absolutePath + File.separator + fileName);

			// Sử dụng try-with-resources để tự động đóng luồng
			try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile))) {
				stream.write(bytes);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileName;
	}

	public void deletePhotos(String dir, String fileName) {
		Path filePath = Paths.get(dir, fileName);

		try {
			if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
				Files.delete(filePath);
				System.out.println("Deleted file: " + filePath);
			} else {
				System.out.println("File not found or is a directory: " + filePath);
			}
		} catch (IOException ex) {
			throw new RuntimeException("Could not delete file: " + filePath, ex);
		}
	}

}
