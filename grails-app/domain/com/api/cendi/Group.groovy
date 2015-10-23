package com.api.cendi

class Group {

	String group_name
	String teacher_id
	String school_id

    static constraints = {
    	group_name      (nullable:false, blank:false, unique:false)
    	teacher_id      (nullable:false, blank:false, unique:false)
    	school_id      (nullable:false, blank:false, unique:false)
    }
}
