package Hotel_Manager_OOP_Class_.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Hotel_Manager_OOP_Class_.demo.entity.User;
import Hotel_Manager_OOP_Class_.demo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException{
            User user = userRepository.findByUsername(username)
                    .orElseThrow(()-> new UsernameNotFoundException("Lỗi tên người dùng không tồn tại"));
            UserDetails userDetails;
            userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPasswordHash())
                    .authorities(user.getRole().getName())
                    .build();

            return userDetails;
    }
    
}