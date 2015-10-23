package com.api.cendi

import java.security.SecureRandom

class Teacher {
    
    private SecureRandom random = new SecureRandom();
    
    String personalId
    //String group
    
    String school_id
    
    String email
    String password = newPass()
    
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
    
    List groups = []
    
    static constraints = {
        personalId      (nullable:false, blank:false, unique:true)
        school_id       (blank:false, nullable:false, unique:false)
        email           (nullable:false, blank:false, email:true, unique:false)
        password        (nullable:false, blank:false, unique:false, minLenght: 8, matches: /^[\d\p{L}]*$/)
        name            (nullable:false, blank:false, maxSize:256)
        fName           (nullable:false, blank:false, maxSize:256)
        lName           (nullable:false, blank:false, maxSize:256)
        dateOfBirth     (nullable:false, blank:false)
        dateDelete      (nullable:true, blank:true)
        sex             (nullable:false, blank:false, inList:["Male","Female"])
        profilePicture  (nullable:false, blank:true, unique:false)
        status          (nullable:false, blank:false, inList:['active','blocked', 'history'])
        //type            (nullable:false, blank:false, inList:['admin','teacher','student'])
        type            (nullable:false, blank:false, inList:['teacher','admin'],)
        groups           (blank:false, nullable:false, unique:false)
        
    }
    
    private String newPass() {
        return new BigInteger(65, random).toString(32);
    }
}
