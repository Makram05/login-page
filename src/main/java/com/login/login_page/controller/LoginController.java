package com.login.login_page.controller;

import com.login.login_page.entity.Users;
import com.login.login_page.repository.UsersRepository;
import com.login.login_page.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private OTPService otpService;

    // Store OTPs temporarily (in memory or a database for a real-world scenario)
    private Map<String, String> otpStorage = new HashMap<>();
    private Map<String, Long> otpTimestamps = new HashMap<>();

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // Show login page
    }

    @PostMapping("/login")
    public String login(String username, String password, Model model) {
        Users user = usersRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            // If credentials are correct, generate OTP and send it via SMS
            String otp = otpService.generateOTP();
            otpStorage.put(username, otp);  // Store OTP for the user
            otpTimestamps.put(username, System.currentTimeMillis());  // Store OTP timestamp

            // Send OTP to the user's phone number
            otpService.sendOTP(user.getPhoneNumber(), otp);  // Use user's phone number for SMS

            model.addAttribute("username", username);
            return "otp";  // Redirect to OTP page to input OTP
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";  // Return to login page with error message
        }
    }

    @PostMapping("/verifyOtp")
    public String verifyOtp(String username, String otp, Model model) {
        String storedOtp = otpStorage.get(username);
        Long timestamp = otpTimestamps.get(username);

        if (storedOtp != null && storedOtp.equals(otp) && !otpService.isOTPExpired(timestamp)) {
            // If OTP is valid and not expired, allow user to access the welcome page
            otpStorage.remove(username);  // Remove OTP from storage
            otpTimestamps.remove(username);  // Remove OTP timestamp

            model.addAttribute("username", username);  // Add username to model for welcome page
            return "welcome";  // Show welcome page
        } else {
            model.addAttribute("error", "Invalid OTP or OTP has expired");
            return "otp";  // Return to OTP page with error message
        }
    }

}

