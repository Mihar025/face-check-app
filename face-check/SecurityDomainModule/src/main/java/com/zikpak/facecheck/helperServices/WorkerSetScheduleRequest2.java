package com.zikpak.facecheck.helperServices;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerSetScheduleRequest2 {

    // График по дням недели
    @NotNull(message = "Weekly schedule cannot be null")
    private Map<DayOfWeek, DaySchedule> weeklySchedule;

    // Вложенный класс для расписания одного дня
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DaySchedule {
        @NotNull(message = "Start time cannot be null")
        private LocalTime startTime;

        @NotNull(message = "End time cannot be null")
        private LocalTime endTime;

        @NotNull(message = "Lunch start cannot be null")
        private LocalTime lunchStart;

        @NotNull(message = "Lunch end cannot be null")
        private LocalTime lunchEnd;

        @NotNull(message = "isCompanyPayingLunch cannot be null")
        private Boolean isCompanyPayingLunch;

        // Выходной день (если true - день не рабочий)
        private Boolean isDayOff;

        public void validate() {
            if (isDayOff != null && isDayOff) {
                return; // Если выходной - валидация не нужна
            }

            LocalTime minimumStartTime = LocalTime.of(6, 30);
            if (startTime.isBefore(minimumStartTime)) {
                throw new IllegalArgumentException("Start time cannot be before 6:30 AM");
            }

            if (startTime.isAfter(endTime)) {
                throw new IllegalArgumentException("Start time cannot be after end time");
            }

            if (lunchStart != null && lunchEnd != null) {
                if (lunchStart.isAfter(lunchEnd)) {
                    throw new IllegalArgumentException("Lunch start cannot be after lunch end");
                }

                if (lunchStart.isBefore(startTime) || lunchStart.isAfter(endTime)) {
                    throw new IllegalArgumentException("Lunch start must be within work hours");
                }

                if (lunchEnd.isBefore(startTime) || lunchEnd.isAfter(endTime)) {
                    throw new IllegalArgumentException("Lunch end must be within work hours");
                }
            }
        }
    }

    public void validate() {
        if (weeklySchedule == null || weeklySchedule.isEmpty()) {
            throw new IllegalArgumentException("Weekly schedule cannot be empty");
        }

        // Валидация каждого дня
        for (Map.Entry<DayOfWeek, DaySchedule> entry : weeklySchedule.entrySet()) {
            DayOfWeek day = entry.getKey();
            DaySchedule schedule = entry.getValue();

            try {
                schedule.validate();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        String.format("Invalid schedule for %s: %s", day, e.getMessage())
                );
            }
        }
    }
}