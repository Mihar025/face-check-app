package com.zikpak.facecheck.services.foremanService;


import com.zikpak.facecheck.mapper.UserMapper;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.admin.WorksiteWorkerResponse;
import com.zikpak.facecheck.requestsResponses.worker.UserFullContactInformation;
import com.zikpak.facecheck.services.ForemanAndAdminFunctional.ForemanAndAdminService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ForemanService {
        private final UserMapper userMapper;

            /*

            findAllWorkersWhoseDidPunchIn
            findWorkerByIdWhoseDidPunchIn
            SendRequestOnUpdatingPunchInTime which admin have to approve




             */

        private UserRepository userRepository;
        private final ForemanAndAdminService foremanAndAdminService;




        public PageResponse<WorksiteWorkerResponse> findAllWorkersInWorkSite(int page, int size, Integer workSiteId, Authentication authentication) {
                return foremanAndAdminService.findAllWorkersInWorkSite(page, size, workSiteId, authentication);
        }

        public UserFullContactInformation findWorkerByName(String firstName){
                 var userInfo = userRepository.findUserByFirstName(firstName)
                        .orElseThrow(() -> new EntityNotFoundException("Worker with name " + firstName + " not found"));
                 return userMapper.toUserFullContactInformation(userInfo);
                        }





}
