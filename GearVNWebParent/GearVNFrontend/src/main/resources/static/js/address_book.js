let selectCountryList = $("#selectCountryList");
let selectStateList = $("#selectStateList");
let inputState = $("#inputState");

$(document).ready(function() {

	// load stateList by country
	selectCountryList.on("change", function() {
		loadStateList();
	});

});

function loadStateList() {
	let contextPathForStateSetting = "/" + window.location.pathname.split('/')[1];
	let countryId = selectCountryList.val();
	let url = contextPathForStateSetting + "/states/list_by_country/" + countryId;

	$.get(url, function(response) {
		selectStateList.empty();

		$.each(response, function(index, state) {
			console.log(state.name)
			// state.name là giá trị hiển thị và cũng là value
			selectStateList.append(
				$('<option>').attr('value', state.name)
			);
		});

	}).done(function() {
		inputState.val("");
	}).fail(function() {
		alert("ERROR: Could not connect to server or server encountered an error.");
	});
}