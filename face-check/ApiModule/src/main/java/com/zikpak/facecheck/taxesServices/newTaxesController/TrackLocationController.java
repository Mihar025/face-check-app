package com.zikpak.facecheck.taxesServices.newTaxesController;

import com.zikpak.facecheck.taxesServices.services.LocationTrackingGoogle.LocationRecordDto;
import com.zikpak.facecheck.taxesServices.services.LocationTrackingGoogle.LocationService;
import com.zikpak.facecheck.taxesServices.services.LocationTrackingGoogle.LocationUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("location")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TrackLocationController {

    private final LocationService locationService;

    @PostMapping("/update/{userId}")
    @Operation(summary = "Update user location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location saved successfully",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<?> updateLocation(
            @PathVariable("userId") Integer userId,
            @RequestBody LocationUpdateDto locationUpdateDto) {
        try {
            LocationRecordDto saved = locationService.saveLocation(userId, locationUpdateDto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Location saved successfully",
                    "locationId", saved.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "Get location history for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LocationRecordDto.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<List<LocationRecordDto>> getLocationHistory(
            @Parameter(description = "User ID") @PathVariable("userId") Integer userId,
            @Parameter(description = "Start date (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            List<LocationRecordDto> locationRecords = locationService.getLocationHistory(userId, startDate);
            return ResponseEntity.ok(locationRecords);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/last/{userId}")
    @Operation(summary = "Get last location for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(schema = @Schema(implementation = LocationRecordDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<LocationRecordDto> getLastLocation(
            @Parameter(description = "User ID") @PathVariable Integer userId) {
        try {
            LocationRecordDto locationRecord = locationService.getLastLocation(userId);
            return ResponseEntity.ok(locationRecord);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/batch/{userId}")
    @Operation(summary = "Save batch locations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LocationRecordDto.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<List<LocationRecordDto>> saveBatchLocations(
            @Parameter(description = "User ID") @PathVariable Integer userId,
            @RequestBody List<LocationUpdateDto> locationRecords) {
        try {
            List<LocationRecordDto> savedLocations = locationService.saveBatchLocations(userId, locationRecords);
            return ResponseEntity.ok(savedLocations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}