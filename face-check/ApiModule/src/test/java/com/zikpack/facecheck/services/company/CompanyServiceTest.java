package com.zikpack.facecheck.services.company;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.mapper.CompanyMapper;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.RoleRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.requestsResponses.CompanyUpdatingRequest;
import com.zikpak.facecheck.requestsResponses.CompanyUpdatingResponse;
import com.zikpak.facecheck.services.company.CompanyService;
import com.zikpak.facecheck.services.company.CompanyTaxCalculator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {
    @InjectMocks
    private CompanyService companyService;
    @Mock
    private  CompanyRepository companyRepository;

    @Mock
    private  CompanyMapper companyMapper;

    @Mock
    private  UserRepository userRepository;

    @Mock
    private Authentication authentication;

    private CompanyUpdatingRequest request;
    private Company company;
    private Company savedCompany;
    private CompanyUpdatingResponse companyExpectedResponse;
    private User admin;
    private User worker;
    private User accessDinedUser;



    @BeforeEach
    void setUp(){
        request = new CompanyUpdatingRequest();
        request.setCompanyName("newCom");
        request.setCompanyAddress("newAdr");
        request.setCompanyPhone("321");
        request.setCompanyEmail("newEm");
        request.setWorkersQuantity(49);


         company = new Company();
        company.setId(1);
        company.setCompanyName("LLC");
        company.setCompanyAddress("address");
        company.setCompanyPhone("123");
        company.setCompanyEmail("email");
        company.setWorkersQuantity(50);

        savedCompany = new Company();
        savedCompany.setId(1);
        savedCompany.setCompanyName("newCom");
        savedCompany.setCompanyAddress("newAdr");
        savedCompany.setCompanyPhone("321");
        savedCompany.setCompanyEmail("newEm");
        savedCompany.setWorkersQuantity(49);

        companyExpectedResponse = CompanyUpdatingResponse.builder()
                .companyId(1)
                .companyName("newCom")
                .companyAddress("newAdr")
                .companyPhone("321")
                .companyEmail("newEm")
                .workersQuantity(49)
                .build();





         admin = new User();
        admin.setId(1);
        admin.setCompany(company);
        admin.setBusinessOwner(true);

        worker = new User();
        worker.setId(1);
        worker.setCompany(company);
        worker.setBusinessOwner(false);
        worker.setUser(true);

        accessDinedUser = new User();
        accessDinedUser.setBusinessOwner(false);
        accessDinedUser.setAdmin(false);

        when(authentication.getPrincipal()).thenReturn(admin);
    }


    @Test
    void deleteCompany(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        companyService.deleteCompany(company.getId(), authentication);

        verify(companyRepository).deleteById(1);
    }

    @Test
    void deleteCompany_throwAccessDeniedException(){
        when(authentication.getPrincipal()).thenReturn(worker);

        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> companyService.deleteCompany(company.getId(), authentication)
        );
        assertEquals("You do not have permission to delete this company", ex.getMessage());
        verify(companyRepository,never()).findById(any());
        verify(companyRepository, never()).deleteById(any());
    }

    @Test
     void deleteCompany_EntityNotFoundException(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(companyRepository.findById(company.getId())).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> companyService.deleteCompany(company.getId(), authentication)
        );

        assertEquals("Company not found", ex.getMessage());
        verify(companyRepository).findById(company.getId());
        verify(companyRepository,never()).deleteById(any());
    }







    @Test
    void fireEmployee(){
        when(authentication.getPrincipal()).thenReturn(admin);

        when(userRepository.findById(worker.getId())).thenReturn(Optional.of(worker));

        when(companyRepository.save(company)).thenReturn(company);

        companyService.fireEmployee(worker.getId(), authentication);

        assertEquals(49, company.getWorkersQuantity());
        verify(companyRepository).save(company);
        verify(userRepository).deleteById(worker.getId());
    }

    @Test
    void fireWorker_notAuthorized(){
        when(authentication.getPrincipal()).thenReturn(worker);

        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> companyService.fireEmployee(worker.getId(), authentication)
        );
        assertEquals("You do not have permission to update this company", ex.getMessage());

        verify(userRepository, never()).findById(any());
        verify(companyRepository, never()).save(any());
        verify(userRepository, never()).deleteById(any());

    }

    @Test
    void fireWorker_throwEntityNotFoundException(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(userRepository.findById(worker.getId())).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> companyService.fireEmployee(worker.getId(), authentication)
        );
        assertEquals("Employee not found", ex.getMessage());
        verify(userRepository).findById(worker.getId());
        verify(userRepository, never()).deleteById(any());
        verify(companyRepository, never()).save(any());
    }

    @Test
    void updateCompany(){
        when(authentication.getPrincipal()).thenReturn(admin);

        when(companyRepository.findById(1)).thenReturn(Optional.of(company));

        when(companyRepository.save(company)).thenReturn(savedCompany);

        when(companyMapper.toCompanyUpdateResponse(savedCompany))
                .thenReturn(companyExpectedResponse);

        CompanyUpdatingResponse actual = companyService.updateCompany(request,1,authentication);
        assertEquals(companyExpectedResponse, actual);
        assertEquals("newCom", company.getCompanyName());
        assertEquals("newAdr", company.getCompanyAddress());
        assertEquals("321", company.getCompanyPhone());
        assertEquals("newEm", company.getCompanyEmail());
        assertEquals(49, company.getWorkersQuantity());

        verify(companyRepository).findById(1);
        verify(companyRepository).save(company);
        verify(companyMapper).toCompanyUpdateResponse(savedCompany);

    }


    @Test
    void updateCompany_throwAccessDinedException(){
        when(authentication.getPrincipal()).thenReturn(accessDinedUser);
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> companyService.updateCompany(request,1,authentication)
        );
        assertEquals("You do not have permission to update this company", ex.getMessage());

        verify(companyRepository, never()).findById(any());
        verify(companyRepository, never()).save(any());
    }

    @Test
    void updateCompany_throwRuntimeException(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> companyService.updateCompany(request,1,authentication)
        );
        assertEquals("Company not found", ex.getMessage());

        verify(companyRepository).findById(1);
        verify(companyRepository, never()).save(any());
    }


    @Test
    void companyNameNotFound(){
        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> companyService.companyName(authentication)
        );
        assertTrue(ex.getMessage().contains("Company with ID: 1 not found"));
    }

    @Test
    void companyAddressNotFound(){
        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> companyService.companyAddress(authentication)
        );
        assertTrue(ex.getMessage().contains("Company with ID: 1 not found"));
    }

    @Test
    void companyPhoneNotFound(){
        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> companyService.companyPhone(authentication)
        );
        assertTrue(ex.getMessage().contains("Company with ID: 1 not found"));
    }

    @Test
    void companyEmailNotFound(){
        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> companyService.companyEmail(authentication)
        );
        assertTrue(ex.getMessage().contains("Company with ID: 1 not found"));
    }


    @Test
    void companyNameTest(){
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));

        String result = companyService.companyName(authentication);

        assertEquals("LLC", result);
    }
    @Test
    void companyAddress(){
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));

        String result = companyService.companyAddress(authentication);

        assertEquals("address", result);
    }

    @Test
    void companyPhone(){
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        String result = companyService.companyPhone(authentication);
        assertEquals("123", result);
    }

    @Test
    void companyEmail(){
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        String result = companyService.companyEmail(authentication);
        assertEquals("email", result);
    }








}
