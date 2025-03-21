// load category
$(document).ready(function() {
	// load combobox function
	dropdownBrands.change(function() {
		dropdownCategories.empty();
		getCategories();
	});
	// set categories cho brand đầu tiên
	getCategoryForNewForm();
});

function getCategoryForNewForm(){
	let inputCategoryId = $("#inputCategoryId");
	let editMode = false;
	
	// check thử element inputCategoryId có tồn tại không
	if (inputCategoryId.length){
		editMode = true;
	}
	
	if (!editMode){
		getCategories();
	}
}

function getCategories() {
	let brandId = dropdownBrands.val();
	let url = brand + brandId + "/categories";

	$.get(url, function(response) {
		$.each(response, function(index, category) {
			// tạo 1 thẻ html, set giá trị, add vào combobox
			$("<option>").val(category.id).text(category.name)
					.appendTo(dropdownCategories);
		})
	});
}