package com.samirmamede.wakari.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class JwtService {
    private val logger = LoggerFactory.getLogger(JwtService::class.java)
    
    @Value("\${wakari.security.jwt.secret}")
    private lateinit var secretKey: String
    
    @Value("\${wakari.security.jwt.expiration}")
    private var jwtExpiration: Long = 0
    
    private lateinit var signingKey: Key
    
    @PostConstruct
    fun init() {
        // Garantir que a chave secreta tenha pelo menos 256 bits (32 caracteres)
        if (secretKey.length < 32) {
            val missingLength = 32 - secretKey.length
            secretKey += "0".repeat(missingLength)
            logger.warn("JWT secret key é muito curta, adicionando padding. Recomendado usar uma chave de pelo menos 32 caracteres.")
        }
        
        // Converter a chave secreta para bytes usando Base64 e criar a chave de assinatura
        val keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(secretKey.toByteArray()))
        signingKey = Keys.hmacShaKeyFor(keyBytes)
    }
    
    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject) ?: ""
    }
    
    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T? {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }
    
    fun generateToken(userDetails: UserDetails): String {
        return generateToken(HashMap(), userDetails)
    }
    
    fun generateToken(extraClaims: Map<String, Any>, userDetails: UserDetails): String {
        return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact()
    }
    
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username.isNotBlank() && username == userDetails.username) && !isTokenExpired(token)
    }
    
    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }
    
    private fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration) ?: Date(0)
    }
    
    private fun extractAllClaims(token: String): Claims {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            logger.error("Erro ao extrair claims do token JWT", e)
            throw e
        }
    }
} 