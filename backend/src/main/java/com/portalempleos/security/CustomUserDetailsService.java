package com.portalempleos.security;

import com.portalempleos.model.User;
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
        // Buscar usuario por email
        User u = userRepository.findByEmail(usernameOrNit).orElse(null);

        if (u == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + usernameOrNit);
        }

        String role = (u.getRole() == null || u.getRole().isBlank()) ? "USER" : u.getRole();

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword() == null ? "{noop}" : u.getPassword())
                .roles(role)
                .build();
    }
}
