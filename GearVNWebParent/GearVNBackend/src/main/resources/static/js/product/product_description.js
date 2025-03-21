$(document).ready(function() {
	// set richtext function
	$("#shortDescription").richText();
	$("#fullDescription").richText();
});

function setDefaultValueTextArea() {
	let shortDescription = $('#shortDescription');
	let fullDescription = $('#fullDescription');
	let content_shortDescription = shortDescription.val().trim();
	let content_fullDescription = fullDescription.val().trim();

	if (content_shortDescription === '<div><br></div>'
			|| content_shortDescription === '<br>') {
		shortDescription.val('');
	}
	if (content_fullDescription === '<div><br></div>'
			|| content_fullDescription === '<br>') {
		fullDescription.val('');
	}
}