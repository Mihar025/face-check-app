package com.zikpak.facecheck.domain;

import com.zikpak.facecheck.authRequests.CompanyRegistrationRequest;
import com.zikpak.facecheck.authRequests.PaymentRequest;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.Role;

import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import jakarta.mail.MessagingException;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;


public interface AuthenticationServiceRegAuth {

    Role findRoleUser();
    Role findRoleForeman();
    Role findRoleAdmin();

    Company findCompanyByName(String companyName);
    void checkIfUserAlreadyExists(String email) throws MessagingException;
    User checkIsHavePermissionForCreatingCompany(Authentication authentication);

    void setBusinessInformation(User user, Company company);
    Company createNewCompany(CompanyRegistrationRequest companyRegistrationRequest);
    void checkIsTheOwnerAndAdmin(Authentication authentication);
    User findUserById(Integer id);
    WorkerPayroll makeWorkerPayroll(BigDecimal hourRate, BigDecimal overtimeRate, Integer userId);




}
