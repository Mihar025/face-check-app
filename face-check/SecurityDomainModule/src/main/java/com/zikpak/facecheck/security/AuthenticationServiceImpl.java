package com.zikpak.facecheck.security;


import com.amazonaws.services.kms.model.NotFoundException;
import com.zikpak.facecheck.authRequests.CompanyRegistrationRequest;
import com.zikpak.facecheck.authRequests.PaymentRequest;
import com.zikpak.facecheck.domain.AuthenticationServiceRegAuth;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.Role;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.mapper.CompanyMapper;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.RoleRepository;
import com.zikpak.facecheck.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationServiceRegAuth {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;


    @Override
    public Role findRoleUser() {
       return roleRepository.findByName("USER")
               .orElseThrow(() -> new RuntimeException("User Role Not Found and Initialize"));
    }

    @Override
    public Role findRoleForeman() {
        return roleRepository.findByName("FOREMAN")
                .orElseThrow(() -> new RuntimeException("FOREMAN Role Not Found and Initialize"));
    }

    @Override
    public Role findRoleAdmin() {
        return roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN Role Not Found and Initialize"));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Company findCompanyByName(String companyName) {
        var company = companyRepository.findByCompanyName(companyName)
                .orElseThrow(() -> new RuntimeException("Company Not Found"));
        companyRepository.incrementWorkers(companyName);
        return company;
    }

    @Override
    public void checkIfUserAlreadyExists(String email) throws MessagingException {
        if (userRepository.existsByEmail(email)) {
            throw new MessagingException("User with this email already exists");
        }
    }

    @Override
    public User checkIsHavePermissionForCreatingCompany(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        if(!user.isAdmin()){
            throw new AccessDeniedException("You dont have permission for this operation");
        }
        return user;
    }

    @Override
    public void setBusinessInformation(User user, Company company) {
        user.setBusinessOwner(true);
        user.setCompany(company);
        userRepository.save(user);
    }


    @Override
    public Company createNewCompany(CompanyRegistrationRequest companyRegistrationRequest) {
        return companyRepository.findByCompanyName(companyRegistrationRequest.getCompanyName())
                .orElseGet(() -> {
                    var company = companyMapper.createNewCompany(companyRegistrationRequest);
                    return companyRepository.save(company);
                });
    }

    @Override
    public void checkIsTheOwnerAndAdmin(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        if(!user.isAdmin() && !user.isBusinessOwner()){
            throw new AccessDeniedException("You dont have permission for this operation");
        }

    }

    @Override
    public User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invalid user")) ;
    }

    @Override
    public WorkerPayroll makeWorkerPayroll(BigDecimal hourRate, BigDecimal overtimeRate, Integer userId) {
        if(hourRate.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Hour rate must be greater than zero");
        }
        if(overtimeRate.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Overtime rate must be greater than zero");
        }

        var currentUser = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User with provided Id not found"));

        LocalDate currentDate = LocalDate.now();

        LocalDate periodStart = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate periodEnd = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        return WorkerPayroll.builder()
                .baseHourlyRate(currentUser.getBaseHourlyRate())
                .overtimeRate(currentUser.getOvertimeRate())
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .build();
    }


}
