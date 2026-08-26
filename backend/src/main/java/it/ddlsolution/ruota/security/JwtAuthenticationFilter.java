package it.ddlsolution.ruota.security;

import io.jsonwebtoken.ExpiredJwtException;
import it.ddlsolution.ruota.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String path = request.getRequestURI();
            if (path != null && path.contains("/auth/refresh-token")) {
                filterChain.doFilter(request, response);
                return;
            }


            String jwt = authHeader.substring(7);
            if (jwt.equals("null")){
                jwt=null;
            }
            Long id=null;
            String role=null;
            if (jwt != null) {
                id = Long.parseLong(jwtService.extractId(jwt));
                role = jwtService.extractRole(jwt);
            }


            if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.isTokenValid(jwt, id)) {
                    // Crea le authorities basate sul ruolo
                    var authorities = new ArrayList<org.springframework.security.core.GrantedAuthority>();
                    if (role != null) {
                        authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role));
                    }
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            id,
                            null,
                            authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token JWT non valido");
                    return;
                }
            }
        } catch (ExpiredJwtException eje) {
            log.error("Token JWT scaduto", eje);
            request.setAttribute("expiredJwt", true);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT scaduto");
            return;
        } catch (Exception e) {
            log.error("Errore nella validazione del JWT", e);
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}
