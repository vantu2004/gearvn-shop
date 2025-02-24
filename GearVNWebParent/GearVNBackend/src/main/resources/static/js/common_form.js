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