package com.portalempleos.security;

import com.portalempleos.model.User;
import com.portalempleos.model.Company;
import com.portalempleos.repository.UserRepository;
import com.portalempleos.repository.CompanyRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public CustomUserDetailsService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrNit) throws UsernameNotFoundException {

        // === 1) Buscar Usuario por email ===
        User u = userRepository.findByEmail(usernameOrNit).orElse(null);
        if (u != null) {
            String role = (u.getRole() == null || u.getRole().isBlank()) ? "USER" : u.getRole().toUpperCase();
            String email = (u.getEmailEntity() != null) ? u.getEmailEntity().getEmail() : usernameOrNit;

            return org.springframework.security.core.userdetails.User
                    .withUsername(email)
                    .password(u.getPassword() == null ? "{noop}" : u.getPassword())
                    .roles(role)
                    .build();
        }

        // === 2) Buscar Empresa por NIT o Email ===
        Company c = companyRepository.findByNit(usernameOrNit)
                .or(() -> companyRepository.findByEmail(usernameOrNit))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario/Empresa no encontrado: " + usernameOrNit));

        String companyEmail = (c.getEmailEntity() != null)
                ? c.getEmailEntity().getEmail()
                : usernameOrNit;

        return org.springframework.security.core.userdetails.User
                .withUsername(companyEmail)
                .password(c.getPassword() == null ? "{noop}" : c.getPassword())
                .roles("EMPLOYER")
                .build();
    }
}
