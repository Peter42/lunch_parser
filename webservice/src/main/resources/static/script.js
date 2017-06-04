var xhttp = new XMLHttpRequest();
xhttp.onreadystatechange = function() {
	if (this.readyState == 4 && this.status == 200) {
		render(JSON.parse(this.responseText));
	}
};
xhttp.open("GET", "./api/v1/", true);
xhttp.send();

function unixToString(unix, includeTime) {

	const dateTime = new Date(unix * 1000);

	var date = dateTime.toLocaleString(undefined, {
		day : '2-digit',
		month : '2-digit',
		year : 'numeric'
	});

	if (includeTime) {
		date = dateTime.toLocaleString(undefined, {
			hour : '2-digit',
			minute : '2-digit',
			second : '2-digit'
		}) + " " + date;
	}

	return date;
}

function render(data) {
	var div = document.getElementById("content");
	for (var i = 0; i < data.menus.length; ++i) {
		renderCantine(data.menus[i], div);
	}

	div = document.getElementById("time");
	time.innerHTML = "<b>Speiseplan für den "
			+ unixToString(data.menuForDay, false) + "</b> Stand: "
			+ unixToString(data.generationTime, true);
}

function renderCantine(cantine, parentDiv) {
	if (cantine == null) {
		return;
	}
	var div = document.createElement("div");
	div.className = "cantine";
	parentDiv.appendChild(div);

	var name = document.createElement("div");
	name.className = "cantinename";
	name.innerText = cantine.name;
	div.appendChild(name);

	for (var i = 0; i < cantine.lunchItems.length; ++i) {
		renderLunchItem(cantine.lunchItems[i], div);
	}
}

function renderLunchItem(lunchitem, parentDiv) {
	var div = document.createElement("div");
	div.className = "lunchitem";
	div.innerHTML = lunchitem.itemName;
	parentDiv.appendChild(div);
}

// register service worker
if ('serviceWorker' in navigator) {
	navigator.serviceWorker.register('serviceworker.js', {
		scope : './'
	});
}
