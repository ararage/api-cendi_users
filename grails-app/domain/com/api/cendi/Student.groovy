package com.api.cendi

class Student{
    
    String personalId
    String group
    String school
    String email
    String name
    String fName
    String lName
    Date dateOfBirth
    Date registrationDate = new Date()
    Date dateLastUpdate = new Date()
    Date dateLastAccess = new Date()
    Date dateDelete
    String sex
    String profilePicture
    String status// = "active"
    String type
    String age
    

    static constraints = {
        personalId      (nullable:false, blank:false, unique:true)
        email           (nullable:false, blank:false, email:true, unique:false)
        name            (nullable:false, blank:false, maxSize:256)
        fName           (nullable:false, blank:false, maxSize:256)
        lName           (nullable:false, blank:false, maxSize:256)
        dateOfBirth     (nullable:false, blank:false)
        dateDelete      (nullable:true, blank:true)
        sex             (nullable:false, blank:false, inList:["Male","Female"])
        profilePicture  (nullable:false, blank:true)
        status          (nullable:false, blank:false, inList:['active','deleted', 'blocked', 'history'])
        //type            (nullable:false, blank:false, inList:['admin','teacher','student'])
        type            (nullable:false, blank:false, inList:['student'])
        group (blank:false, nullable:false, unique:false)
        school (blank:false, nullable:false, unique:false)
    }
}
