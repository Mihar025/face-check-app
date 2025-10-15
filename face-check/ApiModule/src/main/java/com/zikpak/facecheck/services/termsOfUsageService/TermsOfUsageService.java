package com.zikpak.facecheck.services.termsOfUsageService;


import com.zikpak.facecheck.entity.TermsOfUseAgreement;
import com.zikpak.facecheck.repository.TermOfUsageRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsOfUsageService {

    private final TermOfUsageRepository termOfUsageRepository;
    private final TermsOfUseAgreementMapper termsOfUsageMapper;


    @Transactional
    public TermsOfUseRequestDataResponse  acceptTermsOfUseFromWorkerAt(TermsOfUseRequestDataRequest request){
        TermsOfUseAgreement termsOfUseAgreement = new TermsOfUseAgreement();
        termsOfUseAgreement.setEvent(request.getEvent());
        termsOfUseAgreement.setUserId(request.getUserId());
        termsOfUseAgreement.setTimeStamp(LocalDate.now());
        termsOfUseAgreement.setTermsVersion(request.getTermsVersion());
        termsOfUseAgreement.setPrivacyVersion(request.getPrivacyVersion());
        termsOfUseAgreement.setIp(request.getIp());
        termsOfUseAgreement.setDevice(request.getDevice());
        termsOfUseAgreement.setOsVersion(request.getOsVersion());
        var savedTermsAndPolicy = termOfUsageRepository.save(termsOfUseAgreement);

        return termsOfUsageMapper.toTermsOfUse(savedTermsAndPolicy);
    }


    public TermsOfUseRequestDataResponse findById(Integer id){
        var foundedTerms = termOfUsageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Terms of usage for worker with id: " + id + " not found"));
        return termsOfUsageMapper.toTermsOfUse(foundedTerms);
    }



    public PageResponse<TermsOfUseRequestDataResponse> findAllTermsOfUse(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("timeStamp").descending());
        Page<TermsOfUseAgreement> forms = termOfUsageRepository.findAll(pageable);

        List<TermsOfUseRequestDataResponse> formsResponse = forms.getContent().stream()
                .map(termsOfUsageMapper::toTermsOfUse)
                .toList();

        return new PageResponse<>(
                formsResponse,
                forms.getNumber(),
                forms.getSize(),
                forms.getTotalElements(),
                forms.getTotalPages(),
                forms.isFirst(),
                forms.isLast()
        );
    }






}
