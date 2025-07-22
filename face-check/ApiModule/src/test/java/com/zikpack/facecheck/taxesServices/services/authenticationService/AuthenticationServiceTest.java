package com.zikpack.facecheck.taxesServices.services.authenticationService;

import com.zikpak.facecheck.authRequests.*;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.Role;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.mapper.CompanyMapper;
import com.zikpak.facecheck.mapper.UserMapper;
import com.zikpak.facecheck.metrics.MetricsAuthenticationService;
import com.zikpak.facecheck.metrics.MetricsWorkSiteService;
import com.zikpak.facecheck.repository.*;
import com.zikpak.facecheck.security.AuthenticationServiceImpl;
import com.zikpak.facecheck.security.EmailService;
import com.zikpak.facecheck.security.EmailTemplateName;
import com.zikpak.facecheck.security.JwtService;
import com.zikpak.facecheck.services.userService.UserService;
import com.zikpak.facecheck.taxesServices.pdfServices.FillFormI9;
import com.zikpak.facecheck.taxesServices.pdfServices.FillFormW4;
import com.zikpak.facecheck.taxesServices.services.authenticationService.AuthenticationService;
import io.micrometer.core.instrument.Timer;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private  AuthenticationServiceImpl authenticationServiceImpl;

    @InjectMocks
    private  AuthenticationServiceImpl authenticationServiceImpl2;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private  UserMapper userMapper;

    @Mock
    private FillFormW4 fillFormW4;

    @Mock
    FillFormI9 fillFormI9;
    @Mock private TokenRepository tokenRepository;

    @Mock private EmailService emailService;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private AuthenticationManager authenticationManager;

    @Mock private WorkerPayrollRepository workerPayrollRepository;

    @Mock private JwtService jwtService;

    @Mock private Authentication authentication;

    @Mock private CompanyMapper companyMapper;


    @Mock
    private MetricsAuthenticationService metric;
    @Mock
    private Timer.Sample timerSample;

    private User worker;
    private User admin;
    private User worker2;
    private Company company;
    private Role userRole;
    private Role adminRole;
    private RegistrationRequest registrationRequest;
    private RegistrationAdminRequest adminRegRequest;
    private String activationUrl = "https://app.example.com/activate";

    private PaymentRequest paymentRequest;
    private WorkerPayroll payment;

    private CompanyRegistrationRequest companyRegistrationRequest;

    private AuthenticationResponse authenticationResponse;
    private AuthenticationRequest authenticationRequest;




    @BeforeEach
    void setUp() {

        registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("email");
        registrationRequest.setPassword("password");
        registrationRequest.setCompanyName("companyName");

        adminRegRequest = new RegistrationAdminRequest();
        adminRegRequest.setEmail("email");
        adminRegRequest.setPassword("password");

        userRole = new Role();
        userRole.setName("USER");

        adminRole = new Role();
        adminRole.setName("ADMIN");

        company = new Company();
        company.setId(1);
        company.setCompanyName("companyName");

        worker = new User();
        worker.setId(1);
        worker.setEmail("email");
        worker.setPassword("password");
        worker.setCompany(company);
        worker.setBaseHourlyRate(new BigDecimal("20.0"));
        worker.setOvertimeRate(new BigDecimal("35.0"));

        admin = new User();
        admin.setId(2);
        admin.setEmail("email2");
        admin.setPassword("password2");
        admin.setCompany(company);
        admin.setAdmin(true);
        admin.setUser(false);


        worker2 = new User();
        worker2.setFirstName("John");
        worker2.setLastName("Doe");
        worker2.setEmail("user@example.com");


        paymentRequest = new PaymentRequest();
        paymentRequest.setHourRate(new BigDecimal("20.0"));
        paymentRequest.setOvertimeRate(new BigDecimal("35.0"));


        payment = new WorkerPayroll();
        payment.setBaseHourlyRate(new BigDecimal("20.0"));
        payment.setOvertimeRate(new BigDecimal("35.0"));
        payment.setWorker(worker);
        payment.setCompany(company);

        companyRegistrationRequest = new CompanyRegistrationRequest();
        companyRegistrationRequest.setCompanyName("companyName");

        authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken("token");

        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("email");
        authenticationRequest.setPassword("password");



        timerSample = Mockito.mock(Timer.Sample.class);

        ReflectionTestUtils.setField(authenticationService, "activationUrl", activationUrl);

        lenient().when(metric.startTimer()).thenReturn(timerSample);
    }

    @Test
    void authenticate_success() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(worker);
        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(jwtService.generateToken(anyMap(), eq(worker)))
                .thenReturn("JWT-TOKEN");


        // — ACT —
        AuthenticationResponse resp = authenticationService.authenticate(authenticationRequest);

        // — ASSERT —
        assertEquals("JWT-TOKEN", resp.getToken());

        // убедимся, что метрика за успех вызвалась
        verify(metric).recordOperationTime(timerSample, "authenticate_successfully");
    }

    @Test
    void registerCompany_success() throws MessagingException {
            when(authentication.getPrincipal()).thenReturn(admin);
            when(authenticationServiceImpl.createNewCompany(companyRegistrationRequest)).thenReturn(company);

       authenticationService.registerCompany(companyRegistrationRequest, authentication);

        assertEquals("companyName", company.getCompanyName());
        verify(metric).recordOperationTime(timerSample,"register_company_successfully");
    }

    @Test
    void whenCompanyExists_thenReturnIt() {
        // — GIVEN —
        CompanyRegistrationRequest req = new CompanyRegistrationRequest();
        req.setCompanyName("Acme");

        Company existing = new Company();
        existing.setCompanyName("Acme");
        // репозиторий находит существующую компанию
        when(companyRepository.findByCompanyName("Acme"))
                .thenReturn(Optional.of(existing));

        // — WHEN —
        Company result = authenticationServiceImpl2.createNewCompany(req);

        // — THEN —
        assertEquals(existing, result);
        // маппер и save не должны вызываться
        verify(companyMapper, never()).createNewCompany(any());
        verify(companyRepository, never()).save(any());
    }

    @Test
    void registerUser_success() throws Exception {
        doNothing().when(authenticationServiceImpl).checkIfUserAlreadyExists(registrationRequest.getEmail());
        when(authenticationServiceImpl.findRoleUser()).thenReturn(userRole);
        when(authenticationServiceImpl.findCompanyByName(registrationRequest.getCompanyName())).thenReturn(company);
        when(userMapper.toWorker(registrationRequest)).thenReturn(worker);
        when(fillFormI9.generateFilledPdf(worker.getId(), company.getId()))
                .thenReturn(new byte[0]);

        when(fillFormW4.generateW4Pdf(worker.getId(), company.getId()))
                .thenReturn(new byte[0]);


        when(userRepository.save(worker)).thenReturn(worker);

        authenticationService.register(registrationRequest);

        verify(authenticationServiceImpl).checkIfUserAlreadyExists("email");
        verify(authenticationServiceImpl).findRoleUser();
        verify(authenticationServiceImpl).findCompanyByName("companyName");
        verify(userMapper).toWorker(registrationRequest);
        verify(fillFormI9).generateFilledPdf(1, 1);
        verify(fillFormW4).generateW4Pdf(1, 1);
        verify(userRepository).save(worker);
        verify(metric).recordOperationTime(timerSample,"register_successfully");

    }


    @Test
    void registerAdmin_success() throws Exception {
        doNothing().when(authenticationServiceImpl).checkIfUserAlreadyExists(adminRegRequest.getEmail());
        when(authenticationServiceImpl.findRoleAdmin()).thenReturn(adminRole);
        when(userMapper.toAdmin(adminRegRequest)).thenReturn(worker);
        when(fillFormI9.generateFilledPdf(worker.getId(), company.getId()))
                .thenReturn(new byte[0]);

        when(fillFormW4.generateW4Pdf(worker.getId(), company.getId()))
                .thenReturn(new byte[0]);


        when(userRepository.save(worker)).thenReturn(worker);

        authenticationService.registerAdmin(adminRegRequest);

        verify(authenticationServiceImpl).checkIfUserAlreadyExists("email");
        verify(authenticationServiceImpl).findRoleAdmin();
      //  verify(authenticationServiceImpl).findCompanyByName("companyName");
        verify(userMapper).toAdmin(adminRegRequest);
        verify(fillFormI9).generateFilledPdf(1, 1);
        verify(fillFormW4).generateW4Pdf(1, 1);
        verify(userRepository).save(worker);
        verify(metric).recordOperationTime(timerSample,"register_admin_successfully");
    }

    @Test
    void setPaymentDataForWorkerHoursRateAndOvertime_success(){
        when(authenticationServiceImpl.findUserById(worker.getId())).thenReturn(worker);
        when(authenticationServiceImpl.makeWorkerPayroll(worker.getBaseHourlyRate(), worker.getOvertimeRate(), worker.getId())).thenReturn(payment);
        when(workerPayrollRepository.save(payment)).thenReturn(payment);

        authenticationService.setPaymentDataForWorkerHoursRateAndOvertime(worker.getId(), paymentRequest, authentication);

        verify(authenticationServiceImpl).findUserById(worker.getId());
        verify(authenticationServiceImpl).makeWorkerPayroll(worker.getBaseHourlyRate(), worker.getOvertimeRate(), worker.getId());
        verify(workerPayrollRepository).save(payment);
        verify(metric).recordOperationTime(timerSample,"set_payment_data_for_worker_hours_rate_and_overtime");
    }








}
