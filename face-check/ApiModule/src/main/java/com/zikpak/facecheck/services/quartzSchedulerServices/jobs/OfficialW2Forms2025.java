package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.taxesServices.pdfServices.W2OfficialPDFService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OfficialW2Forms2025 extends BaseSchedulerJob {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private W2OfficialPDFService w2OfficialPDFService;

    @Autowired
    private ReportsMailSender reportsMailSender;


    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {
        int currentYear = LocalDate.now().getYear();
        int targetYear = currentYear - 1; // W-2 за прошлый год

        if (currentYear != 2026) {
            log.info("⏭️ Пропускаем генерацию W-2. Текущий год: {}, ожидаем: 2026", currentYear);
            return JobResult.success(0) ;
        }

        log.info("📄 W2FormScheduler запущен: генерируем W-2 за {} всем работникам", targetYear);

        // Получаем всех пользователей, у которых есть компания (т.е. они работники)
        List<User> workers = userRepository.findAll().stream()
                .filter(user -> user.getCompany() != null)
                .filter(user -> !user.isBusinessOwner()) // исключаем владельцев бизнеса
                .toList();


        // Группируем по компаниям для отправки уведомлений
        Map<Company, List<User>> workersByCompany = new HashMap<>();
        int totalSuccess = 0;
        int totalFailure = 0;
        StringBuilder errors = new StringBuilder();

        for (User worker : workers) {
            try {
                // Генерируем W-2
                w2OfficialPDFService.generateFilledPdf(worker.getId(), worker.getCompany().getId(),  targetYear);
                log.info("✅ Сгенерирован W-2 для workerId={} за {}", worker.getId(), targetYear);

                Company company = worker.getCompany();
                workersByCompany.computeIfAbsent(company, k -> new ArrayList<>()).add(worker);
                totalSuccess++;

            } catch (Exception ex) {
                totalFailure++;
                log.error("❌ Ошибка генерации W-2 для workerId={}", worker.getId(), ex);
            }
        }

        for (Map.Entry<Company, List<User>> entry : workersByCompany.entrySet()) {
            Company company = entry.getKey();
            int count = entry.getValue().size();
            try {
                reportsMailSender.sendEmailW2Forms(company.getCompanyEmail());
                log.info("📧 Уведомление о W-2 отправлено компании: {} (forms: {})",
                        company.getCompanyEmail(), count);
            } catch (Exception e) {
                log.error("❌ Ошибка отправки уведомления для компании: {}",
                        company.getCompanyEmail(), e);
            }
        }

        log.info("🏁 W2FormScheduler завершил генерацию W-2 за {}. Всего: {} работников",
                targetYear, workers.size());

        if (totalFailure == 0 && errors.length() == 0) {
            return JobResult.success(totalSuccess);
        } else if (totalSuccess > 0) {
            return JobResult.partialSuccess(totalSuccess, totalFailure, errors.toString());
        } else {
            return JobResult.failure(errors.toString());
        }


    }
}

