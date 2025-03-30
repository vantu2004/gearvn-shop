let btnLoadCountryList = $("#btnLoadCountryList")
let btnCreateCountry = $("#btnCreateCountry")
let btnUpdateCountry = $("#btnUpdateCountry")
let btnDeleteCountry = $("#btnDeleteCountry")
let inputCountryName = $("#inputCountryName")
let inputCode = $("#inputCode")
let selectCountryList = $("#selectCountryList")

let contextPathForCountrySetting = "/" + window.location.pathname.split('/')[1];

$(document).ready(function() {
	// load country
	loadCountries("All countries have been loaded.");

	// refresh load country
	btnLoadCountryList.click(function() {
		loadCountries("All countries have been loaded.");
		changeFormStateToNew();
	});

	selectCountryList.on("change", function() {
		changeFormStateToSelectedCountry();
	});

	btnCreateCountry.click(function() {
		addCountry();
	});

	btnUpdateCountry.click(function() {
		updateCountry();
	});

	btnDeleteCountry.click(function() {
		deleteCountry();
	});
});

function loadCountries(message) {

	let url = contextPathForCountrySetting + "/countries/list";

	$.get(url, function(response) {
		selectCountryList.empty();

		$.each(response, function(index, country) {
			let optionValue = country.id + " - " + country.code;
			// tham số 1 là text, 2 là value
			selectCountryList.append(new Option(country.name, optionValue));
		});
	}).done(function() {
		showToastMessage(message);
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server or server encountered an error.");
	});
}

function showToastMessage(message) {
	$("#toastMessage").text(message);
	$(".toast").toast("show");
}

// khi click chọn 1 option thì disabled btnCreateCountry và enabled 2 button kia
function changeFormStateToSelectedCountry() {
	let selectedIndex = selectCountryList.prop("selectedIndex");

	if (selectedIndex >= 0) {
		let countryName = selectCountryList.find("option:selected").text();
		// value option có dạng "0 - VN", lấy phần sau dấu "-"
		let countryCode = selectCountryList.val().split(" - ")[1].trim();

		// Điền vào input
		inputCountryName.val(countryName);
		inputCode.val(countryCode);

		btnCreateCountry.prop("disabled", true);
		btnUpdateCountry.prop("disabled", false);
		btnDeleteCountry.prop("disabled", false);
	}
}

// khi lick refresh hoặc sau khi create/update/delete thì đưa về trạng thái ban đầu
function changeFormStateToNew() {
	// Cập nhật trạng thái button
	btnCreateCountry.prop("disabled", false);
	btnUpdateCountry.prop("disabled", true);
	btnDeleteCountry.prop("disabled", true);

	inputCountryName.val("");
	inputCode.val("");

	inputCountryName.focus();
}

function addCountry() {
	let url = contextPathForCountrySetting + "/countries/save";

	let countryName = inputCountryName.val();
	let countryCode = inputCode.val();

	if (!countryName.trim() || !countryCode.trim()) {
		showToastMessage("Country name or Country code cannot be left blank");
	} else {
		let jsonData = { name: countryName, code: countryCode };

		$.ajax({
			type: "POST",
			url: url,
			// mặc định ko đc đổi xhr (XMLHttpRequest) trong beforeSend
			beforeSend: function(xhr) {
				xhr.setRequestHeader(csrfHeader, csrfValue);
			},
			data: JSON.stringify(jsonData),
			contentType: "application/json"
		}).done(function() {
			loadCountries(`The new country ${countryName} with code ${countryCode} has been added.`);
			changeFormStateToNew();
		}).fail(function() {
			showToastMessage("ERROR: Could not connect to server or server encountered an error.");
		});
	}
}

function updateCountry() {
	let url = contextPathForCountrySetting + "/countries/save";

	let countryId = selectCountryList.val().split(" - ")[0].trim();
	let countryName = inputCountryName.val();
	let countryCode = inputCode.val();

	if (!countryName.trim() || !countryCode.trim()) {
		showToastMessage("Country name or Country code cannot be left blank");
	} else {
		let jsonData = { id: countryId, name: countryName, code: countryCode };

		$.ajax({
			type: "POST",
			url: url,
			// mặc định ko đc đổi xhr (XMLHttpRequest) trong beforeSend
			beforeSend: function(xhr) {
				xhr.setRequestHeader(csrfHeader, csrfValue);
			},
			data: JSON.stringify(jsonData),
			contentType: "application/json"
		}).done(function() {
			loadCountries(`The country with id ${countryId} has been updated.`);
			changeFormStateToNew();
		}).fail(function() {
			showToastMessage("ERROR: Could not connect to server or server encountered an error.");
		});
	}
}

function deleteCountry() {

	let countryId = selectCountryList.val().split(" - ")[0].trim();
	let url = contextPathForCountrySetting + "/countries/delete/" + countryId;

	$.ajax({
		url: url,
		type: "DELETE",
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeader, csrfValue);
		}
	}).done(function() {
		loadCountries(`The country with id ${countryId} has been deleted`);
		changeFormStateToNew();
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server or server encountered an error.");
	});

}
