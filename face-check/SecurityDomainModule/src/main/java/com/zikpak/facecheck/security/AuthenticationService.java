package com.zikpak.facecheck.security;

import com.zikpak.facecheck.authRequests.*;
import com.zikpak.facecheck.entity.Token;
import com.zikpak.facecheck.entity.User;

import com.zikpak.facecheck.mapper.UserMapper;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.TokenRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.requestsResponses.settings.*;
import jakarta.mail.MessagingException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.security.mailing.frontend.activation-url}")
    private String activationUrl;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final WorkerPayrollRepository workerPayrollRepository;
    private final JwtService jwtService;
    private final AuthenticationServiceImpl authenticationServiceImpl;


    @Transactional
    public void register(RegistrationRequest request) throws MessagingException {

       authenticationServiceImpl.checkIfUserAlreadyExists(request.getEmail());
       var role = authenticationServiceImpl.findRoleUser();
            var company = authenticationServiceImpl.findCompanyByName(request.getCompanyName());
            var user = userMapper.toWorker(request);

            user.setRoles(List.of(role));
            user.setCompany(company);
            userRepository.save(user);
        try {
            sendValidationEmail(user);
        } catch(MessagingException e) {
            log.error("Failed to send validation email", user.getEmail(), e);
            throw e;
        }
     }

    public void registerForeman(@Valid RegistrationRequest request) throws MessagingException {
        authenticationServiceImpl.checkIfUserAlreadyExists(request.getEmail());
        var role = authenticationServiceImpl.findRoleForeman();
         var company = authenticationServiceImpl.findCompanyByName(request.getCompanyName());
          var user = userMapper.toForeman(request);

            user.setRoles(List.of(role));
           user.setCompany(company);
            userRepository.save(user);
        try {
            sendValidationEmail(user);
        } catch(MessagingException e) {
            log.error("Failed to send validation email", user.getEmail(), e);
            throw e;
        }
        }



    public void registerAdmin(RegistrationAdminRequest request) throws MessagingException {
        authenticationServiceImpl.checkIfUserAlreadyExists(request.getEmail());
        var role = authenticationServiceImpl.findRoleAdmin();
          var user = userMapper.toAdmin(request);
            user.setRoles(List.of(role));

            userRepository.save(user);
        try {
            sendValidationEmail(user);
        } catch(MessagingException e) {
            log.error("Failed to send validation email", user.getEmail(), e);
            throw e;
        }
    }


    @Transactional(rollbackOn = Exception.class)
    public void setPaymentDataForWorkerHoursRateAndOvertime(Integer employeeId,
                                                            PaymentRequest paymentRequest,
                                                            Authentication authentication){
        var employee = authenticationServiceImpl.findUserById(employeeId);
        employee.setBaseHourlyRate(paymentRequest.getHourRate());
        employee.setOvertimeRate(paymentRequest.getOvertimeRate());
        var payment = authenticationServiceImpl.makeWorkerPayroll(employee.getBaseHourlyRate(), employee.getOvertimeRate(), employee.getId() );
        payment.setWorker(employee);
        payment.setCompany(employee.getCompany());
        workerPayrollRepository.save(payment);
    }


    @Transactional(rollbackOn = Exception.class)
    public void registerCompany (CompanyRegistrationRequest companyRegistrationRequest, Authentication authentication) throws MessagingException{
        User user = ((User) authentication.getPrincipal());
        if(!user.isAdmin()){
            throw new AccessDeniedException("You dont have permission for this operation");
        }

       var newCompany = authenticationServiceImpl.createNewCompany(companyRegistrationRequest);
       authenticationServiceImpl.setBusinessInformation(user, newCompany);
    }


    public AuthenticationResponse authenticate(@Valid AuthenticationRequest request) {
        try {
            log.info("Attempting authentication for user: {}", request.getEmail());

            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            log.info("Authentication successful for user: {}", request.getEmail());

            var claims = new HashMap<String, Object>();
            var user = ((User) auth.getPrincipal());
            claims.put("fullName", user.fullName());
            var jwtToken = jwtService.generateToken(claims, user);

            return AuthenticationResponse
                    .builder()
                    .token(jwtToken)
                    .build();
        } catch (Exception e) {
            log.error("Authentication failed for user: {}, error: {}",
                    request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired!");
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Invalid user"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }


    @Transactional(rollbackOn = Exception.class)
    public SettingsEmailResponse updateEmailInSettings( Authentication authentication,
                                                       SettingsEmailRequest updateEmail
    ){

        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with provided if do not found"));

       /* if(!foundedUser.getEmail().equals(updateEmail.getCurrentEmail())){
                    throw new RuntimeException("Invalid email");
        }

        */
        if(foundedUser.getEmail().equals(updateEmail.
        getNewEmail())){
            throw new RuntimeException("Provided email already used");
        }
        if(foundedUser.getEmail().equals(updateEmail.getConfirmNewEmail())){
            throw new RuntimeException("Provided email already used");
        }

        if(!updateEmail.getNewEmail().equals(updateEmail.getConfirmNewEmail())){
            throw new RuntimeException("Emails do not match");
        }

        foundedUser.setEmail(updateEmail.getConfirmNewEmail());
        var savedUser =  userRepository.save(foundedUser);
        return SettingsEmailResponse.builder()
                .email(savedUser.getEmail())
                .build();
    }

    @Transactional(rollbackOn = Exception.class)
    public void updatePasswordInSettings(Authentication authentication,
                                         SettingsPasswordRequest newPassword){
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with provided if do not found"));

          //  if(!foundedUser.getPassword().equals(newPassword.getCurrentPassword())){
          //      throw new RuntimeException("Passwords do not match 1 condition");
          //  }
            if(foundedUser.getPassword().equals(newPassword.getNewPassword())){
                throw new RuntimeException("You are already using this password");
            }
            if(foundedUser.getPassword().equals(newPassword.getConfirmPassword())){
                throw new RuntimeException("You are already using this password! Use another password!");
            }
            if(!newPassword.getNewPassword().equals(newPassword.getConfirmPassword())){
                throw new RuntimeException("Passwords do not match");
            }


            var hashedpassword = passwordEncoder.encode(newPassword.getConfirmPassword());
            foundedUser.setPassword(hashedpassword);
        userRepository.save(foundedUser);
    }
    @Transactional(rollbackOn = Exception.class)
    public SettingsPhoneNumberResponse updatePhoneNumber(Authentication authentication,
                                                         SettingsPhoneNumberRequest settingsPhoneNumberRequest){
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with provided if do not found"));
        if(!foundedUser.getPhoneNumber().equals(settingsPhoneNumberRequest.getCurrentPhoneNumber())){
            throw new RuntimeException("Phone number doesn't match");
        }
        if(foundedUser.getPhoneNumber().equals(settingsPhoneNumberRequest.getNewPhoneNumber())){
            throw new RuntimeException("Phone number already used");
        }
        if(foundedUser.getPhoneNumber().equals(settingsPhoneNumberRequest.getConfirmNewPhoneNumber())){
            throw new RuntimeException("Phone number already used");
        }
        if(!settingsPhoneNumberRequest.getNewPhoneNumber().equals(settingsPhoneNumberRequest.getConfirmNewPhoneNumber())){
            throw new RuntimeException("Phone numbers doesn't match! Try again!");
        }

        foundedUser.setPhoneNumber(settingsPhoneNumberRequest.getConfirmNewPhoneNumber());
        var savedPhoneNumber = userRepository.save(foundedUser);
        return SettingsPhoneNumberResponse.builder()
                .phoneNumber(savedPhoneNumber.getPhoneNumber())
                .build();
    }




    @Transactional
    public void sendPasswordResetCode(String email) throws MessagingException {
        log.info("Starting password reset process for email: {}", email);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        log.info("User found: {}", user.fullName());

        var token = generateAndSaveActivationToken(user);
        log.info("Generated token: {}", token);

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    user.fullName(),
                    EmailTemplateName.RESET_PASSWORD,
                    null,
                    token,
                    "Password Reset Code"
            );
            log.info("Reset email sent successfully");
        } catch (MessagingException e) {
            log.error("Failed to send reset email", e);
            throw e;
        }
    }

    @Transactional
    public void verifyPasswordResetCode(String email, String code) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        var tokenOpt = tokenRepository.findByTokenAndUser(code, user);
        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("Invalid or expired code");
        }

        var token = tokenOpt.get();
        if (LocalDateTime.now().isAfter(token.getExpiresAt())) {
            throw new RuntimeException("Code has expired");
        }

        token.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(token);
    }

    @Transactional
    public void resetPassword(ForgotPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        var latestToken = user.getTokens().stream()
                .filter(t -> t.getValidatedAt() != null)
                .max(Comparator.comparing(Token::getValidatedAt))
                .orElseThrow(() -> new RuntimeException("Please verify your reset code first"));

        if (LocalDateTime.now().isAfter(latestToken.getExpiresAt())) {
            throw new RuntimeException("Reset code has expired. Please request a new one");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        latestToken.setExpiresAt(LocalDateTime.now());
        tokenRepository.save(latestToken);
    }





    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account Activation"

        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for(int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
























}
