package com.portalempleos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Permitir preflight CORS
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Solo si NO hay autenticación en el contexto
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                String jwt = parseJwt(request);

                // Si existe token y es válido → autenticar
                if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                    String email = jwtUtils.getEmailFromJwtToken(jwt);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error validando token: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * EXTRA IMPORTANTE:
     * Evita aceptar tokens vacíos tipo "Bearer ", "Bearer null", "Bearer undefined"
     */
    private String parseJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (!StringUtils.hasText(header)) {
            return null;
        }

        if (!header.startsWith("Bearer ")) {
            return null;
        }

        // Cortar "Bearer " y limpiar espacios
        String token = header.substring(7).trim();

        // Si el token está vacío → tratar como si NO hubiera token
        if (!StringUtils.hasText(token)) {
            return null;
        }

        return token;
    }
}
