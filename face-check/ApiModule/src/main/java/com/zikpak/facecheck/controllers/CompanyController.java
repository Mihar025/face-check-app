package com.zikpak.facecheck.controllers;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.requestsResponses.company.CompanyResponse;
import com.zikpak.facecheck.services.company.*;
import com.zikpak.facecheck.taxesServices.services.PayStubService;
import com.zikpak.facecheck.requestsResponses.CompanyUpdatingRequest;
import com.zikpak.facecheck.requestsResponses.CompanyUpdatingResponse;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.company.finance.*;
import com.zikpak.facecheck.requestsResponses.worker.RelatedUserInCompanyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("company")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;


    @GetMapping("/{companyId}/billing-info")
    public ResponseEntity<CompanyStripeResponse> getBillingInfo(
            @PathVariable Integer companyId,
            Authentication auth
    ) {
        return ResponseEntity.ok(companyService.findCompanyStripe(companyId, auth));
    }

    @GetMapping(value = "/{companyId}/employees/count", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> count(@PathVariable Integer companyId, Authentication auth) {
        return ResponseEntity.ok(String.valueOf(companyService.findWorkersQuantityInCertainCompany(companyId, auth)));
    }


    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyUpdatingResponse> updateCompany(
            @Valid @RequestBody CompanyUpdatingRequest request,
            @PathVariable Integer companyId,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.updateCompany(request, companyId, authentication));
    }


    //working
    @PutMapping("/{companyId}/income")
    public ResponseEntity<CompanyIncomePerMonthResponse> setCompanyIncomePerMonth(
            @Valid @RequestBody CompanyIncomePerMonthRequest request,
            @PathVariable Integer companyId,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.setCompanyIncomePerMonth(request, companyId, authentication));
    }

    //working
    @GetMapping("/{companyId}/income")
    public ResponseEntity<CompanyIncomePerMonthResponse> findCompanyIncomePerMonth(
            @PathVariable Integer companyId,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.findCompanyIncomePerMonth(companyId, authentication));
    }

    @GetMapping("/employee-in-certain-company/{workerId}/{companyId}/")
    public ResponseEntity<RelatedUserInCompanyResponse> findCertainEmployeeInCompany(
            @PathVariable(name = "workerId") Integer workerId,
            @PathVariable(name = "companyId") Integer companyId,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.findEmployeeInCertainCompany(workerId, companyId, authentication));
    }


    //working
    @PostMapping("/calculate-taxes")
    public ResponseEntity<CompanyTaxCalculationResponse> calculateTaxes(
            @RequestBody CompanyTaxCalculationRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.countAllTaxesByCompanyIncome(request, authentication));
    }


    //working
    @GetMapping("/total-salaries")
    public ResponseEntity<BigDecimal> getTotalSalaries(
            Authentication authentication) {
        return ResponseEntity.ok(companyService.countSalariesInTotalForAllEmployeePerMonth(authentication));
    }

    @GetMapping("/get-company-id")
    public ResponseEntity<Integer> getCompanyId(
            Authentication authentication) {
        return ResponseEntity.ok(companyService.findCompanyId(authentication));
    }


    //working
    @GetMapping("/employee-rates")
    public ResponseEntity<List<EmployeeSalaryResponse>> getAllEmployeeRates(
            Authentication authentication) {
        return ResponseEntity.ok(companyService.findAllEmployeesBaseHourRate(authentication));
    }


    //working
    @GetMapping("/{companyId}/employees/{employeeId}/rate")
    public ResponseEntity<EmployeeSalaryResponse> getEmployeeRate(
            @PathVariable Integer companyId,
            @PathVariable Integer employeeId,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.findTheLatestEmployeeBaseHourRate(
                companyId,
                employeeId,
                authentication));
    }

    //working
    @PutMapping("/{companyId}/employees/{employeeId}/rate")
    public ResponseEntity<EmployeeSalaryResponse> updateEmployeeRate(
            @PathVariable Integer companyId,
            @PathVariable Integer employeeId,
            @Valid @RequestBody EmployeeRaiseHourRateRequest request,
            Authentication authentication) {

        log.info("Rate update request - Company: {}, Employee: {}, New Rate: {}",
                companyId, employeeId, request.getBaseHourlyRate());

        try {
            EmployeeSalaryResponse response = companyService.changeEmployeeBaseHourRate(
                    companyId, employeeId, authentication, request);
            log.info("Rate update successful for employee {}", employeeId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Rate update failed for employee {} - Error: {}", employeeId, e.getMessage(), e);
            throw e;
        }
    }

    //working
    @GetMapping("/{companyId}/employees")
    public ResponseEntity<PageResponse<RelatedUserInCompanyResponse>> getAllEmployees(
            @PathVariable Integer companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.findAllEmployeesInCertainCompany(page, size, companyId, authentication));
    }



    @GetMapping("/find-all-companies")
    public ResponseEntity<PageResponse<CompanyResponse>> findAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.findAllCompanies(page, size, authentication));
    }



    @GetMapping("/find-all-employees")
    public ResponseEntity<PageResponse<RelatedUserInCompanyResponse>> findAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        log.info("=== findAllEmployees endpoint called ==="); // ← Добавьте это
        log.info("Authentication: {}", authentication);
        log.info("Principal: {}", authentication.getPrincipal());

        return ResponseEntity.ok(companyService.findAllEmployees(page, size, authentication));
    }




    //working
    @GetMapping("/{companyId}/employees/users")
    public ResponseEntity<PageResponse<RelatedUserInCompanyResponse>> getUserEmployees(
            @PathVariable Integer companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.findAllEmployeesWhoseRoleAreUser(page, size, companyId, authentication));
    }
    //working
    @GetMapping("/{companyId}/employees/foreman")
    public ResponseEntity<PageResponse<RelatedUserInCompanyResponse>> getForemanEmployees(
            @PathVariable Integer companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.findAllEmployeesWhoseRoleAreForeman(page, size, companyId, authentication));
    }

    //working
    @GetMapping("/{companyId}/employees/admins")
    public ResponseEntity<PageResponse<RelatedUserInCompanyResponse>> getAdminEmployees(
            @PathVariable Integer companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.findAllEmployeesWhoseRoleAreAdmin(page, size, companyId, authentication));
    }


    //working
    @PatchMapping("/employees/{employeeId}/promote/foreman")
    public ResponseEntity<Void> promoteToForeman(
            @PathVariable Integer employeeId,
            Authentication authentication) {
        companyService.raiseToForemanRoleInCompany(employeeId, authentication);
        return ResponseEntity.ok().build();
    }
    //working
    @PatchMapping("/employees/{employeeId}/promote/admin")
    public ResponseEntity<Void> promoteToAdmin(
            @PathVariable Integer employeeId,
            Authentication authentication) {
        companyService.raiseToAdminRoleInCompany(employeeId, authentication);
        return ResponseEntity.ok().build();
    }

    //working
    @DeleteMapping("/employees/{employeeId}/fire")
    public ResponseEntity<Void> fireEmployee(
            @PathVariable Integer employeeId,
            Authentication authentication) {
        companyService.fireEmployee(employeeId, authentication);
        return ResponseEntity.ok().build();
    }

    //working
    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(
            @PathVariable Integer companyId,
            Authentication authentication) {
        companyService.deleteCompany(companyId, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/demote/{workerId}/admin-to-foreman")
    public ResponseEntity<Void> demoteFromAdminToForeman(
            @PathVariable Integer workerId,
            Authentication authentication) {
        companyService.demoteFromAdminToForeman(workerId, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/demote/{workerId}/foreman-to-user")
    public ResponseEntity<Void> demoteFromForemanToUser(
            @PathVariable Integer workerId,
            Authentication authentication) {
        companyService.demoteFromForemanToUser(workerId, authentication);
        return ResponseEntity.ok().build();
    }



    @GetMapping(value = "/name", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getCompanyName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(companyService.companyName(authentication));
    }

    @GetMapping(value = "/address", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getCompanyAddress() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(companyService.companyAddress(authentication));
    }

    @GetMapping(value = "/phone", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getCompanyPhone() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(companyService.companyPhone(authentication));
    }

    @GetMapping(value = "/email", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getCompanyEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(companyService.companyEmail(authentication));
    }

    @PutMapping("/update-name")
    public ResponseEntity<UpdateCompanyNameResponse> updateCompanyName(
            @Valid @RequestBody UpdateCompanyNameRequest name,
            Authentication authentication) {
        companyService.updateCompanyName(name, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-address")
    public ResponseEntity<UpdateCompanyAddressResponse> updateCompanyAddress(
            @Valid @RequestBody UpdateCompanyAddressRequest address,
            Authentication authentication) {
        companyService.updateCompanyAddress(address, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-phone")
    public ResponseEntity<UpdateCompanyPhoneNumberResponse> updateCompanyPhone(
            @Valid @RequestBody UpdateCompanyPhoneNumberRequest phoneNumber,
            Authentication authentication) {
        companyService.updateCompanyPhoneNumber(phoneNumber, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-email")
    public ResponseEntity<UpdateCompanyEmailResponse> updateCompanyEmail(
            @Valid @RequestBody UpdateCompanyEmailRequest email,
            Authentication authentication) {
        companyService.updateCompanyEmail(email, authentication);
        return ResponseEntity.ok().build();
    }

}