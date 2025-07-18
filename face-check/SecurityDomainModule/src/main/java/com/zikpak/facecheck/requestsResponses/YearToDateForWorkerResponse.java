package com.zikpak.facecheck.requestsResponses;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
public class YearToDateForWorkerResponse {

    private BigDecimal yearToDateNetPay;
    private BigDecimal YearToDateGrossPay;

    private BigDecimal yearToDateMedicare;
    private BigDecimal yearToDateSocialSecurityEmployee;
    private BigDecimal yearToDateFederalWithholding;
    private BigDecimal yearToDateNyStateWithholding;
    private BigDecimal yearToDateNyLocalWithholding;
    private BigDecimal yearToDateNyDisabilityWithholding;
    private BigDecimal yearToDateNyPaidFamilyLeave;

    public YearToDateForWorkerResponse(BigDecimal yearToDateNetPay, BigDecimal yearToDateGrossPay,
                                       BigDecimal yearToDateMedicare, BigDecimal yearToDateSocialSecurityEmployee,
                                       BigDecimal yearToDateFederalWithholding, BigDecimal yearToDateNyStateWithholding,
                                       BigDecimal yearToDateNyLocalWithholding, BigDecimal yearToDateNyDisabilityWithholding,
                                       BigDecimal yearToDateNyPaidFamilyLeave) {
        this.yearToDateNetPay = yearToDateNetPay;
        this.YearToDateGrossPay = yearToDateGrossPay;
        this.yearToDateMedicare = yearToDateMedicare;
        this.yearToDateSocialSecurityEmployee = yearToDateSocialSecurityEmployee;
        this.yearToDateFederalWithholding = yearToDateFederalWithholding;
        this.yearToDateNyStateWithholding = yearToDateNyStateWithholding;
        this.yearToDateNyLocalWithholding = yearToDateNyLocalWithholding;
        this.yearToDateNyDisabilityWithholding = yearToDateNyDisabilityWithholding;
        this.yearToDateNyPaidFamilyLeave = yearToDateNyPaidFamilyLeave;
    }

    // СПЕЦИАЛЬНЫЙ конструктор для JPQL - принимает Number и конвертирует в BigDecimal
    public YearToDateForWorkerResponse(Number yearToDateNetPay, Number yearToDateGrossPay,
                                       Number yearToDateMedicare, Number yearToDateSocialSecurityEmployee,
                                       Number yearToDateFederalWithholding, Number yearToDateNyStateWithholding,
                                       Number yearToDateNyLocalWithholding, Number yearToDateNyDisabilityWithholding,
                                       Number yearToDateNyPaidFamilyLeave) {
        this.yearToDateNetPay = toBigDecimal(yearToDateNetPay);
        this.YearToDateGrossPay = toBigDecimal(yearToDateGrossPay);
        this.yearToDateMedicare = toBigDecimal(yearToDateMedicare);
        this.yearToDateSocialSecurityEmployee = toBigDecimal(yearToDateSocialSecurityEmployee);
        this.yearToDateFederalWithholding = toBigDecimal(yearToDateFederalWithholding);
        this.yearToDateNyStateWithholding = toBigDecimal(yearToDateNyStateWithholding);
        this.yearToDateNyLocalWithholding = toBigDecimal(yearToDateNyLocalWithholding);
        this.yearToDateNyDisabilityWithholding = toBigDecimal(yearToDateNyDisabilityWithholding);
        this.yearToDateNyPaidFamilyLeave = toBigDecimal(yearToDateNyPaidFamilyLeave);
    }

    private BigDecimal toBigDecimal(Number number) {
        return number != null ? new BigDecimal(number.toString()) : BigDecimal.ZERO;
    }
}
