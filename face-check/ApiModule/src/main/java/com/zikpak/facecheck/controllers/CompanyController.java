package com.zikpak.facecheck.controllers;


import com.zikpak.facecheck.helperServices.PayStubService;
import com.zikpak.facecheck.requestsResponses.CompanyUpdatingRequest;
import com.zikpak.facecheck.requestsResponses.CompanyUpdatingResponse;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.company.finance.*;
import com.zikpak.facecheck.requestsResponses.worker.RelatedUserInCompanyResponse;
import com.zikpak.facecheck.services.company.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final PayStubService payStubService;


    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyUpdatingResponse> updateCompany(
            @RequestBody CompanyUpdatingRequest request,
            @PathVariable Integer companyId,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.updateCompany(request, companyId, authentication));
    }


    //working
    @PutMapping("/{companyId}/income")
    public ResponseEntity<CompanyIncomePerMonthResponse> setCompanyIncomePerMonth(
            @RequestBody CompanyIncomePerMonthRequest request,
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
        return ResponseEntity.ok(companyService.findTheLatestEmployeeBaseHourRate(companyId, employeeId, authentication));
    }

    //working
    @PutMapping("/{companyId}/employees/{employeeId}/rate")
    public ResponseEntity<EmployeeSalaryResponse> updateEmployeeRate(
            @PathVariable Integer companyId,
            @PathVariable Integer employeeId,
            @RequestBody EmployeeRaiseHourRateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(companyService.changeEmployeeBaseHourRate(companyId, employeeId, authentication, request));
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



    @GetMapping("/name")
    public ResponseEntity<String> getCompanyName(Authentication authentication) {
        return ResponseEntity.ok(companyService.companyName(authentication));
    }

    @GetMapping("/address")
    public ResponseEntity<String> getCompanyAddress(Authentication authentication) {
        return ResponseEntity.ok(companyService.companyAddress(authentication));
    }

    @GetMapping("/phone")
    public ResponseEntity<String> getCompanyPhone(Authentication authentication) {
        return ResponseEntity.ok(companyService.companyPhone(authentication));
    }

    @GetMapping("/email")
    public ResponseEntity<String> getCompanyEmail(Authentication authentication) {
        return ResponseEntity.ok(companyService.companyEmail(authentication));
    }

    @PutMapping("/update-name")
    public ResponseEntity<Void> updateCompanyName(
            @RequestBody String name,
            Authentication authentication) {
        companyService.updateCompanyName(name, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-address")
    public ResponseEntity<Void> updateCompanyAddress(
            @RequestBody String address,
            Authentication authentication) {
        companyService.updateCompanyAddress(address, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-phone")
    public ResponseEntity<Void> updateCompanyPhone(
            @RequestBody String phoneNumber,
            Authentication authentication) {
        companyService.updateCompanyPhoneNumber(phoneNumber, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-email")
    public ResponseEntity<Void> updateCompanyEmail(
            @RequestBody String email,
            Authentication authentication) {
        companyService.updateCompanyEmail(email, authentication);
        return ResponseEntity.ok().build();
    }




    @GetMapping("/generate/{payrollId}")
    public ResponseEntity<byte[]> generatePayStub(@PathVariable Integer payrollId) {
        byte[] pdf = payStubService.generatePayStubPdf(payrollId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pay_stub_" + payrollId + ".pdf")
                .body(pdf);
    }








}