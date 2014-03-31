class UrlMappings {

	static mappings = {
        "/$controller?/$actorName?"{
            action = [GET: "get", PUT: "put", POST: "post", DELETE: "delete"]
        }
        "/"(view:"/index")
        "/help"(view:"/index")
	}
}
