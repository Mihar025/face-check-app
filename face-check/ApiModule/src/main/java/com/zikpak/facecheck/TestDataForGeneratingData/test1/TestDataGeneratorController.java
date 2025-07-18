package com.zikpak.facecheck.TestDataForGeneratingData.test1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("test-data")
@RequiredArgsConstructor
@Slf4j
public class TestDataGeneratorController {

    private final PayrollTestDataGenerator payrollTestDataGenerator;

    // Генерация полных данных за весь год
    @PostMapping("/generate-full-year")
    public ResponseEntity<Map<String, String>> generateFullYearData() {
        log.info("🚀 Запуск генерации данных за полный год");

        try {
            payrollTestDataGenerator.generateFullYearData();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Данные за год успешно сгенерированы!"
            ));

        } catch (Exception e) {
            log.error("Ошибка генерации", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }




}