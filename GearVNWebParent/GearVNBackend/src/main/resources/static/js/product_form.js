let MAX_EXTRA_FILE_SIZE = 5242880; // 5MB

// Lấy context-path
let contextPath = window.location.pathname.split('/')[1];

$(document).ready(function() {
	$(document).on("change", ".image-input", function() {
		if (!checkExtraFileSize(this)) {
			// Reset ảnh về mặc định
			$(this).closest(".image-upload-container").find(".image-preview")
				.attr("src", "/" + contextPath + "/images/LogoGearvn.png");
			this.value = "";
			$(this).removeAttr("data-has-image"); // Xóa trạng thái đã chọn ảnh trước đó
			return;
		}

		showExtraFileThumbnail(this);

		// Nếu input chưa có ảnh trước đó thì thêm input mới
		if (!$(this).attr("data-has-image")) {
			$(this).attr("data-has-image", "true");
			addExtraFileThumbnail();
		}
	});
});

function checkExtraFileSize(fileInput) {
	let fileSize = fileInput.files[0].size;
	if (fileSize > MAX_EXTRA_FILE_SIZE) {
		alert("Bạn phải chọn ảnh nhỏ hơn 5MB!");
		return false;
	}
	return true;
}

function showExtraFileThumbnail(fileInput) {
	let reader = new FileReader();
	reader.onload = function(e) {
		$(fileInput).closest(".image-upload-container").find(".image-preview")
			.attr("src", e.target.result);
	};
	reader.readAsDataURL(fileInput.files[0]);
}

function addExtraFileThumbnail() {
	let newExtraFileInput = `
	<div class="form-group mb-3 row">
		<label for="inputPhotos" class="col-sm-3 col-form-label">Extra Images</label>
		<div class="col-sm-9">
			<div class="image-upload-container">
				<div class="d-flex align-items-center image-upload mb-2">
					<input type="file" class="form-control me-2 image-input"
						accept=".png, .jpg, .jpeg" name="extraMultipartFile">
					<button class="btn btn-danger">
						<i class="fas fa-times fa-lg"></i>
					</button>
				</div>
				<div>
					<img src="${defaultImage}" width="50%" height="50%"
						class="d-inline-block align-top image-preview"
						style="border-radius: 10px;" alt="image">
				</div>
			</div>
		</div>
	</div>`;

	$(".parent-images").append(newExtraFileInput);
}

// Xóa input ảnh khi bấm nút X
$(document).on("click", ".btn-danger", function() {
	$(this).closest(".form-group").remove();
});

