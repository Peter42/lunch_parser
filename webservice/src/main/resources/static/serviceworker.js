const CACHE_NAME = "lunch-menu-cache-v1";
const NO_CONNECTION_HTML = "noConnection.html";
const NO_CONNECTION_JPEG = "noConnection.jpg";
const NO_CONNECTION_CSS  = "noConnection.css";


async function install() {
	console.log("installing");
	const cache = await caches.open(CACHE_NAME);
	return cache.addAll([NO_CONNECTION_HTML, NO_CONNECTION_JPEG, NO_CONNECTION_CSS]);
}

async function fetchOrOffline(event) {
	try {  
		return await fetch(event.request);
	}
	catch(error) {
		url = getRelativeURL(event.request.url);
		var page = null;
		if(url.length == 0){
			page = NO_CONNECTION_HTML;
		}
		else if(url.endsWith(NO_CONNECTION_JPEG)){
			page = NO_CONNECTION_JPEG;
		}
		else if(url.endsWith(NO_CONNECTION_CSS)){
			page = NO_CONNECTION_CSS;
		}
		
		if(page != null) {
			const cache = await caches.open(CACHE_NAME);
			return cache.match(page);
		}
		else {
			throw error;
		}
	}
}

function getRelativeURL(url) {
	var scope = self.registration.scope;
	return url.substring(scope.length);
}

function wrapWaitUntil(func) {
	return function(event) {
		event.waitUntil(func(event));
	}
}

function wrapRespondWith(func) {
	return function(event) {
		event.respondWith(func(event));
	}
}

self.addEventListener('install', wrapWaitUntil(install) );
self.addEventListener('fetch',  wrapRespondWith(fetchOrOffline));