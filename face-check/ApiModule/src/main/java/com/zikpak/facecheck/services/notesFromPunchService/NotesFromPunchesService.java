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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotesFromPunchesService {


    private final WorkerAttendanceRepository workerAttendanceRepository;
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "checkInTime"));

        Page<WorkerAttendance> notesPage;

        if (isAppOwner) {
            notesPage = workerAttendanceRepository.findAllNotesForAppowner(pageable);
        } else {
            notesPage = workerAttendanceRepository.findAllNotesForAdmin(user.getCompany().getId(), pageable);
        }

        List<NotesForPunchResponse> responses = notesPage.getContent().stream()
                .map(notesForPunchMapper::toNotesForPunchResponse)
                .toList();

        return new PageResponse<>(
                responses,
                notesPage.getNumber(),
                notesPage.getSize(),
                notesPage.getTotalElements(),
                notesPage.getTotalPages(),
                notesPage.isFirst(),
                notesPage.isLast()
        );
    }


}
