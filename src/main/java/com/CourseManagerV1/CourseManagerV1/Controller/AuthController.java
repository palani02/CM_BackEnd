package com.CourseManagerV1.CourseManagerV1.Controller;

import com.CourseManagerV1.CourseManagerV1.Service.AuthService;
import com.CourseManagerV1.CourseManagerV1.dto.LoginRequest;
import com.CourseManagerV1.CourseManagerV1.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.CourseManagerV1.CourseManagerV1.dto.ForgotPasswordRequest;
import java.util.Map;

// Login and signin contrller/ endpoints
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, String> result = authService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword(),
                loginRequest.getRole()
        );

        HttpStatus status = "success".equals(result.get("status")) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(result, status);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest registerRequest) {
        Map<String, String> result = authService.registerStudent(
                registerRequest.getEmail(),
                registerRequest.getPassword()
        );

        HttpStatus status = "success".equals(result.get("status")) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(result, status);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Map<String, String> result = authService.forgotPassword(
                request.getEmail(),
                request.getCurrentPassword(),
                request.getNewPassword(),
                request.getRole()
        );

        HttpStatus status = "success".equals(result.get("status")) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(result, status);
    }

}
