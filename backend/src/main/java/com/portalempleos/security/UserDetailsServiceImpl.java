package com.portalempleos.security;

import com.portalempleos.model.Company;
import com.portalempleos.model.User;
import com.portalempleos.repository.CompanyRepository;
import com.portalempleos.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public UserDetailsServiceImpl(UserRepository userRepository,
                                  CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        email = email.toLowerCase();

        // 1️⃣ Buscar usuario normal
        User user = userRepository.findByEmailEntity_Email(email).orElse(null);

        if (user != null) {
            return new UserDetailsImpl(user);
        }

        // 2️⃣ Buscar empresa
        Company company = companyRepository.findByEmailEntity_Email(email).orElse(null);

        if (company != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(company.getEmailEntity().getEmail())
                    .password(company.getPassword())
                    .authorities("ROLE_COMPANY")
                    .build();
        }

        throw new UsernameNotFoundException("No existe usuario o empresa con el correo: " + email);
    }
}
