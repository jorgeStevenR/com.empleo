package com.portalempleos.security;

import com.portalempleos.model.Company;
import com.portalempleos.model.User;
import com.portalempleos.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailEntity_Email(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + email));

        return new UserDetailsImpl(user);
    }

    // ✅ Método adicional para crear UserDetails desde una empresa
    public UserDetails createCompanyUser(Company company) {
        if (company == null)
            return null;

        return org.springframework.security.core.userdetails.User.builder()
                .username(company.getEmailEntity().getEmail())
                .password("") // No requiere password aquí
                .authorities("ROLE_COMPANY")
                .build();
    }
}
