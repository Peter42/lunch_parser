var offset = 0;

function loadData() {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			render(JSON.parse(this.responseText));
		}
	};
	xhttp.open("GET", "./api/v1/+" + offset, true);
	xhttp.setRequestHeader("X-User-Preferences", btoa(JSON.stringify(localStorage)));
	xhttp.send();
	
	btnPrevDay.style.display = offset < 1 ? "none" : "";
}

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
	
	const max = Math.max(div.childElementCount, data.menus.length);

	for (var i = 0; i < max; ++i) {
		if(i < data.menus.length) {
			renderCantine(data.menus[i], div, div.childNodes[i]);
		}
		else {
			div.removeChild(div.childNodes[data.menus.length]);
		}
	}

	div = document.getElementById("time");
	time.innerHTML = "<b>Speiseplan für den "
			+ unixToString(data.menuForDay, false) + "</b><br>Stand: "
			+ unixToString(data.generationTime, true);
}

function renderCantine(cantine, parentDiv, recycleDiv) {
	if (cantine == null) {
		return;
	}
	
	let div, name;
	if (recycleDiv !== undefined) {
		div = recycleDiv;
		name = div.getElementsByClassName("cantinename")[0];
	}
	else {
		div = document.createElement("div");
		div.className = "cantine";
		parentDiv.appendChild(div);

		name = document.createElement("div");
		name.className = "cantinename";
		div.appendChild(name);
	}
	
	name.innerText = cantine.name;

	const max = Math.max(div.childElementCount-1, cantine.lunchItems.length);
	for (var i = 0; i < max; ++i) {
		if(i < cantine.lunchItems.length) {
			renderLunchItem(cantine.lunchItems[i], div, div.childNodes[i+1]);
		}
		else {
			div.removeChild(div.childNodes[cantine.lunchItems.length]);
		}
	}
}

function renderLunchItem(lunchitem, parentDiv, recycleDiv) {
	let div, spanName, spanContent, spanPrice;
	if (recycleDiv !== undefined) {
		div         = recycleDiv;
		spanName    = div.getElementsByClassName("lunchitemname")[0];
		spanContent = div.getElementsByClassName("lunchitemcomment")[0];
		spanPrice   = div.getElementsByClassName("lunchitemprice")[0];
	}
	else {
		div = document.createElement("div");
		div.className = "lunchitem";
		
		spanName = document.createElement("span");
		spanName.className = "lunchitemname";
		spanName.innerHTML = lunchitem.itemName;
		div.appendChild(spanName);
		
		spanComment = document.createElement("span");
		spanComment.className = "lunchitemcomment";
		spanComment.innerHTML = lunchitem.comment != null ? lunchitem.comment : "";
		div.appendChild(spanComment);
		
		spanPrice = document.createElement("span");
		spanPrice.className = "lunchitemprice";
		spanPrice.innerHTML = lunchitem.price == -1.0 ? "Price unknown" : lunchitem.price.toFixed(2);
		div.appendChild(spanPrice);

		parentDiv.appendChild(div);
	}
	
	spanName.innerHTML = lunchitem.itemName;
	spanComment.innerHTML = lunchitem.comment != null ? lunchitem.comment : "";
	spanPrice.innerHTML = lunchitem.price == -1.0 ? "Price unknown" : lunchitem.price.toFixed(2);
}

let btnPrevDay, btnNextDay;
window.onload = () => {
	btnPrevDay = document.getElementById("btnPrevDay");
	btnPrevDay.onclick = () => {
		offset--;
		loadData();
	};
	btnNextDay = document.getElementById("btnNextDay");
	btnNextDay.onclick = () => {
		offset++;
		loadData();
	};
	loadData();
};

// register service worker
if ('serviceWorker' in navigator) {
	navigator.serviceWorker.register('serviceworker.js', {
		scope : './'
	});
}
