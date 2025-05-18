package com.gearvn.admin.shippingrate;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gearvn.admin.paging.PagingAndSortingHelper;
import com.gearvn.admin.product.ProductRepository;
import com.gearvn.admin.setting.country.CountryRepository;
import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.ShippingRate;
import com.gearvn.common.entity.product.Product;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShippingRateService {

	public static final int RATES_PER_PAGE = 10;
	private static final int DIM_DIVISOR = 139;

	@Autowired
	private ShippingRateRepository shippingRateRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private ProductRepository productRepository;

	public void getAllShippingRatesByPage(int currentPage, PagingAndSortingHelper helper) {
		helper.listEntities(currentPage, RATES_PER_PAGE, this.shippingRateRepository);
	}

	public List<Country> getAllCountries() {
		return this.countryRepository.findAllByOrderByNameAsc();
	}

	// tránh trùng lặp ShippingRate theo Country và State khi create/update
	public void handleSaveShippingRate(ShippingRate rateInForm) throws Exception {
		ShippingRate rateInDB = this.shippingRateRepository.findByCountryAndState(rateInForm.getCountry().getId(),
				rateInForm.getState());

		/*
		 * kiểm tra khi create thì trong db có tồn tại ShippingRate nào trước đó cùng
		 * Country và State. Nếu ID null (tức là đang create), mà DB đã có bản ghi cùng
		 * Country + State thì lỗi trùng.
		 */
		boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;

		/*
		 * kiểm tra khi update thì trong db có tồn tại 1 ShippingRate nào đó khác
		 * ShippingRate hiện tại nhưng có cùng Country và State. Nếu đang update (ID
		 * khác null) và DB có bản ghi khác bản ghi hiện tại nhưng lại cùng Country +
		 * State, thì cũng là trùng
		 */
		boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null
				&& !rateInDB.equals(rateInForm);

		if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
			throw new Exception("There's already a rate for the destination ");
		}

		this.shippingRateRepository.save(rateInForm);
	}

	public ShippingRate getShippingRateById(Integer id) {
		try {
			return this.shippingRateRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new NoSuchElementException("Could not find shipping rate with ID " + id);
		}
	}

	public void deleteShippingRate(Integer id) {
		Long count = this.shippingRateRepository.countById(id);
		if (count == null || count == 0) {
			throw new NoSuchElementException("Could not find shipping rate with ID " + id);

		}
		this.shippingRateRepository.deleteById(id);
	}

	public float calculateShippingCost(Integer productId, Integer countryId, String state) {
		ShippingRate shippingRate = this.shippingRateRepository.findByCountryAndState(countryId, state);

		if (shippingRate == null) {
			throw new NoSuchElementException(
					"No shipping rate found for the given destination. You have to enter shipping cost manually.");
		}

		Product product = this.productRepository.findById(productId).get();

		// tính cân nặng thể tích
		float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;

		// lựa chọn giữa cân nặng thể tích và cân nặng thực tế
		float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;

		return finalWeight * shippingRate.getRate();
	}
}
