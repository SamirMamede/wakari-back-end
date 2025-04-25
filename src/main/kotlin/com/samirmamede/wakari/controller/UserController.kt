package com.samirmamede.wakari.controller

import com.samirmamede.wakari.dto.UserResponse
import com.samirmamede.wakari.model.UserRole
import com.samirmamede.wakari.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping
    fun findAll(pageable: Pageable): ResponseEntity<Page<UserResponse>> {
        val users = userService.findAll(pageable).map { UserResponse.fromEntity(it) }
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<UserResponse> {
        val user = userService.findById(id)
        return ResponseEntity.ok(UserResponse.fromEntity(user))
    }

    @GetMapping("/email/{email}")
    fun findByEmail(@PathVariable email: String): ResponseEntity<UserResponse> {
        val user = userService.findByEmail(email)
        return ResponseEntity.ok(UserResponse.fromEntity(user))
    }

    @PutMapping("/{id}/name")
    fun updateName(
        @PathVariable id: Long,
        @RequestBody name: String
    ): ResponseEntity<UserResponse> {
        val updatedUser = userService.updateName(id, name)
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser))
    }

    @PutMapping("/{id}/role")
    fun updateRole(
        @PathVariable id: Long,
        @RequestBody role: UserRole
    ): ResponseEntity<UserResponse> {
        val updatedUser = userService.updateRole(id, role)
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        userService.delete(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
} 