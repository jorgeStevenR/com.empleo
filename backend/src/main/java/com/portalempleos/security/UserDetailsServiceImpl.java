package com.portalempleos.security;

import com.portalempleos.model.User;
import com.portalempleos.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
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
}
