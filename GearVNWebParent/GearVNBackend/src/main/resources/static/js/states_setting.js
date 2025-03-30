let contextPathForStateSetting = "/" + window.location.pathname.split('/')[1];

let selectCountryListTabState = $("#selectCountryListTabState");
let selectStateList = $("#selectStateList");
let inputStateName = $("#inputStateName");
let btnReloadStateList = $("#btnLoadStateList");
let btnCreateState = $("#btnCreateState");
let btnUpdateState = $("#btnUpdateState");
let btnDeleteState = $("#btnDeleteState");

$(document).ready(function() {

	selectCountryListTabState.on("change", function() {
		loadStateList("States/provinces have been loaded.");
	});

	selectStateList.on("change", function() {
		selectStateListChange();
	});

	btnReloadStateList.click(function() {
		loadStateList("States/provinces have been loaded.");
		setInitialState();
	});

	btnCreateState.click(function() {
		addState();
	});

	btnUpdateState.click(function() {
		updateState();
	});

	btnDeleteState.click(function() {
		deleteState();
	});
});

function showToastMessageTabState(message) {
	$("#toastStateMessage").text(message);
	$("#toastState").toast("show");
}

function loadStateList(message) {

	let countryId = selectCountryListTabState.val().split(" - ")[0].trim();

	let url = contextPathForStateSetting + "/states/list_by_country/" + countryId;

	$.get(url, function(response) {
		selectStateList.empty();

		$.each(response, function(index, state) {
			let optionValue = state.id;
			// tham số 1 là text, 2 là value
			selectStateList.append(new Option(state.name, optionValue));
		});
	}).done(function() {
		showToastMessageTabState(message);
	}).fail(function() {
		showToastMessageTabState("ERROR: Could not connect to server or server encountered an error.");
	});
}

function setInitialState() {
	btnCreateState.prop("disabled", false);
	btnUpdateState.prop("disabled", true);
	btnDeleteState.prop("disabled", true);

	inputStateName.val("");

	inputStateName.focus();
}

function selectStateListChange() {
	let selectedIndex = selectStateList.prop("selectedIndex");

	if (selectedIndex >= 0) {
		let stateName = selectStateList.find("option:selected").text();

		inputStateName.val(stateName);

		btnCreateState.prop("disabled", true);
		btnUpdateState.prop("disabled", false);
		btnDeleteState.prop("disabled", false);
	}
}

function addState() {
	let url = contextPathForStateSetting + "/states/save";

	let stateName = inputStateName.val();

	let countryName = selectCountryListTabState.find("option:selected").text();
	let countryId = selectCountryListTabState.val().split(" - ")[0].trim();
	let countryCode = selectCountryListTabState.val().split(" - ")[1].trim();

	if (!stateName.trim()) {
		showToastMessageTabState("State/Province name cannot be left blank");
	} else {
		// phải ánh xạ đúng tên với field trong StateDTO
		let jsonData = { name: stateName, countryDTO: { id: countryId, name: countryName, code: countryCode } };

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
			loadStateList(`The new state/province ${stateName} has been added.`);
			setInitialState();
		}).fail(function() {
			showToastMessageTabState("ERROR: Could not connect to server or server encountered an error.");
		});
	}
}

function updateState() {
	let url = contextPathForStateSetting + "/states/save";

	let stateId = selectStateList.val();
	let stateName = inputStateName.val();

	let countryName = selectCountryListTabState.find("option:selected").text();
	let countryId = selectCountryListTabState.val().split(" - ")[0].trim();
	let countryCode = selectCountryListTabState.val().split(" - ")[1].trim();

	if (!stateName.trim()) {
		showToastMessageTabState("State/Province name cannot be left blank");
	} else {
		// phải ánh xạ đúng tên với field trong StateDTO
		let jsonData = { id: stateId, name: stateName, countryDTO: { id: countryId, name: countryName, code: countryCode } };
		console.log(jsonData)
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
			loadStateList(`The state/province with id ${stateId} has been updated.`);
			setInitialState();
		}).fail(function() {
			showToastMessageTabState("ERROR: Could not connect to server or server encountered an error.");
		});
	}
}

function deleteState() {

	let stateId = selectStateList.val();
	let url = contextPathForCountrySetting + "/states/delete/" + stateId;

	$.ajax({
		url: url,
		type: "DELETE",
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeader, csrfValue);
		}
	}).done(function() {
		loadStateList(`The state with id ${stateId} has been deleted`);
		setInitialState();
	}).fail(function() {
		showToastMessageTabState("ERROR: Could not connect to server or server encountered an error.");
	});

}