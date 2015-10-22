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
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class StudentService {

    public def getStudent(def params,def dominio){
        Map jsonResult = [:]
        def userResult

        if(!params.user_id){
             throw new NotFoundException("Invalid URL, missing user_id")
        }

        userResult = Student.findByPersonalId(params.user_id)

        //existUser(userResult,params.user_id)
        new UtilitiesService().existUser(userResult,params.user_id)

        //jsonResult = getResultAdmin(userResult)

        println "Le user type "+userResult.type
        if(userResult.type.equals("student")){
            jsonResult = new UtilitiesService().getStudentByAdmin(userResult)
        }

        jsonResult
    }

    public def putStudent(def params,def dominio,def json){
        println "Entra"
        Map jsonResult = [:]
        def responseMessage = ''
        def usuario 

        if(!json){
            throw new ConflictException("Empty JSON!")
        }

        if(!params.user_id){
            throw new NotFoundException("Invalid URL, missing user_id")
        }

        if(!json.type){
            throw new ConflictException("Bad JSON, type is missing!!!")
        }

        if(json.type.equals("student")){
            usuario = Student.findByPersonalId(params.user_id)
            println "Usuario "+usuario
            //existUser(usuario,params.user_id)
            new UtilitiesService().existUser(usuario,params.user_id)

            jsonResult = modificaStudent(dominio,usuario,json)
        }
        jsonResult
    }

    public def deleteStudent(def params,def dominio){
        Map jsonResult = [:]
        def userResult

        if(!params.user_id){
             throw new NotFoundException("Invalid URL, missing user_id")
        }

        userResult = Student.findByPersonalId(params.user_id)

        //existUser(userResult,params.user_id)
        new UtilitiesService().existUser(userResult,params.user_id)

        //jsonResult = getResultAdmin(userResult)

        println "Le user type "+userResult.type
        if(userResult.type.equals("student")){
            //jsonResult = getStudentByAdmin(userResult)
            jsonResult.comment = "The user with the user_id "+params.user_id+" has been deleted successfully"
            userResult.delete(flush:true)
        }
        jsonResult
    }

    private def modificaStudent(def dominio, def usuario, def json){
        def responseMessage = ''
        Map jsonResult = [:]

        if(json?.type.equals("student")){
            if(json?.group){
                usuario.group = json.group
            }

            if(json?.school){
                usuario.school = json.school
            }

            if(json?.email){
                usuario.email = json.email
            }     
            
            if(json?.name){
                usuario.name = json.name
            }  

            if(json?.fName){
                usuario.fName = json.fName
            }  

            if(json?.lName){
                usuario.lName = json.lName
            }  

            if(json?.sex){
                usuario.sex = json.sex
            }  

            if(json?.profilePicture){
                usuario.profilePicture = json.profilePicture
            }

            if(json?.status){
                usuario.status = json.status
            }

            if(json?.group){
                usuario.group = json.group
            }
            
            new UtilitiesService().existSchool(json?.school_id)
            
            if(json?.school_id){
                usuario.school_id = json.school_id
            }

            if(json?.dateOfBirth){
                //usuario.dateOfBirth = json.dateOfBirth
                try{
                    json.dateOfBirth = ISODateTimeFormat.dateTimeParser().parseDateTime(json?.dateOfBirth).toDate()
                }catch(Exception e){
                    throw new BadRequestException("Wrong date format in date_of_birth. Must be ISO json format")
                }
            }  

            usuario.dateLastUpdate = new Date()

            if(!usuario.validate()){
                usuario.errors.allErrors.each {
                    responseMessage += MessageFormat.format(it.defaultMessage, it.arguments) + " "
                }
                throw new BadRequestException(responseMessage)
            }

            usuario.save(flush:true)
            jsonResult = new UtilitiesService().getStudentByAdmin(usuario)
        }
        jsonResult
    }   
}
