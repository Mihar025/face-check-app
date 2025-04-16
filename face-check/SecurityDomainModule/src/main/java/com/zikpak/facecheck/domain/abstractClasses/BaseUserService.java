package com.zikpak.facecheck.domain.abstractClasses;


public abstract class BaseUserService {

    protected void validateEmail(String email) {
        if(email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

    }


    protected void validatePassword(String password) {
        if(password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException(" Password cannot be empty");
        }
    }

    protected void validateHomeAddress(String homeAddress) {
        if(homeAddress == null || homeAddress.trim().isEmpty()) {
            throw new IllegalArgumentException(" HomeAddress cannot be empty");
        }
    }

    protected void validatePhoneNumber(String phoneNumber) {
        if(phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(" PhoneNumber cannot be empty");
        }
    }



}
