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
import com.zikpak.facecheck.requestsResponses.company.finance.*;
import com.zikpak.facecheck.requestsResponses.worker.RelatedUserInCompanyResponse;
import com.zikpak.facecheck.requestsResponses.worker.UpdateEmployeeDataRequest;
import com.zikpak.facecheck.requestsResponses.worker.UpdatedEmployeeDataResponse;
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


    @Transactional
    @Override
    public String companyName(Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));
        return company.getCompanyName();
    }
    @Transactional
    @Override
    public String companyAddress(Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));
        return company.getCompanyAddress();
    }
    @Transactional
    @Override
    public String companyPhone(Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));
        return company.getCompanyPhone();
    }
    @Transactional
    @Override
    public String companyEmail(Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));
        return company.getCompanyEmail();
    }
    @Transactional
    @Override
    public void updateCompanyName(String name, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));
        if(company.getCompanyName().equals(name)) {
            throw new AccessDeniedException("Company name already exists");
        }
        else if(name.isEmpty()){
            throw new AccessDeniedException("Company name cannot be empty");
        }
        company.setCompanyName(name);
    }
    @Transactional
    @Override
    public void updateCompanyAddress(String address, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));
        if(company.getCompanyAddress().equals(address)) {
            throw new AccessDeniedException("Company address already exists");
        }
        else if(address.isEmpty()){
            throw new AccessDeniedException("Company address cannot be empty");
        }
        company.setCompanyAddress(address);

    }
    @Transactional
    @Override
    public void updateCompanyPhoneNumber(String phoneNumber, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));
        if(company.getCompanyPhone().equals(phoneNumber)) {
            throw new AccessDeniedException("Company phoneNumber already exists");
        }
        else if(phoneNumber.isEmpty()){
            throw new AccessDeniedException("Company phoneNumber cannot be empty");
        }
        company.setCompanyPhone(phoneNumber);

    }
    @Transactional
    @Override
    public void updateCompanyEmail(String email, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        var company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + admin.getCompany().getId() + " not found"));
        if(company.getCompanyEmail().equals(email)) {
            throw new AccessDeniedException("Company email already exists");
        }
        else if(email.isEmpty()){
            throw new AccessDeniedException("Company email cannot be empty");
        }
        company.setCompanyEmail(email);

    }


    public CompanyUpdatingResponse updateCompany(CompanyUpdatingRequest companyUpdatingRequest, Integer companyId, Authentication authentication) throws AccessDeniedException {
            User user = ((User) authentication.getPrincipal());

            if(!user.isAdmin() && !user.isBusinessOwner()){
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

                // Set of 3 methods
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

                        public EmployeeSalaryResponse changeEmployeeBaseHourRate(Integer companyId,
                                                                                 Integer employeeId,
                                                                                 Authentication authentication,
                                                                                 EmployeeRaiseHourRateRequest raise) throws AccessDeniedException {
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
                            WorkerPayroll currentPayroll;
                                                    if(foundedEmployee.getPayrolls().isEmpty()){
                                                        currentPayroll = new WorkerPayroll();
                                                        currentPayroll.setWorker(foundedEmployee);
                                                        currentPayroll.setPeriodStart(LocalDate.now());
                                                    }
                                                    else{
                                                        currentPayroll = foundedEmployee.getPayrolls()
                                                                .stream()
                                                                .max(Comparator.comparing(WorkerPayroll::getPeriodStart))
                                                                .orElseThrow(() -> new RuntimeException("Employee payroll not found"));
                                                        if (currentPayroll.getPeriodEnd() == null) {
                                                            currentPayroll.setBaseHourlyRate(raise.getBaseHourlyRate());
                                                        }
                                                        else {
                                                            WorkerPayroll newPayroll = new WorkerPayroll();
                                                            newPayroll.setWorker(foundedEmployee);
                                                            newPayroll.setPeriodStart(LocalDate.now());
                                                            newPayroll.setBaseHourlyRate(raise.getBaseHourlyRate());
                                                            newPayroll.setOvertimeRate(currentPayroll.getOvertimeRate());
                                                        }
                                                    }
                                                    currentPayroll.setBaseHourlyRate(raise.getBaseHourlyRate());
                                                    workerPayrollRepository.save(currentPayroll);
                                            return new EmployeeSalaryResponse(
                                                    foundedEmployee.getId(),
                                                    foundedEmployee.getFirstName(),
                                                    foundedEmployee.getLastName(),
                                                    foundedEmployee.getEmail(),
                                                    currentPayroll.getBaseHourlyRate()
                                            )   ;

                        }
                                @Transactional(rollbackOn = Exception.class)
                        public UpdatedEmployeeDataResponse updateEmployeeData(Integer employeeId, UpdateEmployeeDataRequest request, Authentication authentication) throws AccessDeniedException {
                                User user = ((User) authentication.getPrincipal());
                                if(!user.isAdmin() && !user.isBusinessOwner()){
                                    throw new AccessDeniedException("You do not have permission to update this company");
                                }

                                    var foundedWorker = userRepository.findById(employeeId)
                                            .orElseThrow(() -> new RuntimeException("Employee not found"));
                                        if(!foundedWorker.getId().equals(employeeId)){
                                            throw new AccessDeniedException("Something went wrong");
                                        }
                                            else {
                                                if(!request.getFirstName().isEmpty()) {
                                                    foundedWorker.setFirstName(request.getFirstName());
                                                }
                                                    if(!request.getLastName().isEmpty()) {
                                                        foundedWorker.setLastName(request.getLastName());
                                                    }
                                                        if(!request.getEmail().isEmpty()) {
                                                        foundedWorker.setEmail(request.getEmail());
                                                    }
                                                            if(!request.getPassword().isEmpty()) {
                                                                foundedWorker.setPassword(request.getPassword());
                                                            }
                                                     userRepository.save(foundedWorker);
                                        }

                                        return    UpdatedEmployeeDataResponse.builder()
                                                .workerId(foundedWorker.getId())
                                                .firstName(foundedWorker.getFirstName())
                                                .lastName(foundedWorker.getLastName())
                                                .email(foundedWorker.getEmail())
                                                .build();

                        }

                        public RelatedUserInCompanyResponse findEmployeeInCertainCompany(Integer workerId, Integer companyId, Authentication authentication) throws AccessDeniedException {

                            User user = ((User) authentication.getPrincipal());
                            if(!user.isAdmin() || !user.isBusinessOwner()){
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

    public PageResponse<RelatedUserInCompanyResponse> findAllEmployeesInCertainCompany(
            int page,
            int size,
            Integer companyId,
            Authentication authentication) {

        User user = ((User) authentication.getPrincipal());
        if (!user.isAdmin() && !user.getCompany().getId().equals(companyId)) {
            log.warn("Unauthorized access attempt by user: {}", user.getEmail());
            throw new AccessDeniedException("You do not have permission to view company employees");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAllEmployeesInCompany(pageable, companyId);

        List<RelatedUserInCompanyResponse> responses = users.getContent()
                .stream()
                .map(companyMapper::toCompanyWorkerResponse)
                .toList();

        return new PageResponse<>(
                responses,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast()
        );
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
    public void fireEmployee(Integer workerId, Authentication authentication) throws AccessDeniedException {
            User user = ((User) authentication.getPrincipal());
            if(!user.isAdmin() && !user.isBusinessOwner()){
                throw new AccessDeniedException("You do not have permission to update this company");
            }

            var foundedEmployee = userRepository.findById(workerId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

                     if (!foundedEmployee.getCompany().getId().equals(user.getCompany().getId())) {
                            throw new AccessDeniedException("Something went wrong");
                        }

                     var company = foundedEmployee.getCompany();
                     company.setWorkersQuantity(company.getWorkersQuantity() - 1);
                     companyRepository.save(company);

                     userRepository.deleteById(foundedEmployee.getId());
    }







            @Transactional(rollbackOn = Exception.class)
            public void deleteCompany(Integer companyId, Authentication authentication) throws AccessDeniedException {
            User user = ((User) authentication.getPrincipal());
            if(!user.isAdmin() && !user.isBusinessOwner()){
                throw new AccessDeniedException("You do not have permission to delete this company");
            }
            companyRepository.deleteById(companyId);
            }


    private int compareByRoles(User u1, User u2) {
        int priority1 = getRolePriority(u1);
        int priority2 = getRolePriority(u2);
        return Integer.compare(priority1, priority2);
    }

    private int getRolePriority(User user) {
        if (user.isAdmin()) return 1;
        if (user.isBusinessOwner()) return 2;
        if (user.isForeman()) return 3;
        return 4;
    }




        private Company updateCompanyCredentials(CompanyUpdatingRequest request, Company company){
            company.setCompanyName(request.getCompanyName());
            company.setCompanyAddress(request.getCompanyAddress());
            company.setCompanyPhone(request.getCompanyPhone());
            company.setCompanyEmail(request.getCompanyEmail());
            company.setWorkersQuantity(request.getWorkersQuantity());
            return companyRepository.save(company);
        }

}
