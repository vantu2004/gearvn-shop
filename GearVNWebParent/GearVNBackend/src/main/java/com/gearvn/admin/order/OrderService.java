package com.gearvn.admin.order;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.gearvn.admin.paging.PagingAndSortingHelper;
import com.gearvn.common.entity.order.Order;

@Service
public class OrderService {

	public static final int ORDERS_PER_PAGE = 10;

	@Autowired
	private OrderRepository orderRepository;

	public void getAllOrdersPageable(int currentPage, PagingAndSortingHelper helper) {
		String sortField = helper.getSortField();
		String sortType = helper.getSortType();
		String keyword = helper.getKeyword();

		Sort sort = null;

		if ("destination".equals(sortField)) {
			sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
		} else {
			sort = Sort.by(sortField);
		}

		sort = sortType.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(currentPage - 1, ORDERS_PER_PAGE, sort);

		Page<Order> page = null;

		if (keyword != null) {
			page = this.orderRepository.findAll(keyword, pageable);
		} else {
			page = this.orderRepository.findAll(pageable);
		}

		helper.updateModelAttributes(currentPage, page);
	}

	public Order getOrderById(Integer orderId) {
		return this.orderRepository.findById(orderId).orElseThrow(() -> new NoSuchElementException("Order Not Found!"));
	}

	public void deleteOrderById(Integer id) {
		Long count = this.orderRepository.countById(id);
		if (count == null || count == 0) {
			throw new NoSuchElementException("Could not find any orders with ID " + id);
		}

		this.orderRepository.deleteById(id);
	}

	public List<Order> getAllOrders() {
		return this.orderRepository.findAll();
	}
}
