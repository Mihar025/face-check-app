package com.zikpak.facecheck.controllers;

import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.services.notesFromPunchService.NotesForPunchResponse;
import com.zikpak.facecheck.services.notesFromPunchService.NotesFromPunchesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes-for-punch")
@RequiredArgsConstructor
public class NotesFromPunchesController {

    private final NotesFromPunchesService notesFromPunchesService;


    @GetMapping
    public ResponseEntity<PageResponse<NotesForPunchResponse>> getAllNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        PageResponse<NotesForPunchResponse> response =
                notesFromPunchesService.findAllNotesForPunch(page, size, authentication);

        return ResponseEntity.ok(response);
    }


}
