// vì mặc định cấu hình chỉ 1mb nên cấu hình thêm bên application tăng lên 5mb
let MAX_FILE_SIZE = 5242880; // byte

$(document)
	.ready(
		function() {
			$("#inputPhotos")
				.change(
					function() {
						if (!checkFileSize(this)) {
							// Lấy context-path
							let contextPath = window.location.pathname
								.split('/')[1];
							// set lại default image
							$("#inputPhotosPreview")
								.attr(
									"src",
									"/"
									+ contextPath
									+ "/images/LogoGearvn.png");
							this.value = "";

							return;
						}

						showImageThumbnail(this);
					});
		});

function checkFileSize(fileInput) {
	let fileSize = fileInput.files[0].size;

	if (fileSize > MAX_FILE_SIZE) {
		alert("You must choose an image less than " + MAX_FILE_SIZE
			+ " bytes!");
		return false;
	} else {
		return true;
	}
}

function showImageThumbnail(fileInput) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#inputPhotosPreview").attr("src", e.target.result);
	};

	reader.readAsDataURL(file);
}

function checkPasswordMatch(inputRePassword) {
	if (inputRePassword.value != $("#inputPassword").val()) {
		inputRePassword.setCustomValidity("Passwords do not match!");
	} else {
		inputRePassword.setCustomValidity("");
	}
}

// check trùng categoryName/categoryAlias - create_category.html/update_category.html
function checkDuplicate(form) {
	let id = $("#inputId").val();
	let name = $("#inputName").val();
	let alias = $("#inputAlias").val();
	let csrfValue = $("input[name=_csrf]").val();

	let param = {
		id: id,
		name: name,
		alias: alias,
		_csrf: csrfValue
	};

	$.post(checkDuplicateUrl, param, function(response) {
		if (response === "OK") {
			form.submit();
		} else if (response === "DuplicateName") {
			showModalDialog("Warning", "Name ​​cannot be duplicated");
		} else if (response === "DuplicateAlias") {
			showModalDialog("Warning", "Alias ​​cannot be duplicated");
		} else {
			showModalDialog("Error", "Unknow response from server.");
		}
	}).fail(function() {
		showModalDialog("Error", "Could not connect to the server.");
	});

	// Chặn submit mặc định
	return false;
}

// check trùng brandName - create_brand.html/update_brand.html
function checkDuplicateName(form) {
	let id = $("#inputId").val();
	let name = $("#inputName").val();
	let csrfValue = $("input[name=_csrf]").val();

	let param = {
		id: id,
		name: name,
		_csrf: csrfValue
	};

	$.post(
		checkDuplicateUrl,
		param,
		function(response) {
			if (response === "OK") {
				form.submit();
			} else if (response === "Duplicated") {
				showModalDialog("Warning",
					"There is another brand having the name "
					+ name);
			} else {
				showModalDialog("Error",
					"Unknow response from server.");
			}
		}).fail(function() {
			showModalDialog("Error", "Could not connect to the server.");
		});

	// Chặn submit mặc định
	return false;
}

function showModalDialog(title, message) {
	$("#modalTitle").text(title);
	$("#modalMessage").text(message);
	// modal() có sẵn dùng để show/hide modal
	$('#duplicateModal').modal('show');
}

// hiện tag categories đã chọn - create_brand.html/update_brand.html
$(document)
	.ready(
		function() {
			let $dropdownCategories = $("#categories");
			let $divChosenCategories = $("#chosenCategories");

			function showChosenCategories() {
				$divChosenCategories.empty();
				$dropdownCategories
					.find("option:selected")
					.each(
						function() {
							let catName = $(this)
								.text().trim()
								.replace(/-/g, "");
							$divChosenCategories
								.append(`<span class="badge bg-primary m-1">${catName}</span>`);
						});
			}

			$dropdownCategories.on("change",
				showChosenCategories);
			showChosenCategories(); // Gọi khi trang vừa load
		});