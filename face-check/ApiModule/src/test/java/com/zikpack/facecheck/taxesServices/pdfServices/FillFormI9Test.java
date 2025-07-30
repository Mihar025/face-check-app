package com.zikpack.facecheck.taxesServices.pdfServices;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.DocumentsI9;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.DocumentsI9Repository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.pdfServices.FillFormI9;
import io.micrometer.core.instrument.Timer;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FillFormI9Test {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DocumentsI9Repository documentsI9Repository;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private MetricsForPdfServices metric;

    @Mock
    private Timer.Sample timerSample;

    @InjectMocks
    private FillFormI9 fillFormI9;

    private User testUser;
    private Company testCompany;
    private User companyOwner;
    private List<DocumentsI9> testDocuments;

    @BeforeEach
    void setUp() {
        // Создание тестового владельца компании
        companyOwner = new User();
        companyOwner.setId(999);
        companyOwner.setFirstName("John");
        companyOwner.setLastName("Owner");

        // Создание тестовой компании
        testCompany = new Company();
        testCompany.setId(1);
        testCompany.setCompanyName("Test Company LLC");
        testCompany.setCompanyAddress("123 Business St");
        testCompany.setCompanyCity("New York");
        testCompany.setCompanyState("NY");
        testCompany.setCompanyZipCode("10001");
        testCompany.setCompanyOwner(companyOwner);

        // Создание тестового пользователя - гражданин США
        testUser = createTestUser();

        // Создание тестовых документов
        testDocuments = createTestDocuments();

        // Настройка мока для метрик
        when(metric.startTimer()).thenReturn(timerSample);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setMiddleInitial("M");
        user.setHomeAddress("456 Main St");
        user.setApt("Apt 2B");
        user.setCity("Brooklyn");
        user.setState("NY");
        user.setZipcode("11201");
        user.setPhoneNumber("555-123-4567");
        user.setSSN_WORKER("123456789");
        user.setDateOfBirth(LocalDate.of(1990, 5, 15));
        user.setEmail("jane.doe@email.com");
        user.setHireDate(LocalDate.of(2024, 1, 15));
        user.setIsCitizen(true);
        user.setIsNonCitizenNationalOfTheUS(false);
        user.setIsPermanentResident(false);
        user.setIsANonCitizen(false);
        user.setIsRehired(false);
        return user;
    }

    private List<DocumentsI9> createTestDocuments() {
        DocumentsI9 doc1 = new DocumentsI9();
        doc1.setId(1);
        doc1.setDocumentTitle("U.S. Passport");
        doc1.setIssuingAuthority("U.S. Department of State");
        doc1.setDocumentNumber("123456789");
        doc1.setExpirationDate(LocalDate.of(2030, 12, 31));

        DocumentsI9 doc2 = new DocumentsI9();
        doc2.setId(2);
        doc2.setDocumentTitle("Driver's License");
        doc2.setIssuingAuthority("NY DMV");
        doc2.setDocumentNumber("DL123456");
        doc2.setExpirationDate(LocalDate.of(2028, 6, 30));

        return Arrays.asList(doc1, doc2);
    }

    @Test
    void generateFilledPdf_Success_CitizenUser() throws IOException {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(documentsI9Repository.findAllDocumentsByUserId(1)).thenReturn(testDocuments);

        // Act
        byte[] result = fillFormI9.generateFilledPdf(1, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);

        verify(metric).recordRequest("I9");
        verify(metric).startTimer();
        verify(amazonS3Service).uploadPdfToS3(eq(result), contains("Test_Company_LLC/1/I9Form/Jane_Doe/I9_1_jane_doe.pdf"));
        verify(metric).recordGenerated("I9", true);
        verify(metric).recordS3UploadTime(eq("I9"), eq(true), anyLong());
        verify(metric).recordOperationTime(timerSample, "I9_success");
    }

    @Test
    void generateFilledPdf_Success_PermanentResident() throws IOException {
        // Arrange
        testUser.setIsCitizen(false);
        testUser.setIsPermanentResident(true);
        testUser.setUscisNumber("A123456789");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(documentsI9Repository.findAllDocumentsByUserId(1)).thenReturn(testDocuments);

        // Act
        byte[] result = fillFormI9.generateFilledPdf(1, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metric).recordGenerated("I9", true);
    }

    @Test
    void generateFilledPdf_Success_NonCitizen() throws IOException {
        // Arrange
        testUser.setIsCitizen(false);
        testUser.setIsANonCitizen(true);
        testUser.setWorkAuthrizationExpiryDate(LocalDate.of(2025, 12, 31));
        testUser.setUscisNumber("A987654321");
        testUser.setFormI94AdmissionNumber("I94123456");
        testUser.setPassportNumber("P123456789");
        testUser.setPassportCountryOfIssuance("Canada");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(documentsI9Repository.findAllDocumentsByUserId(1)).thenReturn(testDocuments);

        // Act
        byte[] result = fillFormI9.generateFilledPdf(1, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metric).recordGenerated("I9", true);
    }

    @Test
    void generateFilledPdf_Success_RehiredEmployee() throws IOException {
        // Arrange
        testUser.setIsRehired(true);
        testUser.setDateWhenRehired(LocalDate.of(2024, 6, 1));

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(documentsI9Repository.findAllDocumentsByUserId(1)).thenReturn(testDocuments);

        // Act
        byte[] result = fillFormI9.generateFilledPdf(1, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metric).recordGenerated("I9", true);
    }

    @Test
    void generateFilledPdf_Success_UserWithNullFields() throws IOException {
        // Arrange
        testUser.setMiddleInitial(null);
        testUser.setApt("");  // Используем пустую строку вместо null
        testUser.setHireDate(null);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(documentsI9Repository.findAllDocumentsByUserId(1)).thenReturn(testDocuments);

        // Act
        byte[] result = fillFormI9.generateFilledPdf(1, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metric).recordGenerated("I9", true);
    }

    @Test
    void generateFilledPdf_Success_EmptyDocumentsList() throws IOException {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(documentsI9Repository.findAllDocumentsByUserId(1)).thenReturn(Arrays.asList());

        // Act
        byte[] result = fillFormI9.generateFilledPdf(1, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metric).recordGenerated("I9", true);
    }

    @Test
    void generateFilledPdf_UserNotFound() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            fillFormI9.generateFilledPdf(1, 1);
        });

        assertEquals("User not found", exception.getMessage());
        verify(metric).recordRequest("I9");
        verify(metric).startTimer();
        verify(metric).recordOperationTime(timerSample, "I9_failed");
        verify(metric).recordGenerated("I9", false);
        verify(metric).recordError(eq("I9_failed"), eq("User not found"), any(EntityNotFoundException.class));
        verifyNoInteractions(amazonS3Service);
    }

    @Test
    void generateFilledPdf_CompanyNotFound() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            fillFormI9.generateFilledPdf(1, 1);
        });

        assertEquals("Company not found", exception.getMessage());
        verify(metric).recordRequest("I9");
        verify(metric).startTimer();
        verify(metric).recordOperationTime(timerSample, "I9_failed");
        verify(metric).recordGenerated("I9", false);
        verify(metric).recordError(eq("I9_failed"), eq("Company not found"), any(EntityNotFoundException.class));
        verifyNoInteractions(amazonS3Service);
    }

    @Test
    void generateFilledPdf_S3UploadFailure() throws Exception {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(documentsI9Repository.findAllDocumentsByUserId(1)).thenReturn(testDocuments);

        // Проверяем сигнатуру метода - возможно он не throws IOException
        RuntimeException s3Exception = new RuntimeException("S3 upload failed");
        doThrow(s3Exception).when(amazonS3Service).uploadPdfToS3(any(byte[].class), anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fillFormI9.generateFilledPdf(1, 1);
        });

        assertEquals("S3 upload failed", exception.getMessage());
        verify(metric).recordRequest("I9");
        verify(metric).startTimer();
        verify(metric).recordOperationTime(timerSample, "I9_failed");
        verify(metric).recordGenerated("I9", false);
        verify(metric).recordError(eq("I9_failed"), eq("S3 upload failed"), eq(s3Exception));
    }

    @Test
    void generateFilledPdf_Success_NonCitizenNational() throws IOException {
        // Arrange
        testUser.setIsCitizen(false);
        testUser.setIsNonCitizenNationalOfTheUS(true);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(documentsI9Repository.findAllDocumentsByUserId(1)).thenReturn(testDocuments);

        // Act
        byte[] result = fillFormI9.generateFilledPdf(1, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metric).recordGenerated("I9", true);
    }

    @Test
    void generateFilledPdf_Success_MultipleDocuments() throws IOException {
        // Arrange
        DocumentsI9 doc3 = new DocumentsI9();
        doc3.setId(3);
        doc3.setDocumentTitle("Employment Authorization Document");
        doc3.setIssuingAuthority("USCIS");
        doc3.setDocumentNumber("EAD123456");
        doc3.setExpirationDate(LocalDate.of(2026, 3, 15));

        List<DocumentsI9> threeDocuments = Arrays.asList(testDocuments.get(0), testDocuments.get(1), doc3);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(documentsI9Repository.findAllDocumentsByUserId(1)).thenReturn(threeDocuments);

        // Act
        byte[] result = fillFormI9.generateFilledPdf(1, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metric).recordGenerated("I9", true);
    }

    @Test
    void generateFilledPdf_Success_DocumentsWithNullValues() throws IOException {
        // Arrange
        DocumentsI9 docWithNulls = new DocumentsI9();
        docWithNulls.setId(1);
        docWithNulls.setDocumentTitle("");  // Пустые строки вместо null
        docWithNulls.setIssuingAuthority("");
        docWithNulls.setDocumentNumber("");
        docWithNulls.setExpirationDate(null);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(documentsI9Repository.findAllDocumentsByUserId(1)).thenReturn(Arrays.asList(docWithNulls));

        // Act
        byte[] result = fillFormI9.generateFilledPdf(1, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metric).recordGenerated("I9", true);
    }

    @Test
    void generateFilledPdf_Success_RehiredWithNullDates() throws IOException {
        // Arrange
        testUser.setIsRehired(true);
        testUser.setDateWhenRehired(null); // null date for rehired user

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(documentsI9Repository.findAllDocumentsByUserId(1)).thenReturn(testDocuments);

        // Act
        byte[] result = fillFormI9.generateFilledPdf(1, 1);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metric).recordGenerated("I9", true);
    }

    @Test
    void generateFilledPdf_VerifyMetricsRecording() throws IOException {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(documentsI9Repository.findAllDocumentsByUserId(1)).thenReturn(testDocuments);

        // Act
        fillFormI9.generateFilledPdf(1, 1);

        // Assert - проверяем все вызовы метрик
        verify(metric, times(1)).recordRequest("I9");
        verify(metric, times(1)).startTimer();
        verify(metric, times(1)).recordGenerated("I9", true);
        verify(metric, times(1)).recordS3UploadTime(eq("I9"), eq(true), anyLong());
        verify(metric, times(1)).recordOperationTime(timerSample, "I9_success");

        // Проверяем, что ошибки не записывались
        verify(metric, never()).recordError(anyString(), anyString(), any(Exception.class));
        verify(metric, never()).recordOperationTime(timerSample, "I9_failed");
    }

}