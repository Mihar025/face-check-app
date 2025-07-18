package com.zikpak.facecheck.controllers;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
import com.zikpak.facecheck.taxesServices.services.sickDayService.SickLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("sick-leave")

@RequiredArgsConstructor
public class SickLeaveController {


    private final WorkAttendanceService workAttendanceService;
    private final SickLeaveService sickLeaveService;

    @GetMapping("/info")
    public ResponseEntity<SickLeaveService.SickLeaveInfo> getSickLeaveInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        SickLeaveService.SickLeaveInfo info = sickLeaveService.getSickLeaveInfo(user.getId());
        return ResponseEntity.ok(info);
    }

    /**
     * Использовать sick leave
     */
    @PostMapping("/use")
    public ResponseEntity<?> useSickLeave(
            Authentication authentication,
            @RequestBody Map<String, Object> body,
            @RequestParam("sickDate") LocalDate sickDate
    ) {
        User user = (User) authentication.getPrincipal();
        BigDecimal hoursToUse = new BigDecimal(body.get("hours").toString());

        try {
            // NYC требует минимум 4 часа
            if (hoursToUse.compareTo(BigDecimal.valueOf(4)) < 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Минимальное количество sick leave - 4 часа"
                ));
            }

            workAttendanceService.useSickLeave(user.getId(), hoursToUse, sickDate);
            BigDecimal remaining = sickLeaveService.getAvailableSickHours(user.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Sick leave использован успешно",
                    "hoursUsed", hoursToUse,
                    "remainingHours", remaining
            ));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

}
