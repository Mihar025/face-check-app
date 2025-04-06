package com.zikpak.facecheck.requestsResponses.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerHourResponse {


        private Double regularHours;
        private Double overtimeHours;
        private Double totalHours;

}
