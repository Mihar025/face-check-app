package com.zikpak.facecheck.services.company;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCompanyAddressResponse {
    private String companyAddress;
}
