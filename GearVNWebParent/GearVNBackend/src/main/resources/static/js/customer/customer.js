// check duplicate password
function checkPasswordMatch(inputRePassword) {
	if (inputRePassword.value != $("#inputPassword").val()) {
		inputRePassword.setCustomValidity("Passwords do not match!");
	} else {
		inputRePassword.setCustomValidity("");
	}
}

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

function checkDuplicateEmail(form) {
	let customerId = $("#inputId").val();
	let customerEmail = $("#inputEmail").val();
	let csrfValue = $("input[name=_csrf]").val();

	let param = {
		id: customerId,
		email: customerEmail,
		_csrf: csrfValue
	};

	$.post(
		url,
		param,
		function(response) {
			if (response === "OK") {
				isChangedInputPassword();

				form.submit();
			} else if (response === "Duplicated") {
				showModalDialog("Warning",
					"There is another customer having the email "
					+ customerEmail);
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

function isChangedInputPassword() {
	let inputPassword = $("#inputPassword");
	let inputRePassword = $("#inputRePassword");
	if (!inputPassword.val() || !inputRePassword.val()) {
		inputPassword.val(originalPassword);
	}
}
