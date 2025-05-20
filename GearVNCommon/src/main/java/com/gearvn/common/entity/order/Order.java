package com.gearvn.common.entity.order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.gearvn.common.entity.Address;
import com.gearvn.common.entity.Customer;
import com.gearvn.common.entity.abstract_address.AbstractAddress;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "orders")
public class Order extends AbstractAddress {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 45)
	private String country;

	private Date orderTime;

	private float shippingCost;
	private float productCost;
	private float subtotal;
	private float tax;
	private float total;

	private int deliverDays;
	private Date deliverDate;

	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<OrderDetail> orderDetails = new HashSet<>();

//	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//	@OrderBy("updatedTime ASC")
//	@Builder.Default
//	private List<OrderTrack> orderTracks = new ArrayList<>();

	// dùng để xuất CSV
	@jakarta.persistence.Transient
	private String orderTimeCsv;
	@jakarta.persistence.Transient
	private String emailCsv;
	@jakarta.persistence.Transient
	private String fullNameCsv;
	@jakarta.persistence.Transient
	private String deliverDateCsv;

	public void copyAddressFromCustomer() {
		// TODO Auto-generated method stub
		setFirstName(customer.getFirstName());
		setLastName(customer.getLastName());
		setPhoneNumber(customer.getPhoneNumber());
		setAddressLine1(customer.getAddressLine1());
		setAddressLine2(customer.getAddressLine2());
		setCity(customer.getCity());
		setState(customer.getState());
		setPostalCode(customer.getPostalCode());
		setCountry(customer.getCountry().getName());
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", phoneNumber="
				+ phoneNumber + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", city=" + city
				+ ", state=" + state + ", postalCode=" + postalCode + ", country=" + country + ", orderTime="
				+ orderTime + ", shippingCost=" + shippingCost + ", productCost=" + productCost + ", subtotal="
				+ subtotal + ", tax=" + tax + ", total=" + total + ", deliverDays=" + deliverDays + ", deliverDate="
				+ deliverDate + ", paymentMethod=" + paymentMethod + ", status=" + status + "]";
	}

	@Transient
	public String getDestination() {
		String destination = city + ", ";
		if (StringUtils.hasText(state)) {
			destination += state + ", ";
		}
		destination += country;

		return destination;
	}

	@Transient
	public String getDeliverDateCsv() {
		if (deliverDate == null)
			return "";
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(deliverDate);
	}

	@Transient
	public String getEmailCsv() {
		return customer.getEmail();
	}

	@Transient
	public String getFullNameCsv() {
		return customer.getFullName();
	}

	@Transient
	public String getOrderTimeCsv() {
		if (orderTime == null)
			return "";
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderTime);
	}

	public void copyShippingAddress(Address address) {
		// TODO Auto-generated method stub
		setFirstName(address.getFirstName());
		setLastName(address.getLastName());
		setPhoneNumber(address.getPhoneNumber());
		setAddressLine1(address.getAddressLine1());
		setAddressLine2(address.getAddressLine2());
		setCity(address.getCity());
		setState(address.getState());
		setPostalCode(address.getPostalCode());
		setCountry(address.getCountry().getName());
	}

	@Transient
	public String getFullAddress() {
		String address = firstName;

		if (StringUtils.hasText(lastName)) {
			address += " " + lastName;
		}
		if (StringUtils.hasText(addressLine1)) {
			address += ", " + addressLine1;
		}
		if (StringUtils.hasText(addressLine2)) {
			address += ", " + addressLine2;
		}
		if (StringUtils.hasText(city)) {
			address += ", " + city;
		}
		if (StringUtils.hasText(state)) {
			address += ", " + state;
		}
		if (StringUtils.hasText(country)) {
			address += ", " + country;
		}
		if (StringUtils.hasText(postalCode)) {
			address += ". Postal Code: " + postalCode;
		}
		if (StringUtils.hasText(phoneNumber)) {
			address += ". Phone Number: " + phoneNumber;
		}

		return address;
	}
}
