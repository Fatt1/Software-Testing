package com.flogin.service;

import com.flogin.dto.LoginDto.LoginRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AuthValidationService {
    // Các hằng số cho quy tắc
    private static final int MIN_USER_LEN = 3;
    private static final int MAX_USER_LEN = 50;
    private static final int MIN_PASS_LEN = 6;
    private static final int MAX_PASS_LEN = 100;

    // Regex cho phép a-z, A-Z, 0-9, và các ký tự _, -, .
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]+$");
    // Regex cho password: phải chứa ít nhất 1 chữ
    private static final Pattern PASS_LETTER_PATTERN = Pattern.compile(".*[a-zA-Z]+.*");
    // Regex cho password: phải chứa ít nhất 1 số
    private static final Pattern PASS_NUMBER_PATTERN = Pattern.compile(".*[0-9]+.*");

    public List<String> validate(LoginRequest request){
        List<String> errors = new ArrayList<String>();

        // 1. Validate Username
        String username = request.getUserName();
        if (username == null || username.trim().isEmpty()) {
            errors.add("Username không được để trống");
        } else {
            if (username.length() < MIN_USER_LEN || username.length() > MAX_USER_LEN) {
                errors.add("Username phải từ 3 đến 50 ký tự");
            }
            if (!USERNAME_PATTERN.matcher(username).matches()) {
                errors.add("Username chỉ chứa chữ, số, và ký tự (-, ., _)");
            }
        }

        // 2. Validate Password
        String password = request.getPassword();
        if (password == null || password.isEmpty()) {
            errors.add("Password không được để trống");
        } else {
            if (password.length() < MIN_PASS_LEN || password.length() > MAX_PASS_LEN) {
                errors.add("Password phải từ 6 đến 100 ký tự");
            }
            if (!PASS_LETTER_PATTERN.matcher(password).matches()) {
                errors.add("Password phải chứa ít nhất 1 chữ cái");
            }
            if (!PASS_NUMBER_PATTERN.matcher(password).matches()) {
                errors.add("Password phải chứa ít nhất 1 chữ số");
            }
        }

        return errors;
    }
}
