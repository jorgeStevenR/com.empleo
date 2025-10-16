package com.portalempleos.security;

import com.portalempleos.model.User;
import com.portalempleos.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Primary
@Service("portalUserDetailsService")
public class PortalUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public PortalUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNit) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(emailOrNit)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + emailOrNit));

        String role = (user.getRole() == null || user.getRole().isBlank()) ? "USER" : user.getRole();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword() == null ? "{noop}" : user.getPassword())
                .roles(role)
                .build();
    }
}
