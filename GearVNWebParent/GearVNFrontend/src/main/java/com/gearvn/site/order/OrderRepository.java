package com.gearvn.site.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.order.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}
