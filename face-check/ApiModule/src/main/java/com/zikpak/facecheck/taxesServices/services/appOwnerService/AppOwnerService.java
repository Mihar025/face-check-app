package com.zikpak.facecheck.taxesServices.services.appOwnerService;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.mapper.CompanyMapper;
import com.zikpak.facecheck.mapper.UserMapper;
import com.zikpak.facecheck.mapper.WorkSiteMapper;
import com.zikpak.facecheck.repository.*;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.company.CompanyResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.worker.RelatedUserInCompanyResponse;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AppOwnerService {


    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    private final WorkerSiteRepository workSiteRepository;
    private final WorkSiteMapper workSiteMapper;
    private final UserRepository userRepository;
    private final WorkAttendanceService workAttendanceService;
    private final WorkerAttendanceRepository workerAttendanceRepository;
    private final UserMapper userMapper;


    public PageResponse<CompanyResponse> findAllCompanies(
            int page,
            int size,
            Authentication authentication) {

        checkIsUserHasAdminRoleAndBusinessOwner(authentication);

        Pageable pageable = PageRequest.of(page, size);
        Page<Company> companies = companyRepository.findAll(pageable);
        List<CompanyResponse> responses = companies.getContent()
                .stream()
                .map(companyMapper::toCompany)
                .toList();

        return new PageResponse<>(
                responses,
                companies.getNumber(),
                companies.getSize(),
                companies.getTotalElements(),
                companies.getTotalPages(),
                companies.isFirst(),
                companies.isLast()
        );
    }


    public PageResponse<WorkSiteResponse> findAllWorkSites(
            int page,
            int size,
            Integer companyId,
            Authentication authentication) {

        checkIsUserHasAdminRoleAndBusinessOwner(authentication);


        Pageable pageable = PageRequest.of(page, size);
        Page<WorkSite> sites = workSiteRepository.findAllByCompanyId(companyId,pageable);
        List<WorkSiteResponse> responses = sites.getContent()
                .stream()
                .map(workSiteMapper::toWorkSiteResponse)
                .toList();

        return new PageResponse<>(
                responses,
                sites.getNumber(),
                sites.getSize(),
                sites.getTotalElements(),
                sites.getTotalPages(),
                sites.isFirst(),
                sites.isLast()
        );
    }


    public PageResponse<RelatedUserInCompanyResponse> findAllRelatedUsersInCompany(
            int page,
            int size,
            Integer companyId,
            Authentication authentication) {

        checkIsUserHasAdminRoleAndBusinessOwner(authentication);


        Pageable pageable = PageRequest.of(page, size);
        Page<User> sites = userRepository.findAllEmployeesInCertainCompany(pageable, companyId);
        List<RelatedUserInCompanyResponse> responses = sites.getContent()
                .stream()
                .map(userMapper::toRelatedUserInCompanyResponse)
                .toList();

        return new PageResponse<>(
                responses,
                sites.getNumber(),
                sites.getSize(),
                sites.getTotalElements(),
                sites.getTotalPages(),
                sites.isFirst(),
                sites.isLast()
        );
    }
















    private User checkIsUserHasAdminRoleAndBusinessOwner(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        boolean isAppOwner = user.getRoles().stream()
                .anyMatch(role -> "AppOwner".equals(role.getName()));

        if(!user.isAdmin() && !user.isBusinessOwner() && !isAppOwner) {
            throw new AccessDeniedException("You dont have permission for this operation!");
        }
        return user;
    }


}
