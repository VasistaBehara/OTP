package com.example.otp.otp.controller;

import java.util.HashMap;
import java.util.Map;

import com.example.otp.otp.model.OTPSystem;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OTPSystemRestController {
    private Map<String, OTPSystem> otp_data = new HashMap<>();
    public static final String ACCOUNT_SID = "ACdc894d622d33ac8b310c797fd40a6161";
    public static final String AUTH_TOKEN = "ab90a30f4b128684c55f562f5ab1bf9b";

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    @RequestMapping(value = "/mobileNumbers/{mobileNumber}/otp", method = RequestMethod.POST)
    public ResponseEntity<Object> sendOTP(@PathVariable("mobileNumber") String mobileNumber) {

        OTPSystem otpSystem = new OTPSystem();
        otpSystem.setMobileNumber(mobileNumber);
        otpSystem.setOtp(String.valueOf(((int) (Math.random() * (10000 - 1000))) + 1000));
        otpSystem.setExpiryTime(System.currentTimeMillis() / 20000);
        otp_data.put(mobileNumber, otpSystem);
        Message.creator(new PhoneNumber("+18073565984"), new PhoneNumber("+12256277508"),
                "Your Otp is " + otpSystem.getOtp()).create();
        return new ResponseEntity<>("OTP is sent successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/mobileNumbers/{mobileNumber}/otp", method = RequestMethod.PUT)
    public ResponseEntity<Object> verifyOTP(@PathVariable("otp") String mobileNumber,
            @RequestBody OTPSystem requestBodyOtpSystem) {

        if (requestBodyOtpSystem.getOtp() == null || requestBodyOtpSystem.getOtp().trim().length() <= 0) {
            return new ResponseEntity<>("Please Enter OTP", HttpStatus.BAD_REQUEST);
        }

        if (otp_data.containsKey(mobileNumber)) {
            OTPSystem otpSystem = otp_data.get(mobileNumber);
            if (otpSystem != null) {
                if (otpSystem.getExpiryTime() >= System.currentTimeMillis()) {
                    if (requestBodyOtpSystem.getOtp().equals(otpSystem.getOtp())) {
                        return new ResponseEntity<>("Success", HttpStatus.OK);
                    }
                    return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>("otp is expired", HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>("something went wrong", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("The mobile number is not found", HttpStatus.NOT_FOUND);
    }
}
