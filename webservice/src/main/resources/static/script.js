var xhttp = new XMLHttpRequest();
xhttp.onreadystatechange = function() {
	if (this.readyState == 4 && this.status == 200) {
		render(JSON.parse(this.responseText));
	}
};
xhttp.open("GET", "./api/v1/", true);
xhttp.send();

function render(data) {
	var div = document.getElementById("content");
	for (var i = 0; i < data.length; ++i) {
		renderCantine(data[i], div);
	}
}

function renderCantine(cantine, parentDiv) {
	var div = document.createElement("div");
	div.className = "cantine";
	parentDiv.appendChild(div);
	for (var i = 0; i < cantine.length; ++i) {
		renderLunchItem(cantine[i], div);
	}
}

function renderLunchItem(lunchitem, parentDiv) {
	var div = document.createElement("div");
	div.className = "lunchitem";
	div.innerHTML = lunchitem.itemName;
	parentDiv.appendChild(div);
}
