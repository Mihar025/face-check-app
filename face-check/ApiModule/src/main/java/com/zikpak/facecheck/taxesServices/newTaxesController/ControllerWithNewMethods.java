package com.zikpak.facecheck.taxesServices.newTaxesController;


import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.workAttendanceService.UpdatePunchForWorkerRequest;
import com.zikpak.facecheck.services.workAttendanceService.UpdatePunchForWorkerResponse;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("newMethods")
@RequiredArgsConstructor
@Slf4j
public class ControllerWithNewMethods {

    private final WorkAttendanceService workAttendanceService;
    private final ReportsMailSender reportsMailSender;


    @PutMapping("/worker/{workerId}/punch")
    public ResponseEntity<UpdatePunchForWorkerResponse> updatePunchForWorker(
            @PathVariable Integer workerId,
            @Valid @RequestBody UpdatePunchForWorkerRequest request) {

        log.info("Received request to update punch for worker: {}, date: {}, type: {}",
                workerId, request.getNewPunchDate(), request.getPunchType());

        try {
            UpdatePunchForWorkerResponse response = workAttendanceService
                    .updatePunchForWorker(workerId, request);

            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            log.error("Worker not found: {}", workerId);
            return ResponseEntity.notFound().build();

        } catch (IllegalStateException e) {
            log.error("Invalid state for update: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(UpdatePunchForWorkerResponse.builder()
                            .workerId(workerId)
                            .isSuccessful(false)
                            .message(e.getMessage())
                            .build());

        } catch (Exception e) {
            log.error("Error updating punch for worker: {}", workerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(UpdatePunchForWorkerResponse.builder()
                            .workerId(workerId)
                            .isSuccessful(false)
                            .message("Internal server error: " + e.getMessage())
                            .build());
        }
   }


    @PostMapping("/custom-mail/940/{email}")
    public ResponseEntity<Void> sendEmail940F(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmail940Form(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/custom-mail/941/{email}")
    public ResponseEntity<Void> sendEmail941F(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmail941Form(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/custom-mail/941sb/{email}")
    public ResponseEntity<Void> sendEmail941SBF(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmail941SBForm(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/custom-mail/AFR/{email}")
    public ResponseEntity<Void> sendEmailAnnualFutaReport(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmailAnnualFutaReport(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/custom-mail/FC/{email}")
    public ResponseEntity<Void> sendEmailFutaCompliance(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmailFutaCompliance(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/custom-mail/CSV/{email}")
    public ResponseEntity<Void> sendEmailCSV(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmailCSV(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/custom-mail/EFW2/{email}")
    public ResponseEntity<Void> sendEmailEFW2(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmailEFW2(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/custom-mail/940ScheduleA/{email}")
    public ResponseEntity<Void> sendEmail940FormScheduleA(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmail940FormAndScheduleA(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/custom-mail/HR/{email}")
    public ResponseEntity<Void> sendEmailHR(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmailHoursReport(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/custom-mail/paystubs/{email}")
    public ResponseEntity<Void> sendEmailPaystubs(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmailPaystubs(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/custom-mail/QSF/{email}")
    public ResponseEntity<Void> sendEmailQSF(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmailQuarterSutaForm(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/custom-mail/QFR/{email}")
    public ResponseEntity<Void> sendEmailQFR(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmailQuarterFUTAReport(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/custom-mail/W2F/{email}")
    public ResponseEntity<Void> sendEmailW2F(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmailW2Forms(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/custom-mail/WRP/{email}")
    public ResponseEntity<Void> sendEmailWPR(@PathVariable("email") String email) {
        try {
            reportsMailSender.sendEmailWeeklyPayrollReport(email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MessagingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
