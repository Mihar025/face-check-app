package com.zikpak.facecheck.services.company;


import com.zikpak.facecheck.domain.CompanyServiceImpl;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.mapper.CompanyMapper;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.RoleRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.requestsResponses.CompanyUpdatingRequest;
import com.zikpak.facecheck.requestsResponses.CompanyUpdatingResponse;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.company.CompanyResponse;
import com.zikpak.facecheck.requestsResponses.company.finance.*;
import com.zikpak.facecheck.requestsResponses.worker.RelatedUserInCompanyResponse;

import com.zikpak.facecheck.taxesServices.pdfServices.W2OfficialPDFService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService implements CompanyServiceImpl {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CompanyTaxCalculator companyTaxCalculator;
    private final UserRepository userRepository;
    private final WorkerPayrollRepository workerPayrollRepository;
    private final RoleRepository roleRepository;



    public CompanyStripeResponse findCompanyStripe(Integer companyId, Authentication authentication) {

        User admin = (User) authentication.getPrincipal();

        var company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Company with ID: " + companyId + " not found"));

        return CompanyStripeResponse.builder()
                .companyId(company.getId())
                .workersQuantity(company.getWorkersQuantity())
                .subscriptionStatus(company.getSubscriptionStatus())
                .build();
    }



    @Cacheable(
            value = "users",
            key = "'companyId_' + #authentication.principal.company.id"
    )
    public Integer findCompanyId(Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        Company company  =  companyRepository.findById(admin.getCompany().getId()).orElseThrow(EntityNotFoundException::new);
         return company.getId();
    }


    @Override
    @Cacheable(
            value = "users",
            key = "'companyName_' + #authentication.principal.company.id"
    )
    public String companyName(Authentication authentication) {
        try {

            if (authentication == null) {
                log.error("Authentication is null");
                return "";
            }

            User admin = (User) authentication.getPrincipal();

            if (admin.getCompany() == null) {
                log.error("User has no company associated");
                return "";
            }

            Integer companyId = admin.getCompany().getId();
            log.info("Company ID: {}", companyId);

            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new EntityNotFoundException("Company not found"));

            String companyName = company.getCompanyName() != null ? company.getCompanyName() : "";
            log.info("Returning company name: {}", companyName);

            return companyName;

        } catch (Exception e) {
            log.error("Error in companyName method", e);
            throw e;
        }
    }

    @Override
    @Cacheable(
            value = "users",
            key = "'companyAddress_' + #authentication.principal.company.id"
    )
    public String companyAddress(Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));
        return company.getCompanyAddress() != null ? company.getCompanyAddress() : "";
    }

    @Override
    @Cacheable(
            value = "users",
            key = "'companyPhone_' + #authentication.principal.company.id"
    )
    public String companyPhone(Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));
        // Если phone - это String
        return company.getCompanyPhone() != null ? company.getCompanyPhone() : "";
        // Если phone - это число (Long/Integer)
        // return company.getCompanyPhone() != null ? String.valueOf(company.getCompanyPhone()) : "";
    }

    @Override
    @Cacheable(
            value = "users",
            key = "'companyEmail_' + #authentication.principal.company.id"
    )
    public String companyEmail(Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));
        return company.getCompanyEmail() != null ? company.getCompanyEmail() : "";
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true)
    })
    public UpdateCompanyNameResponse updateCompanyName(UpdateCompanyNameRequest request, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();

        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));

        company.setCompanyName(request.getCompanyName());
        companyRepository.save(company);
        return  UpdateCompanyNameResponse.builder()
                .companyName(company.getCompanyName())
                .build();
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true)
    })
    public UpdateCompanyAddressResponse updateCompanyAddress(UpdateCompanyAddressRequest request, Authentication authentication) {

        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));

        company.setCompanyAddress(request.getCompanyAddress());
        companyRepository.save(company);
        return UpdateCompanyAddressResponse.builder()
                .companyAddress(company.getCompanyAddress())
                .build();
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true)
    })
    public UpdateCompanyPhoneNumberResponse updateCompanyPhoneNumber(UpdateCompanyPhoneNumberRequest request, Authentication authentication) {

        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));

        company.setCompanyPhone(request.getPhoneNumber());
        companyRepository.save(company);
        return UpdateCompanyPhoneNumberResponse.builder()
                .phoneNumber(company.getCompanyPhone())
                .build();

    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true)
    })
    public UpdateCompanyEmailResponse updateCompanyEmail(UpdateCompanyEmailRequest request, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));


        company.setCompanyEmail(request.getEmail());
        companyRepository.save(company);
        return UpdateCompanyEmailResponse.builder()
                .email(company.getCompanyEmail())
                .build();
    }



    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true)
    })
    public CompanyUpdatingResponse updateCompany(CompanyUpdatingRequest companyUpdatingRequest, Integer companyId, Authentication authentication) throws AccessDeniedException {

            User user = ((User) authentication.getPrincipal());
            boolean appOwner = user.getRoles().stream()
                    .anyMatch(role -> "AppOwner".equals(role.getName()));

            if(!user.isAdmin() && !user.isBusinessOwner() && !appOwner){
                throw new AccessDeniedException("You do not have permission to update this company");
            }
                var foundedCompany = companyRepository.findById(companyId)
                        .orElseThrow(() -> new RuntimeException("Company not found"));
                var updatedCompany = updateCompanyCredentials(companyUpdatingRequest, foundedCompany);
                    return companyMapper.toCompanyUpdateResponse(updatedCompany);
        }




            public CompanyIncomePerMonthResponse setCompanyIncomePerMonth(
                    CompanyIncomePerMonthRequest request,
                    Integer companyId,
                    Authentication authentication) throws AccessDeniedException {
                if(request.getCompanyIncomePerMonth() == null){
                    throw new IllegalArgumentException("Company income per month cannot be null");
                }

                var user = ((User) authentication.getPrincipal());
                if(!user.isAdmin() && !user.isBusinessOwner()){
                   throw new
                           AccessDeniedException(
                                   "You do not have permission to update this company"
                                                );
                                                             }
                var foundedCompany = companyRepository.findById(companyId)
                        .orElseThrow(() -> new RuntimeException("Company not found"));

                    LocalDate periodStart = LocalDate.now();
                    LocalDate periodEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());

                    var newPayRoll = WorkerPayroll.builder()
                            .company(foundedCompany)
                            .periodStart(periodStart)
                            .periodEnd(periodEnd)
                            .build();

                       workerPayrollRepository.save(newPayRoll);

                       foundedCompany.setCompanyIncomePerMonth(request.getCompanyIncomePerMonth());

                       var updatedIncome = companyRepository.save(foundedCompany);

                       return companyMapper.toCompanyIncomeResponse(updatedIncome);
            }



            @Transactional
            public CompanyTaxCalculationResponse countAllTaxesByCompanyIncome(
                                                                              CompanyTaxCalculationRequest request,
                                                                              Authentication authentication)
                                                                              throws AccessDeniedException {
                    if(request.getYear() == null || request.getMonth() == null){
                        throw new IllegalArgumentException("Year or Month cannot be null try again!");
                    }

                    var user = ((User) authentication.getPrincipal());
                    if(!user.isAdmin() && !user.isBusinessOwner()){
                        throw new AccessDeniedException("You do not have permission to update this company");
                      }

                    var foundedCompany = companyRepository.findById(user.getCompany().getId())
                            .orElseThrow(() -> new RuntimeException("Company not found"));

                        var countedTaxes = companyTaxCalculator.calculateTax(foundedCompany.getCompanyIncomePerMonth(), request, foundedCompany.getId());

                        return companyMapper.toCountedTaxesResponse(countedTaxes);
            }


                    public CompanyIncomePerMonthResponse findCompanyIncomePerMonth(Integer companyId,
                                                                                    Authentication authentication)
                                                                                    throws AccessDeniedException {

                                var user = ((User) authentication.getPrincipal());
                                    if(!user.isAdmin() && !user.isBusinessOwner()){
                                       throw new
                                                AccessDeniedException(
                                                        "You do not have permission to update this company"
                                                                     );
                                    }
                                        var foundedCompany = companyRepository.findById(companyId)
                                            .orElseThrow(() -> new RuntimeException("Company not found"));
                                            return companyMapper.toCompanyIncomeResponse(foundedCompany);
            }

    @Transactional
    public BigDecimal countSalariesInTotalForAllEmployeePerMonth(Authentication authentication) throws AccessDeniedException {
        User user = ((User) authentication.getPrincipal());
        if(!user.isAdmin() && !user.isBusinessOwner()){
            throw new AccessDeniedException("You do not have permission to update this company");
        }
        var foundedCompany = companyRepository.findById(user.getCompany().getId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        List<User> employees = foundedCompany.getEmployees();

        if (employees.isEmpty()) {
            log.error("Cannot find any employees for company ID: {}", foundedCompany.getId());
            throw new RuntimeException("No employees found in company");
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate monthStart = currentDate.withDayOfMonth(1);
        LocalDate monthEnd = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        return employees.stream()
                .flatMap(employee -> employee.getPayrolls().stream())
                .filter(payroll ->
                        (payroll.getPeriodStart() != null && payroll.getPeriodEnd() != null) &&
                                (!payroll.getPeriodStart().isAfter(monthEnd) &&
                                        !payroll.getPeriodEnd().isBefore(monthStart)))
                .map(WorkerPayroll::getNetPay)
                .filter(netPay -> netPay != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }




                public List<EmployeeSalaryResponse> findAllEmployeesBaseHourRate(Authentication authentication) throws AccessDeniedException {
                         User user = ((User) authentication.getPrincipal());
                        if(!user.isAdmin() && !user.isBusinessOwner()){
                            throw new AccessDeniedException("You do not have permission to update this company");
                        }

                            List<EmployeeSalaryResponse> employeeSalaryResponses = companyRepository.findAllInCompanyByBaseHourRate(user.getCompany().getId());
                                if(employeeSalaryResponses.isEmpty()){
                                    throw new RuntimeException("Employee salary not found");
                                }
                                return employeeSalaryResponses;
                }

                public EmployeeSalaryResponse findTheLatestEmployeeBaseHourRate(Integer companyId,
                                                                                Integer userId,
                                                                                Authentication authentication) throws AccessDeniedException {
                            User user = ((User) authentication.getPrincipal());

                            if(!user.isAdmin() && !user.isBusinessOwner()){
                                throw new AccessDeniedException("You do not have permission to update this company");
                            }

                                var foundedCompany = companyRepository.findById(companyId)
                                        .orElseThrow(() -> new RuntimeException("Company not found"));
                                    var employeeSalary = companyRepository.findEmployeeByCompanyAndUserIdWithBaseHourRate(foundedCompany.getId(), userId);
                            return employeeSalary;
                    }


    @Transactional
    @CacheEvict(value = "users", allEntries = true)  // Очищаем весь кеш users
    public EmployeeSalaryResponse changeEmployeeBaseHourRate(Integer companyId,
                                                             Integer employeeId,
                                                             Authentication authentication,
                                                             EmployeeRaiseHourRateRequest raise) throws AccessDeniedException {
        if(raise.getBaseHourlyRate() == null){
            throw new IllegalArgumentException("Base hourly rate cannot be empty");
        }

        User user = ((User) authentication.getPrincipal());

        if(!user.isAdmin() && !user.isBusinessOwner()){
            throw new AccessDeniedException("You do not have permission to update this company");
        }

        var foundedCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        var foundedEmployee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if(!user.getCompany().getId().equals(foundedCompany.getId())){
            throw new AccessDeniedException("This worker is not from your company");
        }

        // ✅ ИСПРАВЛЕНО: сравниваем с зарплатой работника, а не админа
        if(raise.getBaseHourlyRate().equals(foundedEmployee.getBaseHourlyRate())){
            throw new IllegalArgumentException("Base hourly rate cannot be the same as current hourly rate");
        }

        WorkerPayroll currentPayroll;

        if(foundedEmployee.getPayrolls().isEmpty()){
            currentPayroll = new WorkerPayroll();
            currentPayroll.setWorker(foundedEmployee);
            currentPayroll.setPeriodStart(LocalDate.now());
            currentPayroll.setBaseHourlyRate(raise.getBaseHourlyRate());
        }
        else{
            currentPayroll = foundedEmployee.getPayrolls()
                    .stream()
                    .max(Comparator.comparing(WorkerPayroll::getPeriodStart))
                    .orElseThrow(() -> new RuntimeException("Employee payroll not found"));

            if (currentPayroll.getPeriodEnd() == null) {
                // Обновляем существующий активный payroll
                currentPayroll.setBaseHourlyRate(raise.getBaseHourlyRate());
            }
            else {
                // ✅ ИСПРАВЛЕНО: создаём новый payroll и присваиваем его
                currentPayroll = new WorkerPayroll();
                currentPayroll.setWorker(foundedEmployee);
                currentPayroll.setPeriodStart(LocalDate.now());
                currentPayroll.setBaseHourlyRate(raise.getBaseHourlyRate());
                currentPayroll.setOvertimeRate(foundedEmployee.getOvertimeRate());
            }
        }

        // Сохраняем payroll
        workerPayrollRepository.save(currentPayroll);

        // Также обновляем базовую ставку у самого работника
        foundedEmployee.setBaseHourlyRate(raise.getBaseHourlyRate());
        userRepository.save(foundedEmployee);

        return new EmployeeSalaryResponse(
                foundedEmployee.getId(),
                foundedEmployee.getFirstName(),
                foundedEmployee.getLastName(),
                foundedEmployee.getEmail(),
                raise.getBaseHourlyRate()
        );
    }

    @Cacheable(
            value = "users",
            key = "'employee_' + #workerId + '_company_' + #companyId"
    )
                        public RelatedUserInCompanyResponse findEmployeeInCertainCompany(Integer workerId, Integer companyId, Authentication authentication) throws AccessDeniedException {

                            User user = ((User) authentication.getPrincipal());
                            boolean appOwner = user.getRoles().stream()
                                    .anyMatch(role -> "AppOwner".equals(role.getName()));

                            if(!user.isAdmin() && !user.isBusinessOwner() && !appOwner){
                                throw new AccessDeniedException("You do not have permission to update this company");
                            }
                                    if(!user.getCompany().getId().equals(companyId)){
                                         throw new AccessDeniedException("Something went wrong");
                                        }

                                    var foundedEmployee = userRepository.findById(workerId)
                                            .orElseThrow(() -> new RuntimeException("Employee not found"));
                                        if(!foundedEmployee.getCompany().getId().equals(companyId)){
                                                throw new AccessDeniedException("Something went wrong");
                                        }
                                        return companyMapper.toCompanyWorkerResponse(foundedEmployee);
                        }


    @Cacheable(
            value = "users",
            key = "'companyEmployees_' + #companyId + '_page_' + #page + '_size_' + #size",
            unless = "#result == null || #result.content.isEmpty()"
    )
    public PageResponse<RelatedUserInCompanyResponse> findAllEmployeesInCertainCompany(
            int page,
            int size,
            Integer companyId,
            Authentication authentication) {

        User user = ((User) authentication.getPrincipal());

        boolean appOwner = user.getRoles().stream()
                .anyMatch(role -> "AppOwner".equals(role.getName()));

        if (!user.isAdmin() && !user.getCompany().getId().equals(companyId) && !appOwner) {
            log.warn("Unauthorized access attempt by user: {}", user.getEmail());
            throw new AccessDeniedException("You do not have permission to view company employees");
        }

        List<User> allUsers = userRepository.findAllEmployeesInCompanyWithDetails(companyId);

        int start = page * size;
        int end = Math.min(start + size, allUsers.size());
        List<User> pagedUsers = allUsers.subList(start, end);

        List<RelatedUserInCompanyResponse> responses = pagedUsers
                .stream()
                .map(companyMapper::toCompanyWorkerResponse)
                .toList();

        return new PageResponse<>(
                responses,
                page,
                size,
                allUsers.size(),
                (int) Math.ceil((double) allUsers.size() / size),
                page == 0,
                end >= allUsers.size()
        );
    }


    @Cacheable(
            value = "users",
            key = "'allEmployees_page_' + #page + '_size_' + #size",
            unless = "#result == null || #result.content.isEmpty()"
    )
    public PageResponse<RelatedUserInCompanyResponse> findAllEmployees(
            int page,
            int size,
            Authentication authentication) {

        User user = ((User) authentication.getPrincipal());

        boolean appOwner = user.getRoles().stream()
                .anyMatch(role -> "AppOwner".equals(role.getName()));

        if (!user.isAdmin() && !appOwner) {
            log.warn("Unauthorized access attempt by user: {}", user.getEmail());
            throw new AccessDeniedException("You do not have permission to view company employees");
        }

        // ✅ ИСПОЛЬЗУЕМ JOIN FETCH
        List<User> allUsers = userRepository.findAllEmployeesForAppOwner();

        log.info("Total users found: {}", allUsers.size());
        log.info("Current page: {}, Size: {}", page, size);

        // ✅ Пагинация вручную
        int start = page * size;
        int end = Math.min(start + size, allUsers.size());

        if (start > allUsers.size()) {
            start = 0;
            end = 0;
        }

        List<User> pagedUsers = start < end ? allUsers.subList(start, end) : List.of();

        log.info("Users on this page: {}", pagedUsers.size());

        if (!pagedUsers.isEmpty()) {
            User firstUser = pagedUsers.get(0);
            log.info("First user: {} {}, email: {}",
                    firstUser.getFirstName(),
                    firstUser.getLastName(),
                    firstUser.getEmail());
        }

        List<RelatedUserInCompanyResponse> responses = pagedUsers
                .stream()
                .map(companyMapper::toCompanyWorkerResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) allUsers.size() / size);

        return new PageResponse<>(
                responses,
                page,
                size,
                allUsers.size(),
                totalPages,
                page == 0,
                end >= allUsers.size()
        );
    }

    @Cacheable(
            value = "users",
            key = "'allCompanies_page_' + #page + '_size_' + #size",
            unless = "#result == null || #result.content.isEmpty()"
    )
    public PageResponse<CompanyResponse> findAllCompanies(
            int page,
            int size,
            Authentication authentication) {

        User user = ((User) authentication.getPrincipal());

        boolean appOwner = user.getRoles().stream()
                .anyMatch(role -> "AppOwner".equals(role.getName()));

        if (!user.isAdmin() && !appOwner) {
            log.warn("Unauthorized access attempt by user: {}", user.getEmail());
            throw new AccessDeniedException("You do not have permission to view company employees");
        }

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

    @Cacheable(
            value = "users",
            key = "'workersQuantity_company_' + #companyId"  // ← ИСПРАВЬ!
    )
    public long findWorkersQuantityInCertainCompany(Integer companyId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        boolean appOwner = user.getRoles().stream()
                .anyMatch(role -> "AppOwner".equals(role.getName()));

        if (!user.isAdmin() && !user.getCompany().getId().equals(companyId) && !appOwner) {
            log.warn("Unauthorized access attempt by user: {}", user.getEmail());
            throw new AccessDeniedException("You do not have permission to view company employees");
        }

        return userRepository.countEmployeesInCompany(companyId);
    }






    public PageResponse<RelatedUserInCompanyResponse> findAllEmployeesWhoseRoleAreUser(int page, int size, Integer companyId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        if (!user.isAdmin() && user.getCompany().getId().equals(companyId)) {
            log.warn("Unauthorized access attempt by user: {}", user.getEmail());
        }
            companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<User> users = userRepository.findAllEmployeesInCompanyWhoseRoleIsUser(pageable, companyId);
            if(users.isEmpty()) {
                log.info("No employees found for company: {}", companyId);
            }
                List<RelatedUserInCompanyResponse> relatedUserInCompanyResponses = users.getContent()
                        .stream()
                        .map(companyMapper::toCompanyWorkerResponse)
                        .toList();
                return new PageResponse<>(
                        relatedUserInCompanyResponses,
                        users.getNumber(),
                        users.getSize(),
                        users.getTotalElements(),
                        users.getTotalPages(),
                        users.isFirst(),
                        users.isLast()
                );
    }

    public PageResponse<RelatedUserInCompanyResponse> findAllEmployeesWhoseRoleAreForeman(int page, int size, Integer companyId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        if (!user.isAdmin() && user.getCompany().getId().equals(companyId)) {
            log.warn("Unauthorized access attempt by user: {}", user.getEmail());
        }
        companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<User> users = userRepository.findAllEmployeesInCompanyWhoseRoleIsForeman(pageable, companyId);
        if(users.isEmpty()) {
            log.info("No employees found for company: {}", companyId);
        }
        List<RelatedUserInCompanyResponse> relatedUserInCompanyResponses = users.getContent()
                .stream()
                .map(companyMapper::toCompanyWorkerResponse)
                .toList();
        return new PageResponse<>(
                relatedUserInCompanyResponses,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast()
        );
    }

    public PageResponse<RelatedUserInCompanyResponse> findAllEmployeesWhoseRoleAreAdmin(int page,
                                                                                        int size,
                                                                                        Integer companyId,
                                                                                        Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        if (!user.isAdmin() && user.getCompany().getId().equals(companyId)) {
            log.warn("Unauthorized access attempt by" + " user: {}", user.getEmail());
        }
        companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<User> users = userRepository.findAllEmployeesInCompanyWhoseRoleIsAdmin(pageable, companyId);
        if(users.isEmpty()) {
            log.info("No employees found for company: {}", companyId);
        }
        List<RelatedUserInCompanyResponse> relatedUserInCompanyResponses = users.getContent()
                .stream()
                .map(companyMapper::toCompanyWorkerResponse)
                .toList();
        return new PageResponse<>(
                relatedUserInCompanyResponses,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast()
        );
    }
    /*
    A)  if user is admin and business owner he could raise somebody role, for an example we want to assign user with id 2, ADMIN,
            only user with role admin and isBusinessOwner = true could do that! By another way:
            Business owner could make User : ADMIN!

   B)
        a) If we have simple Admin, he cannot change  somebody boolean value, and assign to the worker with role USER, role ADMIN!
            only business owner could assign admin role!
        b) Admin could assign to the user role FOREMAN!
        c) Admin could decrease role FOREMAN back to USER!
        d) Business owner could demote some ones role from ADMIN , FOREMAN to USER!

            FUTURE!
        only admin could change schedule etc for WORKERS, and FOREMAN could change schedule for worker, only with admin permission
    */


    @Transactional(rollbackOn = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true)
    })
    public void raiseToForemanRoleInCompany(Integer workerId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        if (!user.isAdmin() && !user.isBusinessOwner()) {
            log.warn("Unauthorized access  attempt by user: {}", user.getEmail());
            throw new AccessDeniedException("Access denied");
        }

        var foundedWorker = userRepository.findById(workerId)
                    .orElseThrow(() -> new RuntimeException("Worker not found with id: " + workerId));
            if(!user.getCompany().getId().equals(foundedWorker.getCompany().getId())) {
                throw new AccessDeniedException("You cannot promote foreman role to the employee whose not exist in the company  ");
            }

        var FOREMAN_ROLE = roleRepository.findByName("FOREMAN")
                .orElseThrow(() -> new RuntimeException("FOREMAN role not found"));

            if(foundedWorker.getRoles().stream().anyMatch(role -> role.getName().equals("FOREMAN"))) {
                    throw new RuntimeException("FOREMAN role already signed to this worker");
            }

                    foundedWorker.setUser(false);
                    foundedWorker.setForeman(true);
                    foundedWorker.setLastModifiedDate(LocalDateTime.now());
                    foundedWorker.getRoles().clear();
                    foundedWorker.getRoles().add(FOREMAN_ROLE);

                    userRepository.save(foundedWorker);
    }



    @Transactional(rollbackOn = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true)
    })
    public void raiseToAdminRoleInCompany(Integer workerId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());

        if (!user.isAdmin() && !user.isBusinessOwner()) {
            log.warn("Unauthorized access    attempt by user: {}", user.getEmail());
            throw new AccessDeniedException("Access denied");
        }


        var foundedWorker = userRepository.findById(workerId)
                  .orElseThrow(() -> new RuntimeException("Worker not found with id: " + workerId));
        if(!user.getCompany().getId().equals(foundedWorker.getCompany().getId())) {
            throw new AccessDeniedException("You cannot promote Admin role to the employee whose not exist in the company  ");
        }

        var ADMIN_ROLE = roleRepository.findByName("ADMIN")
                 .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        if(foundedWorker.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            throw new RuntimeException("ADMIN role already signed to this worker");
        }
                foundedWorker.setUser(false);
                foundedWorker.setForeman(false);
                foundedWorker.setAdmin(true);
                foundedWorker.setLastModifiedDate(LocalDateTime.now());
                foundedWorker.getRoles().clear();
                foundedWorker.getRoles().add(ADMIN_ROLE);

                userRepository.save(foundedWorker);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true)
    })
        public void demoteFromAdminToForeman(Integer workerId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        if (!user.isAdmin() && !user.isBusinessOwner()) {
            log.warn("Unauthorized access attempt by user: {}", user.getEmail());
            throw new AccessDeniedException("Access denied");
        }

        var foundedWorker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found with id: " + workerId));
                log.info("Founded worker with id: {}", foundedWorker.getId());

        var FOREMAN_ROLE = roleRepository.findByName("FOREMAN")
                .orElseThrow(() -> new RuntimeException("FOREMAN role not found"));
                log.info("FOREMAN role was initialized: {}", FOREMAN_ROLE.getName());

            if(foundedWorker.getRoles().stream().anyMatch(role -> role.getName().equals("FOREMAN"))) {
                throw new RuntimeException("FOREMAN role already signed to this worker, cannot DEMOTE it!");
            }
            foundedWorker.setUser(false);
            foundedWorker.setForeman(true);
            foundedWorker.setAdmin(false);
            foundedWorker.setLastModifiedDate(LocalDateTime.now());
            foundedWorker.getRoles().clear();
            foundedWorker.getRoles().add(FOREMAN_ROLE);

            userRepository.save(foundedWorker);
        }


    @Transactional(rollbackOn = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true)
    })
    public void demoteFromForemanToUser(Integer workerId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        if (!user.isAdmin()) {
            log.warn("Unauthorized  access attempt by user: {}", user.getEmail());
            throw new AccessDeniedException("Access denied");
        }

        var foundedWorker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found with id: " + workerId));
        log.info("Founded worker  with id: {}", foundedWorker.getId());

        var USER_ROLE = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));
        log.info("USER role was initialized: {}", USER_ROLE.getName());

        if(foundedWorker.getRoles().stream().anyMatch(role -> role.getName().equals("USER"))) {
            throw new RuntimeException("ГЫУК role already signed to this worker, cannot DEMOTE it!");
        }
        foundedWorker.setUser(true);
        foundedWorker.setForeman(false);
        foundedWorker.setAdmin(false);
        foundedWorker.setLastModifiedDate(LocalDateTime.now());
        foundedWorker.getRoles().clear();
        foundedWorker.getRoles().add(USER_ROLE);

        userRepository.save(foundedWorker);
    }






    @Transactional(rollbackOn = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true)
    })
    public void fireEmployee(Integer workerId, Authentication authentication) throws AccessDeniedException {
        User user = ((User) authentication.getPrincipal());

        if(!user.isAdmin() && !user.isBusinessOwner()){
            throw new AccessDeniedException("You do not have permission to update this company");
        }

        var foundedEmployee = userRepository.findById(workerId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        if (!foundedEmployee.getCompany().getId().equals(user.getCompany().getId())) {
            throw new AccessDeniedException("Something went wrong");
        }

        var company = foundedEmployee.getCompany();
        company.setWorkersQuantity(company.getWorkersQuantity() - 1);
        companyRepository.save(company);

        userRepository.deleteById(foundedEmployee.getId());
    }









            @Transactional(rollbackOn = Exception.class)
            @Caching(evict = {
                    @CacheEvict(value = "users", allEntries = true)
            })
            public void deleteCompany(Integer companyId, Authentication authentication) throws AccessDeniedException {
            User user = ((User) authentication.getPrincipal());
            if(!user.isAdmin() && !user.isBusinessOwner()){
                throw new AccessDeniedException("You do not have permission to delete this company");
            }
            var company = companyRepository.findById(companyId).orElseThrow(
                    () -> new EntityNotFoundException("Company not found")
            );
            companyRepository.deleteById(company.getId());
            }


        private Company updateCompanyCredentials(CompanyUpdatingRequest request, Company company){
            company.setCompanyName(request.getCompanyName());
            company.setCompanyAddress(request.getCompanyAddress());
            company.setWorkersQuantity(request.getWorkersQuantity());
            company.setCompanyPhone(request.getCompanyPhone());
            company.setCompanyEmail(request.getCompanyEmail());
            return companyRepository.save(company);
        }

}

