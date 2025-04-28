package com.zikpak.facecheck.helperServices;

import com.zikpak.facecheck.domain.UserFinanceNetGrossTaxCalculator;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.entity.workingPayRoll.TaxRates;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.requestsResponses.finance.WorkerYearlySummaryDto;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerPayRollService implements UserFinanceNetGrossTaxCalculator {


    private final WorkerPayrollRepository workerPayrollRepository;
    private final UserRepository userRepository;
    private final W2PdfGeneratorService w2PdfGeneratorService;
    private final AmazonS3Service amazonS3Service;


    private BigDecimal getBaseRateDirectFromPayroll(User user) {
        var latestPayroll = getLatestPayroll(user);
        log.info("Getting base rate directly from payroll for user {}: {}",
                user.getEmail(), latestPayroll.getBaseHourlyRate());
        return latestPayroll.getBaseHourlyRate();
    }

    private BigDecimal getGrossPayDirectFromPayroll(User user) {
        var latestPayroll = getLatestPayroll(user);
        log.info("Getting gross pay directly from payroll for user {}: {}",
                user.getEmail(), latestPayroll.getGrossPay());
        return latestPayroll.getGrossPay() != null ? latestPayroll.getGrossPay() : BigDecimal.ZERO;
    }

    private BigDecimal getNetPayDirectFromPayroll(User user) {
        var latestPayroll = getLatestPayroll(user);
        log.info("Getting net pay directly from payroll for user {}: {}",
                user.getEmail(), latestPayroll.getNetPay());
        return latestPayroll.getNetPay() != null ? latestPayroll.getNetPay() : BigDecimal.ZERO;
    }

    private BigDecimal getTaxesDirectFromPayroll(User user) {
        var latestPayroll = getLatestPayroll(user);
        log.info("Getting total deductions directly from payroll for user {}: {}",
                user.getEmail(), latestPayroll.getTotalDeductions());
        return latestPayroll.getTotalDeductions() != null ? latestPayroll.getTotalDeductions() : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal findWorkerBaseHourRate(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        return getBaseRateDirectFromPayroll(foundedUser);
    }


    @Override
    public BigDecimal countSalaryPerWeekGross(Authentication authentication) {
        var foundedUser = findUserAndPayroll(authentication);
        return getGrossPayDirectFromPayroll(foundedUser);
    }

    @Override
    public BigDecimal countSalaryPerWeekNet(Authentication authentication) {
        var foundedUser = findUserAndPayroll(authentication);
        return getNetPayDirectFromPayroll(foundedUser);
    }

    @Override
    public BigDecimal findWorkerTotalPayedTaxesAmountForWeek(Authentication authentication) {
        var foundedUser = findUserAndPayroll(authentication);
        return getTaxesDirectFromPayroll(foundedUser);
    }

    @Override
    public double findWorkerWorkedHoursTotalPerWeek(Authentication authentication) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        return latestPayroll.getTotalHours();
    }

    @Override
    public BigDecimal countSalaryPerMontGross(Authentication authentication) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var monthlyAttendance = getMonthlyAttendance(foundedUser);
        double[] hours = calculateHour(monthlyAttendance);
        return calculateGrossPay(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal countSalaryPerYearGross(Authentication authentication) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var yearlyAttendance = getYearlyAttendance(foundedUser);
        double[] hours = calculateHour(yearlyAttendance);
        return calculateGrossPay(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal countSalaryPerMontNet(Authentication authentication) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var monthlyAttendance = getMonthlyAttendance(foundedUser);
        double[] hours = calculateHour(monthlyAttendance);
        return calculateNetPay(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal countSalaryPerYearNet(Authentication authentication) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var yearlyAttendance = getYearlyAttendance(foundedUser);
        double[] hours = calculateHour(yearlyAttendance);
        return calculateNetPay(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal findWorkerTotalPayedTaxesAmountForMonth(Authentication authentication) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var monthlyAttendance = getMonthlyAttendance(foundedUser);
        double[] hours = calculateHour(monthlyAttendance);
        return calculateTotalTaxes(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal findWorkerTotalPayedTaxesAmountForYear(Authentication authentication) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var yearlyAttendance = getYearlyAttendance(foundedUser);
        double[] hours = calculateHour(yearlyAttendance);
        return calculateTotalTaxes(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal findWorkerSalaryForSpecialWeekGross(Authentication authentication, int weekAgo) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var specialWeekAttendance = getAttendanceForSpecialWeek(foundedUser, weekAgo);
        double[] hours = calculateHour(specialWeekAttendance);
        return calculateGrossPay(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal findWorkerSalaryForSpecialMonthGross(Authentication authentication, YearMonth yearMonth) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var specialMonth = getAttendanceForSpecialMonth(foundedUser, yearMonth);
        double[] hours = calculateHour(specialMonth);
        return calculateGrossPay(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal findWorkerSalaryForSpecialYearGross(Authentication authentication, int years) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var specialYear = getAttendanceForSpecialYear(foundedUser, years);
        double[] hours = calculateHour(specialYear);
        return calculateGrossPay(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal findWorkerSalaryForSpecialWeekNet(Authentication authentication, int weekAgo) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var weeklyAttendance = getAttendanceForSpecialWeek(foundedUser, weekAgo);
        double[] hours = calculateHour(weeklyAttendance);
        return calculateNetPay(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal findWorkerSalaryForSpecialMonthNet(Authentication authentication, YearMonth yearMonth) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var specialMonth = getAttendanceForSpecialMonth(foundedUser, yearMonth);
        double[] hours = calculateHour(specialMonth);
        return calculateNetPay(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal findWorkerSalaryForSpecialYearNet(Authentication authentication, int years) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var specialYear = getAttendanceForSpecialYear(foundedUser, years);
        double[] hours = calculateHour(specialYear);
        return calculateNetPay(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal findWorkerTotalPayedTaxesAmountForSpecialWeek(Authentication authentication, int weekAgo) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var weekAttendance = getAttendanceForSpecialWeek(foundedUser, weekAgo);
        double[] hours = calculateHour(weekAttendance);
        return calculateTotalTaxes(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal findWorkerTotalPayedTaxesAmountForSpecialMonth(Authentication authentication, YearMonth yearMonth) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var specialMonth = getAttendanceForSpecialMonth(foundedUser, yearMonth);
        double[] hours = calculateHour(specialMonth);
        return calculateTotalTaxes(hours[0], hours[1], latestPayroll);
    }

    @Override
    public BigDecimal findWorkerTotalPayedTaxesAmountForSpecialYear(Authentication authentication, int years) {
        var foundedUser = findUserAndPayroll(authentication);
        var latestPayroll = getLatestPayroll(foundedUser);
        var specialYear = getAttendanceForSpecialYear(foundedUser, years);
        double[] hours = calculateHour(specialYear);
        return calculateTotalTaxes(hours[0], hours[1], latestPayroll);
    }

    private BigDecimal calculateNetPay(double regularHours, double overtimeHours, WorkerPayroll payroll) {
        var grossPay = calculateGrossPay(regularHours, overtimeHours, payroll);
        var totalTaxes = calculateTotalTaxes(regularHours, overtimeHours, payroll);
        return grossPay.subtract(totalTaxes);
    }

    private BigDecimal calculateTotalTaxes(double regularHours, double overtimeHours, WorkerPayroll payroll) {
        var grossPay = calculateGrossPay(regularHours, overtimeHours, payroll);
        return Arrays.stream(TaxRates.values())
                .map(tax -> grossPay.multiply(tax.getTaxRate()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private User findUserAndPayroll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        var foundedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User with id " + user.getId() + " not found"));
        if (foundedUser.getPayrolls() == null || foundedUser.getPayrolls().isEmpty()) {
            throw new RuntimeException("Payrolls is empty");
        }
        return foundedUser;
    }

    private WorkerPayroll getLatestPayroll(User user) {
        return user.getPayrolls().stream()
                .max(Comparator.comparing(WorkerPayroll::getPeriodEnd))
                .orElseThrow(() -> new RuntimeException("Payrolls is empty"));
    }

    private List<WorkerAttendance> getWeeklyAttendance(User user) {
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(DayOfWeek.SUNDAY);
        LocalDate weekEnd = now.with(DayOfWeek.SATURDAY);

        return user.getAttendances().stream()
                .filter(attendance -> {
                    LocalDate attendanceDate = attendance.getCheckInTime().toLocalDate();
                    return !attendanceDate.isBefore(weekStart) && !attendanceDate.isAfter(weekEnd);
                })
                .toList();
    }

    private List<WorkerAttendance> getMonthlyAttendance(User user) {
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());

        var attendances = user.getAttendances().stream()
                .filter(attendance -> {
                    LocalDate attendanceDate = attendance.getCheckInTime().toLocalDate();
                    return !attendanceDate.isBefore(monthStart) && !attendanceDate.isAfter(monthEnd);
                })
                .toList();
        if (attendances.isEmpty()) {
            throw new RuntimeException("Attendances is empty");
        }
        return attendances;
    }

    private List<WorkerAttendance> getYearlyAttendance(User user) {
        LocalDate now = LocalDate.now();
        LocalDate yearStart = now.withDayOfYear(1);
        LocalDate yearEnd = now.withDayOfYear(now.lengthOfYear());

        var attendances = user.getAttendances().stream()
                .filter(attendance -> {
                    LocalDate attendanceDate = attendance.getCheckInTime().toLocalDate();
                    return !attendanceDate.isBefore(yearStart) && !attendanceDate.isAfter(yearEnd);
                })
                .toList();

        if (attendances.isEmpty()) {
            throw new RuntimeException("Attendances is empty");
        }
        return attendances;
    }

    private List<WorkerAttendance> getAttendanceForSpecialWeek(User user, int weekAgo) {
        if (weekAgo < 0) {
            throw new RuntimeException("Week ago cannot be negative");
        }
        LocalDate now = LocalDate.now();
        LocalDate targetWeekStart = now.minusWeeks(weekAgo).with(DayOfWeek.SUNDAY);
        LocalDate targetWeekEnd = targetWeekStart.with(DayOfWeek.SATURDAY);

        var attendances = user.getAttendances().stream()
                .filter(attendance -> {
                    LocalDate attendanceDate = attendance.getCheckInTime().toLocalDate();
                    return !attendanceDate.isBefore(targetWeekStart) && !attendanceDate.isAfter(targetWeekEnd);
                })
                .toList();

        if (attendances.isEmpty()) {
            throw new RuntimeException("Attendances is empty");
        }
        return attendances;
    }

    private List<WorkerAttendance> getAttendanceForSpecialMonth(User user, YearMonth yearMonth) {
        if (yearMonth == null) {
            throw new RuntimeException("Month cannot be null");
        }
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();

        var attendances = user.getAttendances().stream()
                .filter(attendance -> {
                    LocalDate attendanceDate = attendance.getCheckInTime().toLocalDate();
                    return !attendanceDate.isBefore(monthStart) && !attendanceDate.isAfter(monthEnd);
                })
                .toList();
        if (attendances.isEmpty()) {
            throw new RuntimeException("Attendances is empty");
        }
        return attendances;
    }

    private List<WorkerAttendance> getAttendanceForSpecialYear(User user, int year) {
        if (year < 2024 || year > 2026) {
            throw new RuntimeException("Year must be between 2024 and 2026");
        }
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);

        var attendances = user.getAttendances().stream()
                .filter(attendance -> {
                    LocalDate attendanceDate = attendance.getCheckInTime().toLocalDate();
                    return !attendanceDate.isBefore(yearStart) && !attendanceDate.isAfter(yearEnd);
                })
                .toList();

        if (attendances.isEmpty()) {
            throw new RuntimeException("Attendances is empty");
        }
        return attendances;
    }

    private double[] calculateHour(List<WorkerAttendance> attendances) {
        double regularHours = 0.0;
        double overtimeHours = 0.0;

        for (WorkerAttendance attendance : attendances) {
            regularHours += attendance.getHoursWorked() != null ? attendance.getHoursWorked() : 0.0;
            overtimeHours += attendance.getOvertimeHours() != null ? attendance.getOvertimeHours() : 0.0;
        }

        return new double[]{regularHours, overtimeHours};
    }

    private BigDecimal calculateGrossPay(double regularHours, double overtimeHours, WorkerPayroll payroll) {
        var regularPay = payroll.getBaseHourlyRate()
                .multiply(BigDecimal.valueOf(regularHours));

        var overtimePay = payroll.getOvertimeRate()
                .multiply(BigDecimal.valueOf(overtimeHours));

        return regularPay.add(overtimePay);
    }


    /*
                NEW METHODS!!!!!!
     */

    public WorkerYearlySummaryDto calculateWorkerYearlyTotals(Integer workerId, int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        var payrolls = workerPayrollRepository.findAllByWorkerIdAndYear(workerId, startOfYear, endOfYear);

        BigDecimal grossPayTotal = BigDecimal.ZERO;
        BigDecimal netPayTotal = BigDecimal.ZERO;
        BigDecimal federalWithholdingTotal = BigDecimal.ZERO;
        BigDecimal socialSecurityEmployeeTotal = BigDecimal.ZERO;
        BigDecimal medicareTotal = BigDecimal.ZERO;
        BigDecimal nyStateWithholdingTotal = BigDecimal.ZERO;
        BigDecimal nyLocalWithholdingTotal = BigDecimal.ZERO;
        BigDecimal nyDisabilityWithholdingTotal = BigDecimal.ZERO;
        BigDecimal nyPaidFamilyLeaveTotal = BigDecimal.ZERO;
        BigDecimal totalAllTaxes = BigDecimal.ZERO;

        for (WorkerPayroll payroll : payrolls) {
            grossPayTotal = grossPayTotal.add(safe(payroll.getGrossPay()));
            netPayTotal = netPayTotal.add(safe(payroll.getNetPay()));
            federalWithholdingTotal = federalWithholdingTotal.add(safe(payroll.getFederalWithholding()));
            socialSecurityEmployeeTotal = socialSecurityEmployeeTotal.add(safe(payroll.getSocialSecurityEmployee()));
            medicareTotal = medicareTotal.add(safe(payroll.getMedicare()));
            nyStateWithholdingTotal = nyStateWithholdingTotal.add(safe(payroll.getNyStateWithholding()));
            nyLocalWithholdingTotal = nyLocalWithholdingTotal.add(safe(payroll.getNyLocalWithholding()));
            nyDisabilityWithholdingTotal = nyDisabilityWithholdingTotal.add(safe(payroll.getNyDisabilityWithholding()));
            nyPaidFamilyLeaveTotal = nyPaidFamilyLeaveTotal.add(safe(payroll.getNyPaidFamilyLeave()));

            totalAllTaxes = totalAllTaxes
                    .add(safe(payroll.getFederalWithholding()))
                    .add(safe(payroll.getSocialSecurityEmployee()))
                    .add(safe(payroll.getMedicare()))
                    .add(safe(payroll.getNyStateWithholding()))
                    .add(safe(payroll.getNyLocalWithholding()))
                    .add(safe(payroll.getNyDisabilityWithholding()))
                    .add(safe(payroll.getNyPaidFamilyLeave()));
        }

        return WorkerYearlySummaryDto.builder()
                .grossPayTotal(grossPayTotal)
                .netPayTotal(netPayTotal)
                .federalWithholdingTotal(federalWithholdingTotal)
                .socialSecurityEmployeeTotal(socialSecurityEmployeeTotal)
                .medicareTotal(medicareTotal)
                .nyStateWithholdingTotal(nyStateWithholdingTotal)
                .nyLocalWithholdingTotal(nyLocalWithholdingTotal)
                .nyDisabilityWithholdingTotal(nyDisabilityWithholdingTotal)
                .nyPaidFamilyLeaveTotal(nyPaidFamilyLeaveTotal)
                .totalAllTaxes(totalAllTaxes)
                .build();
    }

    public byte[] generatePDF(Integer workerId, int year) {
        var worker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        var workerSummary = calculateWorkerYearlyTotals(workerId, year);
        var company = worker.getCompany();
        if (company == null) {
            throw new RuntimeException("Worker has no associated company.");
        }

        byte[] pdfContent = w2PdfGeneratorService.generateW2Pdf(
                worker,
                workerSummary,
                worker.getSSN_WORKER(),
                company.getCompanyName(),
                company.getEmployerEIN(),
                company.getCompanyAddress(),
                year
        );

        String fileName = "w2-statements/" + year + "/" + "w2_" + workerId + ".pdf";
        amazonS3Service.uploadPdfToS3(pdfContent, fileName);
        return pdfContent;
    }



    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}



