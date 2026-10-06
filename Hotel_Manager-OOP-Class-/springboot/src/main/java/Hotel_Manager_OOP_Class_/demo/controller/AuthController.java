package Hotel_Manager_OOP_Class_.demo.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import Hotel_Manager_OOP_Class_.demo.dto.LoginRequest;
import Hotel_Manager_OOP_Class_.demo.dto.LoginResponse;
import Hotel_Manager_OOP_Class_.demo.dto.RegisterRequest;
import Hotel_Manager_OOP_Class_.demo.entity.Role;
import Hotel_Manager_OOP_Class_.demo.entity.User;
import Hotel_Manager_OOP_Class_.demo.repository.RoleRepository;
import Hotel_Manager_OOP_Class_.demo.repository.UserRepository;
import Hotel_Manager_OOP_Class_.demo.service.JwtService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @PostMapping("/api/v1/auth/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword())            
        );
        String token = jwtService.generateToken(authentication);
        return new LoginResponse(token);
    }
    @PostMapping("/api/v1/auth/register")
    public String register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username đã tồn tại";
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(role)
                .build();

        userRepository.save(user);

        return "Đăng ký thành công";
    }
    


}