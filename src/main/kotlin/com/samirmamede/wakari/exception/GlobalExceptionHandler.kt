package com.samirmamede.wakari.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Erro interno do servidor",
            message = ex.message ?: "Erro desconhecido",
            path = null
        )
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
    
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.allErrors.associate { error ->
            (error as FieldError).field to (error.defaultMessage ?: "Erro de validação")
        }
        
        val response = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Erro de validação",
            message = "Existem erros de validação na requisição",
            path = null,
            validationErrors = errors
        )
        
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }
    
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(ex: UserAlreadyExistsException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.CONFLICT.value(),
            error = "Conflito",
            message = ex.message ?: "Usuário já existe",
            path = null
        )
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }
    
    @ExceptionHandler(ResourceAlreadyExistsException::class)
    fun handleResourceAlreadyExistsException(ex: ResourceAlreadyExistsException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.CONFLICT.value(),
            error = "Conflito",
            message = ex.message ?: "Recurso já existe",
            path = null
        )
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }
    
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.UNAUTHORIZED.value(),
            error = "Não autorizado",
            message = "Usuário ou senha incorretos",
            path = null
        )
        return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
    }
    
    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(ex: UsernameNotFoundException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = "Não encontrado",
            message = ex.message ?: "Usuário não encontrado",
            path = null
        )
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }
    
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = "Não encontrado",
            message = ex.message ?: "Recurso não encontrado",
            path = null
        )
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }
    
    @ExceptionHandler(InvalidOperationException::class)
    fun handleInvalidOperationException(ex: InvalidOperationException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Operação inválida",
            message = ex.message ?: "A operação solicitada não pode ser executada",
            path = null
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }
}

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String?,
    val validationErrors: Map<String, String>? = null
) 