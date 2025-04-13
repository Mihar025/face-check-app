package com.zikpak.facecheck.domain;

import com.zikpak.facecheck.requestsResponses.UserCompanyNameInformation;
import com.zikpak.facecheck.requestsResponses.worker.*;
import org.springframework.security.core.Authentication;

public interface UserOperations {

    void updateEmail( String email, Authentication authentication);
    void updatePassword( String password, Authentication authentication);
    void updatePhone( String phone, Authentication authentication);
    void updateHomeAddress( String homeAddress, Authentication authentication);

    UserFullNameResponse findWorkerFullName(Authentication authentication);
    UserEmailResponse findWorkerEmail(Authentication authentication);
    UserPhoneNumberResponse findWorkerPhoneNumber(Authentication authentication);
    UserHomeAddressResponse findWorkerHomeAddress(Authentication authentication);
    UserFullContactInformation findWorkerFullContactInformation(Authentication authentication);
    UserCompanyNameInformation findUserCompanyName(Authentication authentication);
}
