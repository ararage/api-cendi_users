class UrlMappings {
	static mappings = {

		"/"{
			controller = "User"
			action = [GET:"notAllowed",POST:"postUser",PUT:"notAllowed",DELETE:"notAllowed"]
		}

   		"/$user_id"{
			controller = "User"
			action = [GET:"getUser",POST:"notAllowed",PUT:"putUser",DELETE:"deleteUser"]
		}	
	}
}
