window.bunkiDatePicker = {
    init: function (selector) {
        if (typeof flatpickr === "undefined") {
            console.error("flatpickr no está cargado.");
            return;
        }

        flatpickr(selector, {
			locale: "es",
			dateFormat: "Y-m-d",
			altInput: true,
			altFormat: "d/m/Y",
			minDate: "today",
			disableMobile: true,
			allowInput: false,
			position: "below"
		});
    }
};