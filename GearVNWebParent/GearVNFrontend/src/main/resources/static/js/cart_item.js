// addProductToCart
$(document).ready(function() {

	decimalPointType = decimalPointType === "COMMA" ? "," : ".";
	thousandsPointType = thousandsPointType === "COMMA" ? "," : ".";


	$("#addProductToCart").on("click", function() {
		addProductToCart();
	});

});

function addProductToCart() {
	let quantity = $("#quantity").val();
	let url = contextPath + "cart/add/" + productId + "/" + quantity;

	$.ajax({
		type: "POST",
		url: url,
		// mặc định ko đc đổi xhr (XMLHttpRequest) trong beforeSend
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeader, csrfValue);
		},
	}).done(function(response) {
		console.log(response);
		showToastMessage(response);
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server or server encountered an error.");
	});

	// ko đặt trong hàm ready vì nó sẽ gọi tự động khi load page
	autoCloseToast();
}

function updateQuantity(productId, quantity) {
	let url = contextPath + "cart/update/" + productId + "/" + quantity;

	$.ajax({
		type: "PUT",
		url: url,
		// mặc định ko đc đổi xhr (XMLHttpRequest) trong beforeSend
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeader, csrfValue);
		},
	}).done(function(subTotal) {
		updateSubTotal(subTotal, productId);
		updateTotal();
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server or server encountered an error.");
		// ko đặt trong hàm ready vì nó sẽ gọi tự động khi load page
		autoCloseToast();
	});
}

function updateSubTotal(subTotal, productId) {
	// dùng file jquery.number.min.js tải bên ngoài về (đưa vào thư mục js) để format lại number
	const formattedSubTotal = $.number(subTotal, 2);
	$("#subTotal" + productId).text(formattedSubTotal);
}

function updateTotal() {
	let total = 0.0;
	let count = 0;

	$(".subTotal").each(function(_, element) {
		let value = element.textContent.trim();

		// Xác định vị trí cuối của dấu thập phân
		const lastIndex = value.lastIndexOf(decimalPointType);
		if (lastIndex !== -1) {
			let decimalPart = value.slice(lastIndex + 1).padEnd(decimalDigits, "0");
			value = value.slice(0, lastIndex) + decimalPart;
		}

		// Xóa tất cả dấu ngăn cách (ngàn + thập phân)
		value = value.replaceAll(",", "").replaceAll(".", "");

		// Chèn lại dấu '.' trước phần thập phân để parse
		if (value.length > decimalDigits) {
			let integerPart = value.slice(0, value.length - decimalDigits);
			let decimalPart = value.slice(value.length - decimalDigits);
			value = integerPart + "." + decimalPart;
		}

		total += parseFloat(value);
		count++;
	});

	if (count === 0) {
		$("#totalSection").addClass("d-none");
		$("#noCartItemSection").removeClass("d-none");
		return;
	}

	let formattedTotal;
	if (decimalPointType === thousandsPointType) {
		// Tạm thời đổi decimalPointType để format không bị nhầm
		const tempDecimal = decimalPointType === "." ? "," : ".";
		formattedTotal = $.number(total, decimalDigits, tempDecimal, thousandsPointType);
		formattedTotal = formattedTotal.replaceAll(tempDecimal, decimalPointType);
	} else {
		formattedTotal = $.number(total, decimalDigits, decimalPointType, thousandsPointType);
	}

	$("#total").text(formattedTotal);
}

function showToastMessage(message) {
	$("#toastAddProductMessage").text(message);
	$("#toastAddProduct").toast("show");
}

// đảm bảo toast tự động tắt
function autoCloseToast() {
	const toastElList = [].slice.call(document
		.querySelectorAll('.toast'));
	toastElList.forEach(function(toastEl) {
		new bootstrap.Toast(toastEl).show();
	});
};

// dùng cho file product_detail
function decreaseValue_productDetail() {
	let input = document.getElementById("quantity");
	let value = parseInt(input.value);
	if (value > 1) {
		input.value = value - 1;
	}
};
// dùng cho file product_detail
function increaseValue_productDetail() {
	let input = document.getElementById("quantity");
	let value = parseInt(input.value);
	input.value = value + 1;
};

// dùng cho file cart
function decreaseValue_cart(button) {
	const input = button.parentElement.querySelector('input');
	let value = parseInt(input.value);
	if (value > 1) {
		input.value = value - 1;
	}

	const productId = input.getAttribute('data-product-id');

	updateQuantity(productId, input.value);
}

// dùng cho file cart
function increaseValue_cart(button) {
	const input = button.parentElement.querySelector('input');
	let value = parseInt(input.value);
	input.value = value + 1;

	const productId = input.getAttribute('data-product-id');

	updateQuantity(productId, input.value);
}

function deleteCartItem(event, linkElement) {
	event.preventDefault();
	url = $(linkElement).attr("href");

	$.ajax({
		type: "DELETE",
		url: url,
		// mặc định ko đc đổi xhr (XMLHttpRequest) trong beforeSend
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeader, csrfValue);
		},
	}).done(function(response) {
		showToastMessage(response);

		// Tìm dòng (tr) chứa liên kết xóa
		const row = linkElement.closest('tr');

		// Xóa dòng
		row.remove();

		updateTotal();
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server or server encountered an error.");
	});

	// ko đặt trong hàm ready vì nó sẽ gọi tự động khi load page
	autoCloseToast();
}