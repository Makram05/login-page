package com.login.login_page.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OTPService {
    private static final long EXPIRATION_TIME = 5 * 60 * 1000;

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String twilioPhoneNumber;

    public OTPService() {
        // Initialize Twilio with credentials
        Twilio.init("ACecf407e59315b04293d5a8409894b394", "c861721683aa50997f2d695d3ae00230");
    }

    // Generates a 6-digit OTP
    public String generateOTP() {
        return RandomStringUtils.randomNumeric(6);  // Generate a 6-digit numeric OTP
    }

    // Sends OTP to the user's phone number using Twilio API
    public void sendOTP(String phoneNumber, String otp) {
        Message message = Message.creator(
                new PhoneNumber(phoneNumber),  // Destination phone number
                new PhoneNumber(twilioPhoneNumber),  // Your Twilio phone number
                "Your OTP is: " + otp  // OTP message content
        ).create();

        System.out.println("Sent OTP: " + message.getSid());  // Optional: log the SID for troubleshooting
    }

    public boolean isOTPExpired(Long otpCreationTime) {
        if (otpCreationTime == null) {
            return true;  // if no OTP creation time exists, it's expired
        }
        long currentTime = System.currentTimeMillis();
        return (currentTime - otpCreationTime) > EXPIRATION_TIME;
    }
}
