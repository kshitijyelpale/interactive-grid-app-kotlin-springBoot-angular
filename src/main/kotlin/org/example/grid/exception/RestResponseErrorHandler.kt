package com.example.todoapp.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseErrorHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [NoSuchElementFoundException::class])
    fun noDataFoundExceptionHandler(exception: NoSuchElementFoundException, request: WebRequest): ResponseEntity<Any>? {
        return handleExceptionInternal(
            exception,
            "No such element found",
            HttpHeaders(),
            HttpStatus.NOT_FOUND,
            request
        )
    }

    @ExceptionHandler(value = [ConstraintViolationException::class])
    fun validationExceptionHandler(exception: ConstraintViolationException, request: WebRequest): ResponseEntity<Any>? {
        return handleExceptionInternal(
            exception,
            exception.message,
            HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        )
    }

    @ExceptionHandler(value = [ServiceException::class])
    fun serviceExceptionHandler(exception: ServiceException, request: WebRequest): ResponseEntity<Any>? {
        return handleExceptionInternal(
            exception,
            "Service exception",
            HttpHeaders(),
            HttpStatus.INTERNAL_SERVER_ERROR,
            request
        )
    }
}
