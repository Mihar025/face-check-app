package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.services.stripe.BillingService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class StripeRecountingJob extends BaseSchedulerJob {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private BillingService billingService;



    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {

        int failure = 0;
        int success = 0;


            List<Company> companies = companyRepository.findAll();
            for (Company company : companies) {
                try {
                if(company.getStripeSubscriptionId() == null){
                    continue;
                }

                if(!"active".equals(company.getSubscriptionStatus())){
                    continue;
                }

                billingService.updateSeats(company.getId());
                success ++;
                } catch(Exception e) {
                    failure++;
                    log.error("Error during recounting seats: {}", e.getMessage());
                }
            }

        if(failure > 0){
            return JobResult.failure(failure + " companies failed to recount seats");
        }

        return JobResult.success(success);

    }
}
