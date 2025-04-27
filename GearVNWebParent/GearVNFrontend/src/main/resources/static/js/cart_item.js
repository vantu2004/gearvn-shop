// addProductToCart
$(document).ready(function() {
	
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

function decreaseValue() {
	let input = document.getElementById("quantity");
	let value = parseInt(input.value);
	if (value > 1) {
		input.value = value - 1;
	}
};

function increaseValue() {
	let input = document.getElementById("quantity");
	let value = parseInt(input.value);
	input.value = value + 1;
};