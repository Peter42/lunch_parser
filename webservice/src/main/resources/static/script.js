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
