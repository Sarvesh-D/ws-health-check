package com.barclays.solveit.ws.health.common;

import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Logging Aspect for logging
 */
@Component
@Aspect
final class LoggingAspect {

	private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class); 

	@Pointcut("execution(* com.barclays.solveit.ws.health..*(..)) && !@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	private void allMethodsExceptRequests() {}
	
	@Pointcut("execution(* com.barclays.solveit.ws.health.web..*(..)) && @annotation(requestMapping)")
	private void allRequests(RequestMapping requestMapping) {}
	
	@Pointcut("execution(* com.barclays.solveit.ws.health..*(..))")
	private void allMethods() {}

	@Around("allMethodsExceptRequests()")
	private Object logMethodsExceptRequests(ProceedingJoinPoint pjp) throws Throwable {
		putUuid();
		Object retval = null;
		logger.trace("Entering method : {}", pjp.getSignature());
		retval = pjp.proceed();
		return retval;
	}
	
	@Around("allRequests(requestMapping)")
	private Object logRequests(ProceedingJoinPoint pjp, RequestMapping requestMapping) throws Throwable {
		putUuid();
		Object retval = null;
		logger.trace("Entering method : {}", pjp.getSignature());
		logger.debug("Request Info {}",getRequestInfo(requestMapping));
		retval = pjp.proceed();
		return retval;
	}
	
	@AfterThrowing(pointcut="allMethods()", throwing="error")
	private void logException(JoinPoint jp , Throwable error) {
		logger.error("Exception occured in method : {}", jp.getSignature());
		logger.error("Exception : {}", error.getMessage());
	}
	
	@AfterReturning(pointcut="allMethods()", returning="retval")
	private void logSuccessfulExit(JoinPoint jp, Object retval) {
		logger.trace("Successfully Exiting method : {}", jp.getSignature());
	}
	
	private StringBuilder getRequestInfo(RequestMapping requestMapping) {
		StringBuilder requestInfo = new StringBuilder();
		requestInfo.append("\nRequest header(s) = ");
		requestInfo.append(Arrays.toString(requestMapping.headers()));
		requestInfo.append("\nRequest method(s) = ");
		requestInfo.append(Arrays.toString(requestMapping.method()));
		requestInfo.append("\nRequest value(s) = ");
		requestInfo.append(Arrays.toString(requestMapping.value()));
		requestInfo.append("\nRequest param(s) = ");
		requestInfo.append(Arrays.toString(requestMapping.params()));
		requestInfo.append("\nRequest path(s) = ");
		requestInfo.append(Arrays.toString(requestMapping.path()));
		return requestInfo;
	}
	
	private void putUuid() {
		if(StringUtils.isBlank(MDC.get("uuid"))) {
			String uuid = UUID.randomUUID().toString().replaceAll("-", "")
					.toUpperCase();
			MDC.put("uuid", uuid);
		}
	}
	
}
