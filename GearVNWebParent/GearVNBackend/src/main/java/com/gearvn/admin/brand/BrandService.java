package com.gearvn.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

	public void deleteBrandById(Integer id) {
		Long count = this.brandRepository.countById(id);
		if (count == null || count == 0) {
			throw new NoSuchElementException("Could not find any brand with id " + id);
		}

		this.brandRepository.deleteById(id);
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
	
	public Page<Brand> getAllBrands(int currentPage, String sortField, String sortType, String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortType.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(currentPage - 1, BRANDS_PER_PAGE, sort);
		
		if (StringUtils.isEmpty(keyword)) {
			return this.brandRepository.findAll(pageable);
		}
		
		return this.brandRepository.findAll(keyword, pageable);
	}
}
