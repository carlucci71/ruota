package it.ddlsolution.ruota.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Hidden;

import it.ddlsolution.ruota.dto.response.AuthResponseDTO;
import it.ddlsolution.ruota.dto.response.MagicLinkResponseDTO;
import it.ddlsolution.ruota.service.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtService jwtService;



    @Hidden
    @GetMapping("/token")
    public ResponseEntity<String> token(@RequestParam String role, @RequestParam String id) {
        String jwtToken = jwtService.generateToken(id, role);
        return ResponseEntity.ok(jwtToken);
    }



}
