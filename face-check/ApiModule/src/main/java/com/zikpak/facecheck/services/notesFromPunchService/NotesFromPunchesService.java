package com.zikpak.facecheck.services.notesFromPunchService;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.worker.RelatedUserInCompanyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotesFromPunchesService {


    private final WorkerAttendanceRepository workerAttendanceRepository;
    private final UserRepository userRepository;
    private final NotesForPunchMapper notesForPunchMapper;


  /*  @Cacheable(
            value = "punchNotes",
            key = "'allPunchNotes' + #page + '_size_' + #size",
            unless = "#result == null || #result.content.isEmpty()"
    )

   */

    public PageResponse<NotesForPunchResponse> findAllNotesForPunch(
            int page,
            int size,
            Authentication authentication) {

        User user = ((User) authentication.getPrincipal());

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));

        boolean isAppOwner = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("AppOwner"));

        if (!isAdmin && !isAppOwner) {
            log.warn("Unauthorized access attempt by user: {}", user.getEmail());
            throw new AccessDeniedException("You do not have permission to view notes");
        }

        List<WorkerAttendance> allNotes;

        if (isAppOwner) {
            allNotes = workerAttendanceRepository.findAllNotesForAppowner();
        } else {
            allNotes = workerAttendanceRepository.findAllNotesForAdmin(user.getCompany().getId());
        }

        int start = page * size;
        int end = Math.min(start + size, allNotes.size());

        if (start > allNotes.size()) {
            start = 0;
            end = 0;
        }

        List<WorkerAttendance> pagedNotes = start < end
                ? allNotes.subList(start, end)
                : List.of();

        List<NotesForPunchResponse> responses = pagedNotes.stream()
                .map(notesForPunchMapper::toNotesForPunchResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) allNotes.size() / size);

        return new PageResponse<>(
                responses,
                page,
                size,
                allNotes.size(),
                totalPages,
                page == 0,
                end >= allNotes.size()
        );
    }



}
