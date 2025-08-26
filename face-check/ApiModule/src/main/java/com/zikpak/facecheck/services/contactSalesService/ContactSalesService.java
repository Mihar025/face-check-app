package com.zikpak.facecheck.services.contactSalesService;


import com.zikpak.facecheck.entity.ContactSalesForm;
import com.zikpak.facecheck.repository.ContactSalesFormRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactSalesService {

    private final ContactSalesFormRepository contactSalesFormRepository;
    private final ContactSalesFormMapper contactSalesFormMapper;



    public PageResponse<ContactSalesFormResponse> findAllContactForms(int page, int size){
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
            Page<ContactSalesForm> forms = contactSalesFormRepository.findAll(pageable);

            List<ContactSalesFormResponse> formsResponse = forms.getContent().stream()
                    .map(contactSalesFormMapper::toContactFormResponse)
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

    public ContactSalesFormResponse findContactFormById(Integer id){
        var foundedForm = contactSalesFormRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cannot find contact form with provided Id " + id));
        return contactSalesFormMapper.toContactFormResponse(foundedForm);
    }

    @Transactional
    public void deleteContactFormById(Integer id){
        contactSalesFormRepository.deleteById(id);
    }

    @Transactional
    public ContactSalesFormResponse saveContactForm(ContactSalesFormRequest contactSalesFormRequest){
        ContactSalesForm newForm = new ContactSalesForm();
        newForm.setFirstName(contactSalesFormRequest.getFirstName());
        newForm.setLastName(contactSalesFormRequest.getLastName());
        newForm.setPhoneNumber(contactSalesFormRequest.getPhoneNumber());
        newForm.setCreatedDate(LocalDate.now());
        log.info("Contact First Name" + contactSalesFormRequest.getFirstName());
        log.info("Contact Last Name:" +  contactSalesFormRequest.getLastName());
        log.info("Contact Phonenumber" + contactSalesFormRequest.getPhoneNumber());

        var savedForm = contactSalesFormRepository.save(newForm);
        return contactSalesFormMapper.toContactFormResponse(savedForm);
    }





}
