package com.zikpak.facecheck.domain;

import org.springframework.security.core.Authentication;

public interface CompanyServiceImpl {

    String companyName(Authentication authentication);
    String companyAddress(Authentication authentication);
    String companyPhone(Authentication authentication);
    String companyEmail(Authentication authentication);




}
