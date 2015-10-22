package com.api.cendi

import java.text.MessageFormat
import grails.transaction.Transactional
import users.exceptions.NotFoundException
import users.exceptions.ConflictException
import users.exceptions.BadRequestException
//import users.ValidAccess
import com.api.util.UtilitiesService
import grails.converters.*
import com.api.cendi.Student
import com.api.cendi.Teacher
//import com.api.cendi.School
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class UserService {

	//def validAccess = new ValidAccess()

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
            
            /*
                def getSchool(def params, def dominio){
        Map jsonResult = [:]
        def schoolResult
        if(!params.school_id){
            throw new NotFoundException("Invalid URL, missing school_id")
        }

        schoolResult = School.findBySchool_id(params.school_id)
        
        new UtilitiesService().existSchool(schoolResult,params.school_id)

        jsonResult = new UtilitiesService().fillSchoolResult(schoolResult)
        jsonResult
    }

            */
            /*def newSchool = School.findBySchool_id(jsonUser?.school_id)
            
            if(!newSchool){
                throw new ConflictException("Invalid school_id")
            }*/
            
            new UtilitiesService().existSchool(jsonUser?.school_id)
            
            newUser.school_id = jsonUser?.school_id
            

            if(!newUser.validate()) {
                newUser.errors.allErrors.each {
                    responseMessage += MessageFormat.format(it.defaultMessage, it.arguments) + " "
                }
                throw new BadRequestException(responseMessage)
            }
            newUser.save(flush:true)
            println "Usuario "+newUser.personalId
            jsonResult = new UtilitiesService().getStudentByAdmin(newUser)
        }

        jsonResult
    }
    
    public def searchStudent(def dominio,def params){
        Map jsonResult  = [:]
        def queryMap    = [:]
        
        def userBoleta
        //def userPassword
        def tokenAdmin
        
        def student
        
        def SEARCH_PARAMS_MAP =[
            boleta:"boleta",
            //password: "password",
            admin:"admin"
        ]
        
        params.each { key, value ->
            def newKey = SEARCH_PARAMS_MAP[key]
            if (newKey){
                queryMap[newKey]=value
                if (newKey.equals("boleta")){
                    userBoleta = value
                }
                /*if (newKey.equals("password")){
                    userPassword = value
                }*/
                if (newKey.equals("admin")){
                    tokenAdmin = value
                }

            }
        }
        
        if (!queryMap){
            throw new BadRequestException("Bad Request call, can't find the search params.")
        }
        
        tokenAdminValid(tokenAdmin)
        
        def studentCriteria = Student.createCriteria()
        
        def result = studentCriteria.list(){
            eq("personalId",userBoleta)
            //eq("password",userPassword)
        }
        
        if(result.size()<1){
            throw new NotFoundException("Can't find that user.")
        }
        
        def personalId
        def status
        def userType
        
        result.each{
            personalId      = it.personalId
            status      = it.status
            userType    = it.type
        }
        
        /*Verificar si esta bloqueado o en un estado que no permita iniciar sesion*/
        
        if (status.equals("history")){
            throw new NotFoundException("Very old student, sorry.")
        }else if(status.equals("blocked")){
            throw new NotFoundException("Blocked student.")
        }
        
        student = Student.findByPersonalId(userBoleta)
        student.dateLastAccess = new Date()
        student.save(flush:true)
        
        jsonResult.user_id      = personalId
        jsonResult.status       = status
        jsonResult.user_type    = userType
        
        jsonResult
    }
    
    def tokenAdminValid(def token){
        if(!token){
            throw new BadRequestException("Access Invalid, Admin not found")
        }
        if (!token.equals("CENDIADMIN_T4FG637F")){
            throw new NotFoundException("Access admin = "+token+" is not found")
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
}
