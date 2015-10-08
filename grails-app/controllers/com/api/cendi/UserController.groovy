package com.api.cendi

import static org.springframework.http.HttpStatus.*
import static org.springframework.http.HttpMethod.*
import grails.plugin.gson.converters.GSON
import org.springframework.dao.OptimisticLockingFailureException
import com.api.cendi.UserService
import users.exceptions.NotFoundException
import users.exceptions.ConflictException
import users.exceptions.BadRequestException
import javax.servlet.http.HttpServletResponse
import grails.converters.*

class UserController {

	private UserService userService = new UserService()

    def setHeaders(){
        response.setContentType "application/json; charset=utf-8"
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
        response.setHeader("Access-Control-Max-Age", "86400");
        response.setHeader("Access-Control-Allow-Headers", "application/json;charset=UTF-8");
    }

    def renderException(def e){
        def statusCode
        def error
        try{
            statusCode  = e.status
            error       = e.error
        }catch(Exception ex){
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            error = "internal_server_error"
        }
        response.setStatus(statusCode)
        def mapExcepction = [
            message: e.getMessage(),
            status: statusCode,
            error: error
        ]
        render mapExcepction as GSON
    }

    def notAllowed(){
        def method = request.method
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
        setHeaders()
        def mapResult = [
            message: "Method $method not allowed",
            status: HttpServletResponse.SC_METHOD_NOT_ALLOWED,
            error:"not_allowed"
        ]
        render mapResult as GSON
    }

    def getUser() {
        setHeaders()
        def dominio = request.getServerName()+":"+request.getServerPort()
        def result = [:]
        int retryCounter = 0
        int maxretry=15
        boolean needsProcessing = true
        while(needsProcessing && retryCounter < maxretry) {
            try{
                result = userService.getUser(params,dominio)
                response.setStatus( HttpServletResponse.SC_OK)
                render result as GSON
                needsProcessing=false;
            }catch(NotFoundException e){
                needsProcessing=false;
                renderException(e)
            }catch(ConflictException e){
                needsProcessing=false;
                renderException(e)
            }catch (BadRequestException e) {
                needsProcessing=false;
                renderException(e)
            }catch (OptimisticLockingFailureException olfex) {
                if((retryCounter += 1) >= maxretry) renderException(olfex);
            }catch(Exception e){
              println "Users Exception error----> "+e
              needsProcessing=false;
              renderException(e)
            }
        }
    }

    def deleteUser() {
        setHeaders()
        def dominio = request.getServerName()+":"+request.getServerPort()
        def result = [:]
        int retryCounter = 0
        int maxretry=15
        boolean needsProcessing = true
        while(needsProcessing && retryCounter < maxretry) {
            try{
                result = userService.deleteUser(params,dominio)
                response.setStatus( HttpServletResponse.SC_OK)
                render result as GSON
                needsProcessing=false;
            }catch(NotFoundException e){
                needsProcessing=false;
                renderException(e)
            }catch(ConflictException e){
                needsProcessing=false;
                renderException(e)
            }catch (BadRequestException e) {
                needsProcessing=false;
                renderException(e)
            }catch (OptimisticLockingFailureException olfex) {
                if((retryCounter += 1) >= maxretry) renderException(olfex);
            }catch(Exception e){
              println "Users Exception error----> "+e
              needsProcessing=false;
              renderException(e)
            }
        }
    }

    def postUser(){
    	setHeaders()
    	def dominio = request.getServerName()+":"+request.getServerPort()
    	def result
        int retryCounter = 0
        int maxretry=15
        boolean needsProcessing = true
        while(needsProcessing && retryCounter < maxretry) {
            try{
                result = userService.postUser(request.JSON,dominio)
                response.setStatus( HttpServletResponse.SC_CREATED)
                needsProcessing=false;
                render result as GSON
            }catch(NotFoundException e){
                needsProcessing=false;
                renderException(e)
            }catch(ConflictException e){
                needsProcessing=false;
                renderException(e)
            }catch (BadRequestException e) {
                needsProcessing=false;
                renderException(e)
            }catch (OptimisticLockingFailureException olfex) {
                if((retryCounter += 1) >= maxretry) renderException(olfex);
            }catch(Exception e){
              println "Users Exception error----> "+e
              needsProcessing=false;
              renderException(e)
            }
        }
    }

    def putUser(){
        setHeaders()
        def dominio = request.getServerName()+":"+request.getServerPort()
        def result
        int retryCounter = 0
        int maxretry=15
        boolean needsProcessing = true
        while(needsProcessing && retryCounter < maxretry) {
            try{
                println "PUT"
                result = userService.putUser(params,dominio,request.JSON)
                response.setStatus( HttpServletResponse.SC_CREATED)
                render result as GSON
                needsProcessing=false;
                
            }catch(NotFoundException e){
                needsProcessing=false;
                renderException(e)
            }catch(ConflictException e){
                needsProcessing=false;
                renderException(e)
            }catch (BadRequestException e) {
                needsProcessing=false;
                renderException(e)
            }catch (OptimisticLockingFailureException olfex) {
                if((retryCounter += 1) >= maxretry) renderException(olfex);
            }catch(Exception e){
              println "Users Exception error----> "+e
              needsProcessing=false;
              renderException(e)
            }
        }
    }
}
