(() => {
	var query = {}
	var url = decodeURIComponent(location.href.replaceAll("+", " "))
	var things = url.split("?").slice(1).join("?").split("#")[0].split("&")
	if (Boolean(things[0])) {
		for (var a = 0; a < things.length; a++) {
			var name =  things[a].split("=")[0]
			var value = things[a].split("=")[1]
			query[name] = value
		}
	} else {
		query = {}
	}
	window.query = query
	window.query_get = function (item, def) {
		if (Object.keys(window.query).includes(item)) {
			return window.query[item]
		} else return def
	}
})();