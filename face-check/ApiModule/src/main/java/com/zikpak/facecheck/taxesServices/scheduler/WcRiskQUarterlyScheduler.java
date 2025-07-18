package com.zikpak.facecheck.taxesServices.scheduler;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.WcRiskCsvService;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.WcRiskServiceForPDF;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class WcRiskQUarterlyScheduler {
    private final WcRiskServiceForPDF pdfService;
    private final WcRiskCsvService csvService;
    private final CompanyRepository companyRepository;


    // Ежедневно в 8:00 по часовому поясу New York
    @Scheduled(cron = "0 0 8 * * *", zone = "America/New_York")
    public void generateQuarterlyWcRiskReportsIfNeeded() {
        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));
        LocalDate inOneWeek = today.plusWeeks(1);

        // Концы кварталов
        List<MonthDay> quarterEnds = List.of(
                MonthDay.of(3, 31),
                MonthDay.of(6, 30),
                MonthDay.of(9, 30),
                MonthDay.of(12, 31)
        );

        MonthDay md = MonthDay.from(inOneWeek);
        if (!quarterEnds.contains(md)) {
            log.debug("WC-Risk: сегодня {} — конец квартала через неделю? нет → пропускаем", today);
            return;
        }

        int month = inOneWeek.getMonthValue();
        int q = (month - 1) / 3;               // 0..3
        LocalDate periodStart = LocalDate.of(
                inOneWeek.getYear(),
                q * 3 + 1,  // 1,4,7,10
                1
        );
        LocalDate periodEnd = inOneWeek;

        log.info("🗓 WC-Risk: формируем отчёты за период {} — {}", periodStart, periodEnd);


        List<Company> companies = companyRepository.findAll();
        if (companies.isEmpty()) {
            log.warn("WC-Risk: нет ни одной компании для отчётов");
            return;
        }
        for (Company company : companies) {
            Integer companyId = company.getId();
            try {
                byte[] pdf = pdfService.generateWcReportPdf(companyId, periodStart, periodEnd);
                log.info("✅ [{}] WC-Risk PDF готов ({} bytes)", companyId, pdf.length);

                byte[] csv = csvService.generateCsv(companyId, periodStart, periodEnd);
                log.info("✅ [{}] WC-Risk CSV готов ({} bytes)", companyId, csv.length);

            } catch (Exception ex) {
                log.error("❌ [{}] Ошибка генерации WC-Risk отчётов", companyId, ex);
            }
        }
    }
}