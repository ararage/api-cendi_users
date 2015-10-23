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
import com.api.cendi.Group
//import com.api.cendi.School
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class UserService {

	//def validAccess = new ValidAccess()

    public def postUser(def jsonUser,def dominio){
        Map jsonResult = [:]
        def responseMessage = ''
        def findUser
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
            
            new UtilitiesService().existSchool(jsonUser?.school_id)
            
            newUser.school_id = jsonUser?.school_id
            
            findUser = Student.findByPersonalId(jsonUser?.personalId)

            new UtilitiesService().existUserTrue(findUser,jsonUser?.personalId)

            if(!newUser.validate()) {
                newUser.errors.allErrors.each {
                    responseMessage += MessageFormat.format(it.defaultMessage, it.arguments) + " "
                }
                throw new BadRequestException(responseMessage)
            }
            newUser.save(flush:true)
            println "Usuario "+newUser.personalId
            jsonResult = new UtilitiesService().getStudentByAdmin(newUser)
        
        }else if(jsonUser?.type.equals("teacher")){
            println "Es un teacher"
            println "Es un teacher"
            Teacher newTeacher = null
            try{
                newTeacher = new Teacher()
            }catch(Exception e){
                println  e
            }
            println "Es un teacher"
            newTeacher.personalId = jsonUser?.personalId
            newTeacher.school_id = jsonUser?.school_id
            newTeacher.email = jsonUser?.email
            newTeacher.name = jsonUser?.name
            newTeacher.fName = jsonUser?.fName
            newTeacher.lName = jsonUser?.lName
            newTeacher.dateOfBirth = jsonResult?.dateOfBirth
            newTeacher.sex = jsonUser?.sex
            newTeacher.profilePicture = jsonUser?.profilePicture
            newTeacher.status  = jsonUser?.status
            newTeacher.type = jsonUser?.type
            newTeacher.age = jsonUser?.age
            println "El nuevo teacher "+newTeacher

            new UtilitiesService().existSchool(jsonUser?.school_id)

            /*falta validar los grupos para esa escuela*/
            
            for(element in jsonUser?.groups){
                def groupClass = new Group(
                    teacher_id: jsonUser?.personalId,
                    group_name : element.group_name,
                    school_id : jsonUser?.school_id
                )

                if(!groupClass.validate()) {
                    groupClass.errors.allErrors.each {
                        responseMessage += MessageFormat.format(it.defaultMessage, it.arguments) + " "
                    }
                    throw new BadRequestException(responseMessage)
                }

                newTeacher.groups.add(
                    teacher_id: groupClass.teacher_id,
                    group_name : groupClass.group_name,
                    school_id : groupClass.school_id
                )
            }

            findUser = Teacher.findByPersonalId(jsonUser?.personalId)

            new UtilitiesService().existUserTrue(findUser,jsonUser?.personalId)

            if(!newTeacher.validate()) {
                newTeacher.errors.allErrors.each {
                    responseMessage += MessageFormat.format(it.defaultMessage, it.arguments) + " "
                }
                throw new BadRequestException(responseMessage)
            }

            newTeacher.save(flush:true)
            println "Teacher "+newTeacher.personalId
            jsonResult = new UtilitiesService().getTeacherByAdmin(newTeacher)

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

    public def searchTeacher(def dominio,def params){
        Map jsonResult  = [:]
        def queryMap    = [:]
        
        def email
        def userPassword
        def tokenAdmin
        
        def teacher
        
        def SEARCH_PARAMS_MAP =[
            email:"email",
            password: "password",
            admin:"admin"
        ]
        
        params.each { key, value ->
            def newKey = SEARCH_PARAMS_MAP[key]
            if (newKey){
                queryMap[newKey]=value
                if (newKey.equals("email")){
                    email = value
                }
                if (newKey.equals("password")){
                    userPassword = value
                }
                if (newKey.equals("admin")){
                    tokenAdmin = value
                }

            }
        }
        
        if (!queryMap){
            throw new BadRequestException("Bad Request call, can't find the search params.")
        }
        
        tokenAdminValid(tokenAdmin)
        
        def teacherCriteria = Teacher.createCriteria()
        
        def result = teacherCriteria.list(){
            eq("email",email)
            eq("password",userPassword)
        }
        
        if(result.size()<1){
            throw new NotFoundException("Can't find that user.")
        }
        
        def personalId
        def status
        def userType
        
        result.each{
            personalId  = it.personalId
            status      = it.status
            userType    = it.type
        }
        
        /*Verificar si esta bloqueado o en un estado que no permita iniciar sesion*/
        
        if (status.equals("history")){
            throw new NotFoundException("Very old teacher, sorry.")
        }else if(status.equals("blocked")){
            throw new NotFoundException("Blocked teacher.")
        }
        
        teacher = Teacher.findByPersonalId(personalId)
        teacher.dateLastAccess = new Date()
        teacher.save(flush:true)
        
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
