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
		div.innerHTML = "<input " + checked + " onchange=\"updatedBoolean('" + pref.key + "', this.checked)\" type=\"checkbox\">" + pref.name;
		break;
		
	case "enum":
		var selectedValue = getEnum(pref.key, pref.defaultValue);
		var innerHTML = pref.name + ": <select onchange=\"updatedEnum('" + pref.key + "', this.selectedOptions[0].value)\">";
		for(var key in pref.values){
			if(!pref.values.hasOwnProperty(key)) continue;
			var selected = selectedValue === key ? " selected" : "";
			innerHTML += "<option" + selected + " value=\""+ key +"\">" + pref.values[key] + "</option>";
		}
		innerHTML += "</select>";
		div.innerHTML= innerHTML;
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