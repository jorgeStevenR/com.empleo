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
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        /* ==========================
                           🔓 RUTAS PÚBLICAS
                           ========================== */

                        // Ofertas y empresas visibles sin login
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/companies/**").permitAll()

                        // Login
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/**").permitAll()

                        // Registro de usuario y empresa
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/companies").permitAll()

                        // Archivos estáticos o rutas públicas
                        .requestMatchers("/files/**").permitAll()


                        /* ==========================
                           🔐 SUBIR ARCHIVOS (Privado)
                           ========================== */
                        .requestMatchers(HttpMethod.POST, "/api/files/upload/cv/**")
                            .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/files/upload/logo/**")
                            .hasAuthority("ROLE_COMPANY")


                        /* ==========================
                           📝 POSTULACIONES
                           ========================== */

                        // Crear postulaciones → solo usuarios
                        .requestMatchers(HttpMethod.POST, "/api/applications").hasAuthority("ROLE_USER")

                        // Ver postulaciones del usuario
                        .requestMatchers(HttpMethod.GET, "/api/applications/user/**").hasAuthority("ROLE_USER")

                        // Empresas ven postulantes por oferta
                        .requestMatchers(HttpMethod.GET, "/api/applications/job/**").hasAuthority("ROLE_COMPANY")


                        /* ==========================
                           🏢 CRUD OFERTAS (Solo Empresa)
                           ========================== */
                        .requestMatchers(HttpMethod.POST, "/api/jobs/**").hasAuthority("ROLE_COMPANY")
                        .requestMatchers(HttpMethod.PUT, "/api/jobs/**").hasAuthority("ROLE_COMPANY")
                        .requestMatchers(HttpMethod.DELETE, "/api/jobs/**").hasAuthority("ROLE_COMPANY")


                        /* ==========================
                           👤 RUTAS PRIVADAS GENERALES
                           ========================== */
                        .requestMatchers("/api/users/**").authenticated()

                        // ⚠️ OJO: NO bloqueamos /api/companies porque el registro es público.
                        // Solo protegemos PUT/DELETE.
                        .requestMatchers(HttpMethod.PUT, "/api/companies/**").hasAuthority("ROLE_COMPANY")
                        .requestMatchers(HttpMethod.DELETE, "/api/companies/**").hasAuthority("ROLE_ADMIN")


                        /* ==========================
                           🟢 TODO LO DEMÁS PERMITIDO
                           ========================== */
                        .anyRequest().permitAll()
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
