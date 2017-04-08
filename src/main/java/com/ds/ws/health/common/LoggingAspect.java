package com.ds.ws.health.common;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * Logging Aspect for logging
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
@Component
@Aspect
@Slf4j
final class LoggingAspect {

    @Autowired(required = false)
    private HttpServletRequest request;

    @Pointcut("execution(* com.ds.ws.health..*(..))")
    private void allMethods() {
    }

    @Pointcut("execution(* com.ds.ws.health..*(..)) && !@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    private void allMethodsExceptRequests() {
    }

    @Pointcut("execution(* com.ds.ws.health.web..*(..)) && @annotation(requestMapping)")
    private void allRequests(RequestMapping requestMapping) {
    }

    private String getRequestInfo(RequestMapping requestMapping) {
	StringJoiner requestInfo = new StringJoiner("\n");
	requestInfo.add("Request header(s) = " + Arrays.toString(requestMapping.headers()));
	requestInfo.add("Request method(s) = " + Arrays.toString(requestMapping.method()));
	requestInfo.add("Request value(s) = " + Arrays.toString(requestMapping.value()));
	requestInfo.add("Request param(s) = " + Arrays.toString(requestMapping.params()));
	requestInfo.add("Request path(s) = " + Arrays.toString(requestMapping.path()));
	if (null != request) {
	    requestInfo.add("User Principal = " + request.getUserPrincipal());
	    requestInfo.add("Request IP = " + request.getRemoteAddr());
	    requestInfo.add("Server IP = " + request.getServerName());
	}
	return requestInfo.toString();
    }

    @AfterThrowing(pointcut = "allMethods()", throwing = "error")
    private void logException(JoinPoint jp, Throwable error) {
	log.error("Exception occured in method : {}", jp.getSignature());
	log.error("Exception : {}", error.getMessage());
    }

    @Around("allMethodsExceptRequests()")
    private Object logMethodsExceptRequests(ProceedingJoinPoint pjp) throws Throwable {
	putUuid();
	Object retval = null;
	log.trace("Entering method : {}", pjp.getSignature());
	retval = pjp.proceed();
	return retval;
    }

    @Around("allRequests(requestMapping)")
    private Object logRequests(ProceedingJoinPoint pjp, RequestMapping requestMapping) throws Throwable {
	putUuid();
	Object retval = null;
	log.trace("Entering method : {}", pjp.getSignature());
	log.debug("Request Info {}", getRequestInfo(requestMapping));
	retval = pjp.proceed();
	return retval;
    }

    @AfterReturning(pointcut = "allMethods()", returning = "retval")
    private void logSuccessfulExit(JoinPoint jp, Object retval) {
	log.trace("Successfully Exiting method : {}", jp.getSignature());
    }

    private void putUuid() {
	if (StringUtils.isBlank(MDC.get("uuid"))) {
	    String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
	    MDC.put("uuid", uuid);
	}
    }

}
