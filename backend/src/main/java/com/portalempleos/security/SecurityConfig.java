package com.portalempleos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Habilita CORS (usas CorsConfig aparte) y desactiva CSRF para API stateless
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                /* ==================== Rutas públicas ==================== */
                // Auth / registro
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/companies").permitAll()

                // Archivos estáticos (CVs y logos servidos desde /files/**)
                .requestMatchers("/files/**").permitAll()

                // Jobs: consulta pública
                .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()

                /* ==================== Rutas protegidas por rol ==================== */
                // Jobs: crear/editar/eliminar solo COMPANY
                .requestMatchers(HttpMethod.POST,   "/api/jobs/**").hasRole("COMPANY")
                .requestMatchers(HttpMethod.PUT,    "/api/jobs/**").hasRole("COMPANY")
                .requestMatchers(HttpMethod.DELETE, "/api/jobs/**").hasRole("COMPANY")

                // Ver postulaciones de una vacante: COMPANY o ADMIN
                .requestMatchers(HttpMethod.GET, "/api/jobs/*/applications").hasAnyRole("COMPANY","ADMIN")

                // Applications: CRUD del postulante (USER)
                .requestMatchers(HttpMethod.POST,   "/api/applications/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT,    "/api/applications/**").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/applications/**").hasRole("USER")
                .requestMatchers(HttpMethod.GET,    "/api/applications/user/**").hasRole("USER")

                // Cambiar solo estado de una postulación (PATCH):
                // - COMPANY/ADMIN: IN_PROGRESS, ACCEPTED, REJECTED
                // - USER: CANCELED (la validación fina la haces en el servicio)
                .requestMatchers(HttpMethod.PATCH, "/api/applications/*/status")
                    .hasAnyRole("COMPANY","ADMIN","USER")

                // Uploads: CV del usuario (PDF) y logo de empresa (img)
                .requestMatchers(HttpMethod.PUT, "/api/users/*/cv").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/companies/*/logo").hasRole("COMPANY")

                /* ==================== Resto de rutas ==================== */
                // Cualquier endpoint restante bajo users/companies requiere token
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/companies/**").authenticated()

                // Todo lo no mapeado explícitamente requiere autenticación
                .anyRequest().authenticated()
            );

        // Filtro JWT antes del UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
