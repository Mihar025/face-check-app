package com.zikpak.facecheck.taxesServices.services.wcRiskService;

import com.zikpak.facecheck.entity.WcRiskClass;
import com.zikpak.facecheck.repository.WcRiskClassRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.dto.WcCodeRequest;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.dto.WcRiskResponse;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.mapper.WcRiskClassMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WcRiskService {

    private final WcRiskClassRepository repo;
    private final WcRiskClassMapper codeMapper;

    public List<WcRiskClass> listConstructionClasses(int year, String industryTag) {
        return repo.findByIndustryTagAndEffectiveYear(industryTag, year);
    }

    public WcRiskResponse findCodeById(String code){
        WcRiskClass existing = repo.findById(code)
                .orElseThrow(() -> new IllegalArgumentException("Code not found: " + code));
        return codeMapper.toWcRisk(existing);
    }

    public PageResponse<WcRiskResponse> findAllCodes(int page, int size, String code ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("industryTag").descending());
        Page<WcRiskClass> codes = repo.findAll(pageable);
        if(codes.isEmpty()) {
            log.info("No codes found : {}", code);
        }
        List<WcRiskResponse> allCodes = codes.getContent()
                .stream()
                .map(codeMapper::toWcRisk)
                .toList();
        return new PageResponse<>(
                allCodes,
                codes.getNumber(),
                codes.getSize(),
                codes.getTotalElements(),
                codes.getTotalPages(),
                codes.isFirst(),
                codes.isLast()
        );
    }


    @Transactional(rollbackOn = Exception.class)
    public WcRiskResponse create(WcCodeRequest wc) {
        if (repo.existsById(wc.getCode())) {
            throw new IllegalArgumentException("Code already exist!" + wc.getCode());
        }
         var newCode =WcRiskClass.builder()
                .code(wc.getCode())
                .description(wc.getDescription())
                .rate(wc.getRate())
                .industryTag(wc.getIndustryTag())
                .effectiveYear(wc.getEffectiveYear())
                .build();
        var saved = repo.save(newCode);
        return codeMapper.toWcRisk(saved);

    }

    @Transactional(rollbackOn = Exception.class)
    public WcRiskResponse update(String code, WcCodeRequest update) {
        var updatedWc = updateCode(code, update);
        return codeMapper.toWcRisk(updatedWc) ;
    }


    @Transactional(rollbackOn = Exception.class)
    public void delete(String code) {
        if (!repo.existsById(code)) {
            throw new IllegalArgumentException("Code not founded! " + code);
        }
        repo.deleteById(code);
    }



    private WcRiskClass updateCode(String code , WcCodeRequest update){
        WcRiskClass existing = repo.findById(code)
                .orElseThrow(() -> new IllegalArgumentException("Code not found: " + code));

        existing.setDescription(update.getDescription());
        existing.setRate(update.getRate());
        existing.setEffectiveYear(update.getEffectiveYear());
        existing.setIndustryTag(update.getIndustryTag());
        return repo.save(existing);
    }


}
