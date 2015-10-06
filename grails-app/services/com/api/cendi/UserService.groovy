package com.api.cendi

import java.text.MessageFormat
import grails.transaction.Transactional
import users.exceptions.NotFoundException
import users.exceptions.ConflictException
import users.exceptions.BadRequestException
import users.ValidAccess
import grails.converters.*
import com.api.cendi.Student
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class UserService {

	def validAccess = new ValidAccess()

    public def getUser(def params,def dominio){
        Map jsonResult = [:]
        def userResult

        if(!params.user_id){
             throw new NotFoundException("Invalid URL, missing user_id")
        }

        userResult = Student.findByPersonalId(params.user_id)

        existUser(userResult,params.user_id)

        //jsonResult = getResultAdmin(userResult)

        println "Le user type "+userResult.type
        if(userResult.type.equals("student")){
            jsonResult = getStudentByAdmin(userResult)
        }
        jsonResult
    }

    private def existUser(def userResult, def user_id){
        if (!userResult){
            throw new NotFoundException("The user with the user_id = "+user_id+" not found")
        }
        if(userResult.status.equals("blocked")){
            throw new NotFoundException("The user with the user_id = "+user_id+" its blocked")
        }else if(userResult.status.equals("deleted")){
            throw new NotFoundException("The user with the user_id = "+user_id+" was deleted")
        }else if(userResult.status.equals("history")){
            throw new NotFoundException("The user with the user_id = "+user_id+" was graduated.")
        }
    }

    public def getUsers(def params,def dominio) {
        Map SEARCH_PARAMS_MAP = [
                school          :   "school",
                group           :   "group",
                status          :   "status",
                type            :   "type",
                age             :   "age"
        ]

        //println "Llegue"
    	Map jsonResult = [:]

        if(!params.user_id){
            throw new NotFoundException("Bad URL ==> missing user_id!!!")
        }

        //if(params.access_token){
            
        //}else{

        //}

    	jsonResult.ad = 2
    	jsonResult
    }

    public def putUser(def params,def dominio,def json){
        Map jsonResult = [:]
        def responseMessage = ''
        def usuario 

        if(!params.user_id){
             throw new NotFoundException("Invalid URL, missing user_id")
        }

        if(!json.type){
            throw new ConflictException("Bad JSON, type is missing!!!")
        }


    }

    public def postUser(def jsonUser,def dominio){
    	Map jsonResult = [:]
    	def responseMessage = ''
    	//def newUser
    	if(!jsonUser){
    		throw new ConflictException("Empty JSON!")
    	}

    	if(!jsonUser.type){
    		throw new ConflictException("Bad JSON: type not found on the json!")
    	}

    	if(jsonUser.dateOfBirth){
    		 try{
                jsonResult.dateOfBirth = ISODateTimeFormat.dateTimeParser().parseDateTime(jsonUser?.dateOfBirth).toDate()
            }catch(Exception e){
                throw new BadRequestException("Wrong date format in date_of_birth. Must be ISO json format")
            }
    	}else{
    		throw new ConflictException("Bad JSON: birth not found on the json!")
    	}

    	if(jsonUser.type.equals("student")){
    		Student newUser = new Student()
    		
    		newUser.personalId  = jsonUser?.personalId
    		newUser.email = jsonUser?.email
    		newUser.name = jsonUser?.name
    		newUser.fName = jsonUser?.fName
    		newUser.lName = jsonUser?.lName
    		newUser.dateOfBirth = jsonResult?.dateOfBirth
    		newUser.sex = jsonUser?.sex
    		newUser.profilePicture = jsonUser?.profilePicture
    		newUser.status = jsonUser?.status
    		newUser.type = jsonUser?.type
    		newUser.age = jsonUser?.age
    		newUser.group  = jsonUser?.group
    		newUser.school = jsonUser?.school

    		if(!newUser.validate()) {
            	newUser.errors.allErrors.each {
                	responseMessage += MessageFormat.format(it.defaultMessage, it.arguments) + " "
            	}
            	throw new BadRequestException(responseMessage)
			}
			newUser.save(flush:true)
			println "Usuario "+newUser.personalId
			jsonResult = getStudentByAdmin(newUser)
    	}

    	jsonResult
    }

    private def getStudentByAdmin(def newUser){
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
