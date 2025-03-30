$(document).ready(function() {
	customizeTabs();
})

function customizeTabs() {
	let urlHash = window.location.hash;

	// Kích hoạt tab tương ứng với hash trên URL nếu có
	if (urlHash) {
		$('.nav-pills a[href="' + urlHash + '"]').tab('show');
	}

	// Cập nhật hash trên URL khi chuyển tab
	$('.nav-pills a').on('shown.bs.tab', function(e) {
		history.replaceState(null, null, e.target.hash);
	});
}
