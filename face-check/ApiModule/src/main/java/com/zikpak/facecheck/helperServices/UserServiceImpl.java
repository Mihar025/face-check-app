package com.zikpak.facecheck.helperServices;

import com.zikpak.facecheck.domain.UserOperations;
import com.zikpak.facecheck.domain.abstractClasses.BaseUserService;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.mapper.UserMapper;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.*;
import com.zikpak.facecheck.requestsResponses.UserCompanyPhoneNumberResponse;
import com.zikpak.facecheck.requestsResponses.worker.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseUserService implements UserOperations {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    @Override
    public void updateEmail( String email, Authentication authentication) {
        if(email == null  || email.isBlank()){
            throw new IllegalArgumentException("Email cannot be empty");
        }
        log.info("Begin process");
        validateEmail(email);
        User user = ((User) authentication.getPrincipal());
        log.info("Finding user by id, through his credentials");
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        log.info("Found user with id:" + foundedUser.getId());
        foundedUser.setEmail(email);
        log.info("Updating email through setter!");
        userRepository.save(foundedUser);
        log.info("Email was successfully updated!");
    }

    @Override
    public void updatePassword( String password, Authentication authentication) {
        if(password == null || password.isBlank()){
            throw new IllegalArgumentException("Password cannot be empty");
        }
        validatePassword(password);
        log.info("Begin  process");
        User user = ((User) authentication.getPrincipal());
        log.info("Finding user  by id, through his credentials");
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        log.info("Found  user with id:" + foundedUser.getId());

        foundedUser.setPassword(passwordEncoder.encode(password));
        log.info("Updating password through setter!");
        userRepository.save(foundedUser);
        log.info("Password was successfully updated!");
    }


    @Override
    public void updatePhone( String phone, Authentication authentication) {
        if(phone == null || phone.isBlank()){

            throw new IllegalArgumentException("Phone cannot be empty");

        }
        validatePhoneNumber(phone);
        log.info(" Begin process");
        User user = ((User) authentication.getPrincipal());
        log.info(" Finding user by id, through his credentials");
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        log.info(" Found user with id:" + foundedUser.getId());

        foundedUser.setPhoneNumber(phone);
        log.info("Updating phonenumber through setter!");
        userRepository.save(foundedUser);
        log.info("Phonenumber was successfully updated!");
    }


    @Override
    public void updateHomeAddress( String homeAddress, Authentication authentication) {
        if(homeAddress == null || homeAddress.isBlank()){

            throw new IllegalArgumentException("Home address cannot be empty");

        }
        validateHomeAddress(homeAddress);
        log.info("Begin   process");
        User user = ((User) authentication.getPrincipal());
        log.info("Finding user by  id, through his credentials");
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        log.info("Found user with  id:" + foundedUser.getId());

        foundedUser.setHomeAddress(homeAddress);
        log.info("Updating Home Address through setter!");
        userRepository.save(foundedUser);
        log.info("Home address was successfully updated!");
    }

    @Override
    public UserFullNameResponse findWorkerFullName(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));

        if( foundedUser.fullName() == null || foundedUser.fullName().isBlank()){
            throw new IllegalArgumentException("Full users name is empty!");
        }

        var savedFullName = foundedUser.fullName();
        return userMapper.toUserFullNameResponse(savedFullName);
    }

    @Override
    public UserEmailResponse findWorkerEmail(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));

        if( foundedUser.getEmail() == null || foundedUser.getEmail().isBlank()){
            throw new IllegalArgumentException("Email is empty!");
        }

        var savedEmail = foundedUser.getEmail();
        return userMapper.toUserEmailResponse(savedEmail);
    }

    @Override
    public UserPhoneNumberResponse findWorkerPhoneNumber(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));

        if( foundedUser.getPhoneNumber() == null || foundedUser.getPhoneNumber().isBlank()){
            throw new IllegalArgumentException("Phone number is empty!");
        }

        return userMapper.toUserPhoneNumberResponse(foundedUser.getPhoneNumber());
    }

    @Override
    public UserHomeAddressResponse findWorkerHomeAddress(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));

        if(foundedUser.getHomeAddress() == null || foundedUser.getHomeAddress().isBlank()){
            throw new IllegalArgumentException("Home address is empty!");
        }
        return userMapper.toUserHomeAddressResponse(foundedUser.getHomeAddress());
    }

    @Override
    public UserFullContactInformation findWorkerFullContactInformation(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        return userMapper.toFullUserInfoResponse(foundedUser);
    }

    @Override
    public UserCompanyNameInformation findUserCompanyName(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        log.info("Found user with id:" + foundedUser.getId());

        Company c = foundedUser.getCompany();
        if (c == null || c.getCompanyName() == null || c.getCompanyName().isBlank()) {
            throw new IllegalArgumentException("Company name is empty!");
        }
        String savedCompanyName = c.getCompanyName();

        log.info("Company name was founded: !" + savedCompanyName);
        return userMapper.toUserCompanyNameResponse(savedCompanyName);
    }


    public UserCompanyAddressResponse findUserCompanyAddress(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        log.info("Found user with id:" + foundedUser.getId());

        Company c = foundedUser.getCompany();
        if (c == null || c.getCompanyAddress() == null || c.getCompanyAddress().isBlank()) {
            throw new IllegalArgumentException("Company Address is empty!");
        }
        String savedCompanyName = c.getCompanyAddress();

        log.info("Company name was founded: !" + savedCompanyName);
        return userMapper.toUserCompanyAddressResponse(savedCompanyName);
    }

    public UserCompanyPhoneNumberResponse findUserCompanyPhoneNumber(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        log.info("Found user with id:" + foundedUser.getId());

        Company c = foundedUser.getCompany();
        if (c == null || c.getCompanyPhone() == null || c.getCompanyPhone().isBlank()) {
            throw new IllegalArgumentException("Company Address is empty!");
        }
        String savedCompanyPhone= c.getCompanyPhone();

        log.info("Company name was founded: !" + savedCompanyPhone);
        return userMapper.toUserCompanyPhoneNumberResponse(savedCompanyPhone);
    }

    public UserCompanyEmailResponse findUserCompanyEmail(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        log.info("Found user with id:" + foundedUser.getId());

        Company c = foundedUser.getCompany();
        if (c == null || c.getCompanyEmail() == null || c.getCompanyEmail().isBlank()) {
            throw new IllegalArgumentException("Company Address is empty!");
        }
        String savedCompanyPhone= c.getCompanyEmail();

        log.info("Company name was founded: !" + savedCompanyPhone);
        return userMapper.toUserCompanyEmailResponse(savedCompanyPhone);


    }

    public WorkerCompanyIdByAuthenticationResponse findCompanyByWorkerAuthentication(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        Integer foundedCompanyId = foundedUser.getCompany().getId();
        if(foundedCompanyId == null){
            throw new IllegalArgumentException("Company id is empty!");
        }
        return userMapper.toWorkerCompanyIdByAuthenticationResponse(foundedCompanyId);
    }

    public WorkerPersonalInformationResponse findWorkerPersonInformation(Authentication authentication, Integer employeeId) {
        User admin = ((User) authentication.getPrincipal());
        if(!admin.isAdmin()){
            throw new AccessDeniedException("You dont have permission for this operation!");
        }
        var foundedUser = userRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + employeeId + " not found"));
        return userMapper.toWorkerPersonalInformationResponse(foundedUser);
    }



}
