package com.zikpak.facecheck.mapper;

import com.zikpak.facecheck.authRequests.RegistrationAdminRequest;
import com.zikpak.facecheck.authRequests.RegistrationRequest;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.requestsResponses.UserCompanyNameInformation;
import com.zikpak.facecheck.requestsResponses.WorkerCompanyIdByAuthenticationResponse;
import com.zikpak.facecheck.requestsResponses.WorkerPersonalInformationResponse;
import com.zikpak.facecheck.requestsResponses.admin.WorksiteWorkerResponse;
import com.zikpak.facecheck.requestsResponses.worker.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public UserFullNameResponse toUserFullNameResponse(String savedFullName) {
        return UserFullNameResponse.builder()
                .fullName(savedFullName)
                .build();
    }

    public UserEmailResponse toUserEmailResponse(String savedEmail) {
        return UserEmailResponse.builder()
                .email(savedEmail)
                .build();
    }

    public UserPhoneNumberResponse toUserPhoneNumberResponse(String phoneNumber) {
        return UserPhoneNumberResponse.builder()
                .phoneNumber(phoneNumber)
                .build();
    }

    public UserHomeAddressResponse toUserHomeAddressResponse(String homeAddress) {
        return UserHomeAddressResponse.builder()
                .homeAddress(homeAddress)
                .build();
    }

    public UserFullContactInformation toFullUserInfoResponse(User foundedUser) {
        return UserFullContactInformation.builder()
                .fullName(foundedUser.fullName())
                .email(foundedUser.getEmail())
                .phoneNumber(foundedUser.getPhoneNumber())
                .address(foundedUser.getHomeAddress())
                .photoUrl(foundedUser.getPhotoUrl())
                .photoFileName(foundedUser.getPhotoFileName())
                .build();
    }


    public User toWorker(RegistrationRequest request){
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .SSN_WORKER((request.getSSN_WORKER()))
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .accountLocked(false)
                .enabled(false)
                .isAdmin(false)
                .isForeman(false)
                .isUser(true)
                .homeAddress(request.getHomeAddress())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }

    public User toForeman(RegistrationRequest request){
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .SSN_WORKER((request.getSSN_WORKER()))
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .accountLocked(false)
                .enabled(false)
                .isAdmin(false)
                .isForeman(true)
                .isUser(false)
                .homeAddress(request.getHomeAddress())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }

    public User toAdmin(RegistrationAdminRequest request){
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .SSN_WORKER((request.getSSN_WORKER()))
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .accountLocked(false)
                .enabled(false)
                .isAdmin(true)
                .isForeman(false)
                .isUser(false)
                .homeAddress(request.getHomeAddress())
                .build();
    }

    public WorksiteWorkerResponse toUserWorkSiteResponse(User user) {
        return WorksiteWorkerResponse.builder()
                .workerId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .workSiteAddress(user.getWorkSites().stream()
                        .findFirst()
                        .map(WorkSite::getAddress)
                        .orElse(null))
                .punchIn(user.getAttendances().stream()
                        .max(Comparator.comparing(WorkerAttendance::getCheckInTime))
                        .map(WorkerAttendance::getCheckInTime)
                        .orElseThrow(() -> new EntityNotFoundException("No check-in time found for user: " + user.getId())))
                .build();
    }


    public UserFullContactInformation toUserFullContactInformation(User userInfo) {
        return UserFullContactInformation.builder()
                .fullName(userInfo.getFirstName() + " " + userInfo.getLastName())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .address(userInfo.getHomeAddress())
                .photoUrl(userInfo.getPhotoUrl())
                .photoFileName(userInfo.getPhotoFileName())
                .build();
    }

    public UserCompanyNameInformation toUserCompanyNameResponse(String savedCompanyName) {
        return UserCompanyNameInformation.builder()
                .companyName(savedCompanyName)
                .build();
    }

    public WorkerCompanyIdByAuthenticationResponse toWorkerCompanyIdByAuthenticationResponse(Integer foundedCompanyId) {
        return  WorkerCompanyIdByAuthenticationResponse.builder()
                .companyId(foundedCompanyId)
                .build();
    }

    public WorkerPersonalInformationResponse toWorkerPersonalInformationResponse(User foundedUser) {
        return WorkerPersonalInformationResponse.builder()
                .workerId(foundedUser.getId())
                .companyId(foundedUser.getCompany().getId())
                .firstName(foundedUser.getFirstName())
                .lastName(foundedUser.getLastName())
                .email(foundedUser.getEmail())
                .companyName(foundedUser.getCompany().getCompanyName())
                .phoneNumber(foundedUser.getPhoneNumber())
                .address(foundedUser.getHomeAddress())
                .baseHourlyRate(foundedUser.getBaseHourlyRate())
                .build();


    }
}
