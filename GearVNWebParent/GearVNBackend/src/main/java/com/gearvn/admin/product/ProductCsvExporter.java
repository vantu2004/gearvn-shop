package com.gearvn.admin.product;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.gearvn.admin.common.AbstractExporter;
import com.gearvn.common.entity.product.Product;

import jakarta.servlet.http.HttpServletResponse;

public class ProductCsvExporter extends AbstractExporter {

    public void export(List<Product> products, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "products_", ".csv", "text/csv");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        // Định nghĩa tiêu đề CSV
        String[] csvHeader = { "Product ID", "Name", "Alias", "Created Time", "Updated Time", "Enabled", "In Stock",
                "Cost", "Price", "Discount Percent", "Length", "Width", "Height", "Weight", "Category", "Brand" };

        // Mapping các thuộc tính tương ứng
        String[] fieldMapping = { "id", "name", "alias", "createdTime", "updatedTime", "enabled", "inStock", "cost",
                "price", "discountPercent", "length", "width", "height", "weight", "categoryName", "brandName" };

        csvWriter.writeHeader(csvHeader);

        // Ghi dữ liệu sản phẩm vào CSV
        for (Product product : products) {
            csvWriter.write(product, fieldMapping);
        }

        csvWriter.close();
    }
}
