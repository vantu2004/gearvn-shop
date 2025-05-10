package com.gearvn.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.gearvn.admin.paging.PagingAndSortingHelper;
import com.gearvn.common.entity.Brand;

import jakarta.validation.Valid;

@Service
public class BrandService {

	public static final int BRANDS_PER_PAGE = 10;

	@Autowired
	private BrandRepository brandRepository;

	public List<Brand> getAllBrands() {
		return this.brandRepository.findAll(Sort.by("name").ascending());
	}

	public void handleSaveBrand(@Valid Brand brand) {
		this.brandRepository.save(brand);
	}

	public Brand getBrandById(Integer id) {
		return this.brandRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Could not find any brand with id " + id));
	}

	public void deleteBrandById(Integer id) throws Exception {
		try {
			Long count = this.brandRepository.countById(id);
			if (count == null || count == 0) {
				throw new NoSuchElementException("Could not find any brand with id " + id);
			}

			this.brandRepository.deleteById(id);
		} catch (Exception e) {
			throw new Exception("Could not delete this product");
		}
	}

	public String isNameUnique(Integer id, String name) {
		Brand brand = this.brandRepository.findByName(name).orElse(null);

		// Tên chưa tồn tại
		if (brand == null) {
			return "OK";
		}

		// Cùng ID, nghĩa là không đổi tên
		if (id != null && id.equals(brand.getId())) {
			return "OK";
		}

		// Đã có thương hiệu khác sử dụng tên này
		return "Duplicated";
	}

	public void getAllBrands(int currentPage, PagingAndSortingHelper helper) {
		helper.listEntities(currentPage, BRANDS_PER_PAGE, brandRepository);
	}
}
