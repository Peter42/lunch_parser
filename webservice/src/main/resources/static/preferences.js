var xhttp = new XMLHttpRequest();
xhttp.onreadystatechange = function() {
	if (this.readyState == 4 && this.status == 200) {
		render(JSON.parse(this.responseText));
	}
};
xhttp.open("GET", "./api/v1/preferences", true);
xhttp.send();

function render(preferences) {
	for(var i = 0; i < preferences.length; ++i) {
		renderPref(preferences[i]);
	}
}

function renderPref(pref) {
	console.log(pref);
	var div = document.createElement("div");
	switch(pref.type) {
	case "boolean":
		var checked = getBoolean(pref.key, pref.defaultValue) ? "checked " : "";
		div.innerHTML = "<input " + checked + " type=\"checkbox\">" + pref.name;
		div.firstElementChild.onchange = function() {
			updatedBoolean(pref.key, this.checked);
		};
		break;
		
	case "enum":
		var selectedValue = getEnum(pref.key, pref.defaultValue);
		var innerHTML = pref.name + ": <select>";
		for(var key in pref.values){
			if(!pref.values.hasOwnProperty(key)) continue;
			var selected = selectedValue === key ? " selected" : "";
			innerHTML += "<option" + selected + " value=\""+ key +"\">" + pref.values[key] + "</option>";
		}
		innerHTML += "</select>";
		div.innerHTML= innerHTML;
		div.firstElementChild.onchange = function() {
			updatedEnum(pref.key, this.value);
		};
		break;
		
	default:
		div.innerHTML = pref.name + " is not supported by the UI";
	}
	
	content.appendChild(div);
}

function updatedBoolean(key, value) {
	localStorage.setItem(key, value);
}

function updatedEnum(key, value) {
	localStorage.setItem(key, value);
}

function getBoolean(key, defaultValue) {
	if(!localStorage.hasOwnProperty(key)){
		return defaultValue;
	}
	return localStorage.getItem(key) === "true";
}

function getEnum(key, defaultValue) {
	if(!localStorage.hasOwnProperty(key)){
		return defaultValue;
	}
	return localStorage.getItem(key);
}