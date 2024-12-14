package com.bybud.deliveryservice.security;

import com.bybud.common.model.User;
import com.bybud.common.repository.UserRepository;
import com.bybud.common.security.CustomUserDetailsService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("deliveryUserDetailsService")
@Lazy
public class DeliveryUserDetailsService extends CustomUserDetailsService {

    public DeliveryUserDetailsService(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the common UserRepository using the inherited getter
        UserRepository userRepository = getUserRepository();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Construct UserDetails with minimal context specific to the delivery-service
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Assuming password encryption is already handled
                .roles("DELIVERY") // Assign a generic or delivery-specific role
                .disabled(!user.isActive())
                .build();
    }
}

