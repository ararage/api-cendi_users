package com.api.util

import java.text.MessageFormat
import grails.transaction.Transactional
import users.exceptions.NotFoundException
import users.exceptions.ConflictException
import users.exceptions.BadRequestException
import users.ValidAccess
import grails.converters.*
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class UtilitiesService {

	public def existUser(def userResult, def user_id){
        if (!userResult){
            throw new NotFoundException("The user with the user_id = "+user_id+" not found")
        }
        
        //if(userResult.status.equals("blocked")){
        //    throw new NotFoundException("The user with the user_id = "+user_id+" its blocked")
        //}else if(userResult.status.equals("deleted")){
        //    throw new NotFoundException("The user with the user_id = "+user_id+" was deleted")
        //}else 
        if(userResult.status.equals("history")){
            throw new NotFoundException("The user with the user_id = "+user_id+" was graduated.")
        }
    }

    public def getStudentByAdmin(def newUser){
    	Map jsonResult = [:]
		jsonResult.personalId = newUser.personalId
		jsonResult.email = newUser.email
		jsonResult.name = newUser.name
		jsonResult.fName = newUser.fName
		jsonResult.lName = newUser.lName
		jsonResult.dateDelete = newUser.dateOfBirth
		jsonResult.sex = newUser.sex
		jsonResult.profilePicture = newUser.profilePicture
		jsonResult.status = newUser.status
		jsonResult.type = newUser.type
		jsonResult.age = newUser.age
		jsonResult.group=newUser.group
		jsonResult.school=newUser.school
		println "Le result "+jsonResult
    	jsonResult
    }
    
}
