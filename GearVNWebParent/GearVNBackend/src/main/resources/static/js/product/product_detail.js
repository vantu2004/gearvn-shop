$(document).ready(function() {
	// Xóa form-group khi nhấn nút xóa
	$(document).on("click", ".delete-detailSection", function() {
		$(this).closest(".form-group").remove();
	});

	// Thêm form-group mới khi nhấn nút Add
	window.addNextDetailSection = function() {
		let newDetailSection = `
            <div class="form-group mb-3 row">
			
				<!-- dùng để update product details -->
				<input type="hidden" id="inputDetailId" name="detailIds"
					value="0">
			
                <div class="col-sm-5">
                    <input type="text" class="form-control" placeholder="Enter detail name" name="detailNames">
                </div>
                <div class="col-sm-5">
                    <input type="text" class="form-control" placeholder="Enter detail value" name="detailValues">
                </div>
                <div class="col-sm-2 text-end">
                    <button class="btn btn-danger delete-detailSection">
                        <i class="fas fa-times fa-lg"></i>
                    </button>
                </div>
            </div>
        `;

		$(".parent-details").append(newDetailSection);
	};
});