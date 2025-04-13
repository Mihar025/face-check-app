package com.zikpak.facecheck.helperServices;

import com.zikpak.facecheck.domain.UserOperations;
import com.zikpak.facecheck.domain.abstractClasses.BaseUserService;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.mapper.UserMapper;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.UserCompanyNameInformation;
import com.zikpak.facecheck.requestsResponses.WorkerCompanyIdByAuthenticationResponse;
import com.zikpak.facecheck.requestsResponses.worker.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final WorkerPayRollService workerPayRollService;

    @Override
    public void updateEmail( String email, Authentication authentication) {
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
        var savedFullName = foundedUser.fullName();
        return userMapper.toUserFullNameResponse(savedFullName);
    }

    @Override
    public UserEmailResponse findWorkerEmail(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        var savedEmail = foundedUser.getEmail();
        return userMapper.toUserEmailResponse(savedEmail);
    }

    @Override
    public UserPhoneNumberResponse findWorkerPhoneNumber(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        return userMapper.toUserPhoneNumberResponse(foundedUser.getPhoneNumber());
    }

    @Override
    public UserHomeAddressResponse findWorkerHomeAddress(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        return userMapper.toUserHomeAddressResponse(foundedUser.getHomeAddress());
    }

    @Override
    public UserFullContactInformation findWorkerFullContactInformation(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        return userMapper.toFullUserInfoResponse( foundedUser);
    }

    @Override
    public UserCompanyNameInformation findUserCompanyName(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        log.info("Found user with id:" + foundedUser.getId());
        String savedCompanyName = foundedUser.getCompany().getCompanyName();
        log.info("Company name was founded: !" + savedCompanyName);
        return userMapper.toUserCompanyNameResponse(savedCompanyName);
    }

    public WorkerCompanyIdByAuthenticationResponse findCompanyByWorkerAuthentication(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        Integer foundedCompanyId = foundedUser.getCompany().getId();
        return userMapper.toWorkerCompanyIdByAuthenticationResponse(foundedCompanyId);
    }
}
