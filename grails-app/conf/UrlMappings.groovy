class UrlMappings {
	static mappings = {

		"/"{
			controller = "User"
			action = [GET:"notAllowed",POST:"postUser",PUT:"notAllowed",DELETE:"notAllowed"]
		}

		"/student/"{
			controller = "User"
			action = [GET:"notAllowed",POST:"notAllowed",PUT:"notAllowed",DELETE:"notAllowed"]
		}

		"/teacher/"{
			controller = "User"
			action = [GET:"notAllowed",POST:"notAllowed",PUT:"notAllowed",DELETE:"notAllowed"]
		}

   		"/student/$user_id"{
			controller = "User"
			action = [GET:"getStudent",POST:"notAllowed",PUT:"putStudent",DELETE:"deleteStudent"]
		}

		"/teacher/$user_id"{
			controller = "User"
			action = [GET:"getTeacher",POST:"notAllowed",PUT:"putTeacher",DELETE:"deleteTeacher"]
		}	
	}
}
