package com.zikpak.facecheck.taxesServices.calculators;

import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.W4.FilingStatus;
import com.zikpak.facecheck.entity.W4.TaxRates;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.finance.PayStubResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class FinanceCalculator {


    private final UserRepository userRepository;

    public BigDecimal calculateSocialSecurity(User user, BigDecimal grossPay, BigDecimal ytdSocialSecurityWages) {
        if (grossPay == null) return BigDecimal.ZERO;

        BigDecimal limit = TaxRates.SOCIAL_SECURITY_WAGE_LIMIT;
        BigDecimal remainingLimit = limit.subtract(ytdSocialSecurityWages);

        if (remainingLimit.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;

        BigDecimal taxableWages = grossPay.min(remainingLimit);
        return TaxRates.round(taxableWages.multiply(TaxRates.SOCIAL_SECURITY_RATE));
    }


    public BigDecimal calculateMedicare(User user, BigDecimal grossPay) {
        if (grossPay == null) return BigDecimal.ZERO;

        return TaxRates.round(grossPay.multiply(TaxRates.MEDICARE_RATE));
    }

    public BigDecimal calculateNYDisability() {
        return TaxRates.round(TaxRates.NY_DISABILITY_WEEKLY_MAX);
    }

    public BigDecimal calculateNYPaidFamilyLeave(BigDecimal grossPay, BigDecimal yearToDatePFL) {
        if (grossPay == null || yearToDatePFL == null) return BigDecimal.ZERO;

        BigDecimal thisPeriod = TaxRates.round(grossPay.multiply(TaxRates.NY_PFL_RATE));

        BigDecimal remaining = TaxRates.NY_PFL_ANNUAL_MAX.subtract(yearToDatePFL);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return thisPeriod.min(remaining);
    }




    public BigDecimal calculateFederalTax(User user, BigDecimal grossPay) {
        if (grossPay == null) return BigDecimal.ZERO;

        int payPeriods = switch (user.getPayFrequency()) {
            case WEEKLY    -> 52;
            case BIWEEKLY  -> 26;
            case MONTHLY   -> 12;
        };

        BigDecimal annualGross = grossPay.multiply(BigDecimal.valueOf(payPeriods));


        BigDecimal standardDeduction = switch (user.getFilingStatus()) {
            case SINGLE, MARRIED_FILLING_SEPARATELY ->
                    TaxRates.FEDERAL_STANDARD_DEDUCTION_SINGLE;
            case MARRIED_FILLING_JOINTLY ->
                    TaxRates.FEDERAL_STANDARD_DEDUCTION_MARRIED;
            case HEAD_OF_HOUSEHOLD ->
                    TaxRates.FEDERAL_STANDARD_DEDUCTION_SINGLE.add(new BigDecimal("5000"));
        };

        // 2. Детские кредиты
        BigDecimal dependentsDeduction = TaxRates.FEDERAL_CHILD_TAX_CREDIT
                .multiply(BigDecimal.valueOf(user.getDependents()));

        // 3. Налогооблагаемый доход
        BigDecimal taxableIncome = annualGross
                .subtract(standardDeduction)
                .subtract(dependentsDeduction)
                .max(BigDecimal.ZERO);

        // 4. Налог по брекетам
        BigDecimal annualTax = calculateFederalTaxForIncome(taxableIncome, user.getFilingStatus());

        // 5. На выплату
        BigDecimal taxPerPayPeriod = annualTax
                .divide(BigDecimal.valueOf(payPeriods), 2, RoundingMode.HALF_UP);

        // 6. Extra withholding
        return TaxRates.round(taxPerPayPeriod.add(user.getExtraWithHoldings()));
    }


    private BigDecimal calculateFederalTaxForIncome(BigDecimal income, FilingStatus status) {
        BigDecimal tax = BigDecimal.ZERO;

        switch (status) {
            case SINGLE, MARRIED_FILLING_SEPARATELY -> {
                // === Single brackets ===
                if (income.compareTo(new BigDecimal("11925")) <= 0) {
                    tax = income.multiply(new BigDecimal("0.10"));
                } else if (income.compareTo(new BigDecimal("48475")) <= 0) {
                    tax = new BigDecimal("1192.50")
                            .add(income.subtract(new BigDecimal("11925"))
                                    .multiply(new BigDecimal("0.12")));
                } else if (income.compareTo(new BigDecimal("103350")) <= 0) {
                    tax = new BigDecimal("5475.00")
                            .add(income.subtract(new BigDecimal("48475"))
                                    .multiply(new BigDecimal("0.22")));
                } else if (income.compareTo(new BigDecimal("197300")) <= 0) {
                    tax = new BigDecimal("17315.00")
                            .add(income.subtract(new BigDecimal("103350"))
                                    .multiply(new BigDecimal("0.24")));
                } else if (income.compareTo(new BigDecimal("250525")) <= 0) {
                    tax = new BigDecimal("39105.00")
                            .add(income.subtract(new BigDecimal("197300"))
                                    .multiply(new BigDecimal("0.32")));
                } else if (income.compareTo(new BigDecimal("626350")) <= 0) {
                    tax = new BigDecimal("57063.00")
                            .add(income.subtract(new BigDecimal("250525"))
                                    .multiply(new BigDecimal("0.35")));
                } else {
                    tax = new BigDecimal("174253.00")
                            .add(income.subtract(new BigDecimal("626350"))
                                    .multiply(new BigDecimal("0.37")));
                }
            }
            case MARRIED_FILLING_JOINTLY -> {
                // === Married filing jointly brackets ===
                if (income.compareTo(new BigDecimal("23850")) <= 0) {
                    tax = income.multiply(new BigDecimal("0.10"));
                } else if (income.compareTo(new BigDecimal("96950")) <= 0) {
                    tax = new BigDecimal("2385.00")
                            .add(income.subtract(new BigDecimal("23850"))
                                    .multiply(new BigDecimal("0.12")));
                } else if (income.compareTo(new BigDecimal("206700")) <= 0) {
                    tax = new BigDecimal("11157.00")
                            .add(income.subtract(new BigDecimal("96950"))
                                    .multiply(new BigDecimal("0.22")));
                } else if (income.compareTo(new BigDecimal("394600")) <= 0) {
                    tax = new BigDecimal("35957.00")
                            .add(income.subtract(new BigDecimal("206700"))
                                    .multiply(new BigDecimal("0.24")));
                } else if (income.compareTo(new BigDecimal("501050")) <= 0) {
                    tax = new BigDecimal("84397.00")
                            .add(income.subtract(new BigDecimal("394600"))
                                    .multiply(new BigDecimal("0.32")));
                } else if (income.compareTo(new BigDecimal("751600")) <= 0) {
                    tax = new BigDecimal("127534.00")
                            .add(income.subtract(new BigDecimal("501050"))
                                    .multiply(new BigDecimal("0.35")));
                } else {
                    tax = new BigDecimal("216384.50")
                            .add(income.subtract(new BigDecimal("751600"))
                                    .multiply(new BigDecimal("0.37")));
                }
            }
            case HEAD_OF_HOUSEHOLD -> {
                // === Head of household brackets ===
                if (income.compareTo(new BigDecimal("17000")) <= 0) {
                    tax = income.multiply(new BigDecimal("0.10"));
                } else if (income.compareTo(new BigDecimal("59800")) <= 0) {
                    tax = new BigDecimal("1700.00")
                            .add(income.subtract(new BigDecimal("17000"))
                                    .multiply(new BigDecimal("0.12")));
                } else if (income.compareTo(new BigDecimal("100450")) <= 0) {
                    tax = new BigDecimal("7036.00")
                            .add(income.subtract(new BigDecimal("59800"))
                                    .multiply(new BigDecimal("0.22")));
                } else if (income.compareTo(new BigDecimal("190200")) <= 0) {
                    tax = new BigDecimal("16478.00")
                            .add(income.subtract(new BigDecimal("100450"))
                                    .multiply(new BigDecimal("0.24")));
                } else if (income.compareTo(new BigDecimal("239000")) <= 0) {
                    tax = new BigDecimal("38666.00")
                            .add(income.subtract(new BigDecimal("190200"))
                                    .multiply(new BigDecimal("0.32")));
                } else if (income.compareTo(new BigDecimal("598900")) <= 0) {
                    tax = new BigDecimal("54126.00")
                            .add(income.subtract(new BigDecimal("239000"))
                                    .multiply(new BigDecimal("0.35")));
                } else {
                    tax = new BigDecimal("180529.00")
                            .add(income.subtract(new BigDecimal("598900"))
                                    .multiply(new BigDecimal("0.37")));
                }
            }
        }

        return TaxRates.round(tax);
    }



    public BigDecimal calculateNYStateTax(User user, BigDecimal grossPay) {
        if (grossPay == null) {
            return BigDecimal.ZERO;
        }

        // 1. Определяем число выплат в году
        int payPeriods = switch (user.getPayFrequency()) {
            case WEEKLY   -> 52;
            case BIWEEKLY -> 26;
            case MONTHLY  -> 12;
        };

        // 2. Годовой доход
        BigDecimal annualGross = grossPay.multiply(BigDecimal.valueOf(payPeriods));
        BigDecimal tax = BigDecimal.ZERO;

        // 3. Будем заполнять эти массивы в зависимости от FilingStatus
        BigDecimal[] brackets;
        BigDecimal[] rates;
        BigDecimal[] baseTax;

        switch (user.getFilingStatus()) {
            // Single И Married Filing Separately используют одну и ту же таблицу
            case SINGLE, MARRIED_FILLING_SEPARATELY -> {
                brackets = new BigDecimal[]{
                        new BigDecimal("8500"),
                        new BigDecimal("11700"),
                        new BigDecimal("13900"),
                        new BigDecimal("21400"),
                        new BigDecimal("80650"),
                        new BigDecimal("215400"),
                        new BigDecimal("1077550"),
                        new BigDecimal("5000000")
                };
                rates = new BigDecimal[]{
                        new BigDecimal("0.04"),
                        new BigDecimal("0.045"),
                        new BigDecimal("0.0525"),
                        new BigDecimal("0.0585"),
                        new BigDecimal("0.0625"),
                        new BigDecimal("0.0685"),
                        new BigDecimal("0.0965"),
                        new BigDecimal("0.1030"),
                        new BigDecimal("0.1090")
                };
                baseTax = new BigDecimal[]{
                        BigDecimal.ZERO,
                        new BigDecimal("340.00"),
                        new BigDecimal("484.00"),
                        new BigDecimal("598.00"),
                        new BigDecimal("1046.00"),
                        new BigDecimal("4468.00"),
                        new BigDecimal("14660.00"),
                        new BigDecimal("87585.00"),
                        new BigDecimal("476585.00")
                };
            }

            // Совместная подача (Married Filing Jointly) — другая таблица
            case MARRIED_FILLING_JOINTLY -> {
                brackets = new BigDecimal[]{
                        new BigDecimal("17150"),
                        new BigDecimal("23600"),
                        new BigDecimal("27900"),
                        new BigDecimal("43000"),
                        new BigDecimal("161550"),
                        new BigDecimal("323200"),
                        new BigDecimal("2155350"),
                        new BigDecimal("5000000")
                };
                rates = new BigDecimal[]{
                        new BigDecimal("0.04"),
                        new BigDecimal("0.045"),
                        new BigDecimal("0.0525"),
                        new BigDecimal("0.0585"),
                        new BigDecimal("0.0625"),
                        new BigDecimal("0.0685"),
                        new BigDecimal("0.0965"),
                        new BigDecimal("0.1030"),
                        new BigDecimal("0.1090")
                };
                baseTax = new BigDecimal[]{
                        BigDecimal.ZERO,
                        new BigDecimal("686.00"),
                        new BigDecimal("976.00"),
                        new BigDecimal("1302.00"),
                        new BigDecimal("2391.00"),
                        new BigDecimal("10717.00"),
                        new BigDecimal("35392.00"),
                        new BigDecimal("211981.00"),
                        new BigDecimal("725931.00")
                };
            }

            // Head of Household — своя таблица
            case HEAD_OF_HOUSEHOLD -> {
                brackets = new BigDecimal[]{
                        new BigDecimal("12800"),
                        new BigDecimal("17650"),
                        new BigDecimal("20900"),
                        new BigDecimal("32200"),
                        new BigDecimal("107650"),
                        new BigDecimal("269300"),
                        new BigDecimal("1616450"),
                        new BigDecimal("5000000")
                };
                rates = new BigDecimal[]{
                        new BigDecimal("0.04"),
                        new BigDecimal("0.045"),
                        new BigDecimal("0.0525"),
                        new BigDecimal("0.0585"),
                        new BigDecimal("0.0625"),
                        new BigDecimal("0.0685"),
                        new BigDecimal("0.0965"),
                        new BigDecimal("0.1030"),
                        new BigDecimal("0.1090")
                };
                baseTax = new BigDecimal[]{
                        BigDecimal.ZERO,
                        new BigDecimal("390.00"),
                        new BigDecimal("578.00"),
                        new BigDecimal("711.00"),
                        new BigDecimal("1252.00"),
                        new BigDecimal("5630.00"),
                        new BigDecimal("18691.00"),
                        new BigDecimal("111851.00"),
                        new BigDecimal("388001.00")
                };
            }

            // На всякий случай: если FilingStatus вдруг null или не покрыт
            default -> {
                return BigDecimal.ZERO;
            }
        }

        // 4. Рассчитываем сам налог по прогрессивной шкале
        if (annualGross.compareTo(brackets[0]) <= 0) {
            tax = annualGross.multiply(rates[0]);
        } else {
            for (int i = brackets.length - 1; i >= 0; i--) {
                if (annualGross.compareTo(brackets[i]) > 0) {
                    // базовая сумма + ставка на разницу
                    tax = baseTax[i]
                            .add(annualGross.subtract(brackets[i]).multiply(rates[i + 1]));
                    break;
                }
            }
        }

        // 5. Делим на число выплат в году и округляем
        BigDecimal taxPerPeriod = tax
                .divide(BigDecimal.valueOf(payPeriods), 2, RoundingMode.HALF_UP);

        // 6. Финальное округление
        return TaxRates.round(taxPerPeriod);
    }

    public BigDecimal calculateNYCLocalTax(User user, BigDecimal grossPay) {
        if (grossPay == null || !user.getLivesInNYC()) return BigDecimal.ZERO;

        int payPeriods = switch (user.getPayFrequency()) {
            case WEEKLY -> 52;
            case BIWEEKLY -> 26;
            case MONTHLY -> 12;
        };

        BigDecimal annualGross = grossPay.multiply(BigDecimal.valueOf(payPeriods));

        BigDecimal rate;
        if (annualGross.compareTo(new BigDecimal("12000")) <= 0) {
            rate = new BigDecimal("0.03078");
        } else if (annualGross.compareTo(new BigDecimal("25000")) <= 0) {
            rate = new BigDecimal("0.03762");
        } else if (annualGross.compareTo(new BigDecimal("50000")) <= 0) {
            rate = new BigDecimal("0.03819");
        } else {
            rate = new BigDecimal("0.03876");
        }

        BigDecimal annualTax = annualGross.multiply(rate);
        BigDecimal perPeriod = annualTax.divide(BigDecimal.valueOf(payPeriods), 2, RoundingMode.HALF_UP);
        return TaxRates.round(perPeriod);
    }




    public PayStubResponse calculateNetPay(
            User user,
            BigDecimal hourlyRate,
            BigDecimal overtimeRate,
            double totalHoursWorked,
            BigDecimal ytdPFL,
            BigDecimal ytdSocialSecurityWages
    ) {
        // Gross Pay
        BigDecimal grossPay = calculateGrossPay(hourlyRate, overtimeRate, totalHoursWorked);

        BigDecimal healthDeduction = BigDecimal.ZERO;;
        if(Boolean.TRUE.equals(user.getEnrolledInHealthPlan()) && user.getCoverageStartDate() != null && user.getMonthlyHealthPremium() != null) {
            BigDecimal annualPremium = user.getMonthlyHealthPremium().multiply(BigDecimal.valueOf(12));
            healthDeduction = annualPremium.divide(BigDecimal.valueOf(52), 2 , RoundingMode.HALF_UP);
            user.setMonthlyHealthPremium(healthDeduction);
            userRepository.save(user);
        }

        BigDecimal taxableBase = grossPay.subtract(healthDeduction).max(BigDecimal.ZERO);


        // Рассчитываем налоги правильно
        BigDecimal socialSecurity = calculateSocialSecurity(user, taxableBase, ytdSocialSecurityWages);
        BigDecimal medicare = calculateMedicare(user, taxableBase);
        BigDecimal disability = calculateNYDisability();
        BigDecimal pfl = calculateNYPaidFamilyLeave(taxableBase, ytdPFL);

        // ❗ Реально пересчитываем на основе годового дохода
        int payPeriods = switch (user.getPayFrequency()) {
            case WEEKLY -> 52;
            case BIWEEKLY -> 26;
            case MONTHLY -> 12;
        };


        // Теперь правильно считаем federal, state, local
        BigDecimal federalTax = calculateFederalTax(user, taxableBase);
        BigDecimal stateTax = calculateNYStateTax(user, taxableBase);
        BigDecimal nycTax = calculateNYCLocalTax(user, taxableBase);

        // Считаем общие удержания
        BigDecimal totalDeductions = socialSecurity
                .add(medicare)
                .add(disability)
                .add(pfl)
                .add(federalTax)
                .add(stateTax)
                .add(nycTax)
                .add(healthDeduction);

        // Чистая зарплата
        BigDecimal netPay = grossPay.subtract(totalDeductions);

        return PayStubResponse.builder()
                .grossPay(TaxRates.round(grossPay))
                .healthDeduction(TaxRates.round(healthDeduction))
                .socialSecurity(TaxRates.round(socialSecurity))
                .medicare(TaxRates.round(medicare))
                .disability(TaxRates.round(disability))
                .pfl(TaxRates.round(pfl))
                .federalTax(TaxRates.round(federalTax))
                .stateTax(TaxRates.round(stateTax))
                .nycTax(TaxRates.round(nycTax))
                .totalDeductions(TaxRates.round(totalDeductions))
                .netPay(TaxRates.round(netPay))
                .build();
    }



    public BigDecimal calculateGrossPay(BigDecimal hourlyRate, BigDecimal overtimeRate, double totalHoursWorked) {
        double regularHours = Math.min(totalHoursWorked, 40);
        double overtimeHours = Math.max(0, totalHoursWorked - 40);

        BigDecimal regularPay = hourlyRate.multiply(BigDecimal.valueOf(regularHours));
        BigDecimal overtimePay = overtimeRate.multiply(BigDecimal.valueOf(overtimeHours));

        return TaxRates.round(regularPay.add(overtimePay));
    }

}
