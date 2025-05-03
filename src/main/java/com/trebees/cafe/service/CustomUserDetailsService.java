package com.trebees.cafe.service;

import com.trebees.cafe.model.CustomerAccount;
import com.trebees.cafe.repository.CustomerAccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerAccountRepository customerRepository;

    public CustomUserDetailsService(CustomerAccountRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CustomerAccount customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // All users get USER role
        
        if (customer.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN")); // Add ADMIN role if flagged
        }
        
        return new User(
                customer.getEmail(),
                customer.getPassword(),
                authorities
        );
    }
}
