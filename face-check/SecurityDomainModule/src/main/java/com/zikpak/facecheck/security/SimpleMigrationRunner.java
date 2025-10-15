package com.zikpak.facecheck.security;
import com.zikpak.facecheck.entity.DocumentsI9;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.DocumentsI9Repository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.taxesServices.services.cryptoService.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Простая миграция существующих SSN на зашифрованное хранение
 * Запускается автоматически при профиле "migrate"
 *
 * Для запуска добавьте в .env:
 * SPRING_PROFILES_ACTIVE=dev,migrate
 *
 * После миграции уберите профиль migrate
 */
@Slf4j
@Component
@Profile("migrate")
@RequiredArgsConstructor
public class SimpleMigrationRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DocumentsI9Repository documentsI9Repository;
    private final CryptoService cryptoService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("🔐 === STARTING SSN ENCRYPTION MIGRATION ===");

        migrateUsers();
        migrateDocuments();

        log.info("✅ === MIGRATION COMPLETED SUCCESSFULLY ===");
        log.info("⚠️  ВАЖНО: Уберите профиль 'migrate' из SPRING_PROFILES_ACTIVE после миграции!");
    }

    private void migrateUsers() {
        log.info("📋 Migrating User SSNs...");

        int migrated = 0;
        int skipped = 0;

        for (User user : userRepository.findAll()) {
            // Пропускаем если уже зашифровано
            if (user.getSsnCiphertext() != null) {
                skipped++;
                continue;
            }

            // Пропускаем если нет SSN
            String oldSSN = user.getSSN_WORKER();
            if (oldSSN == null || oldSSN.isBlank()) {
                skipped++;
                continue;
            }

            try {
                // Шифруем SSN
                CryptoService.Sealed sealed = cryptoService.seal(oldSSN);
                if (sealed != null) {
                    user.setSsnCiphertext(sealed.getCiphertext());
                    user.setSsnIv(sealed.getIv());
                    user.setSsnKeyVersion(sealed.getKeyVersion());
                    user.setSsnH(sealed.getHmac());
                    user.setSsnLast4(sealed.getLast4());

                    // ВАЖНО: очищаем старое поле
                    user.setSSN_WORKER("");

                    userRepository.save(user);
                    migrated++;
                    log.info("✓ User #{} - SSN encrypted (***-**-{})",
                            user.getId(), sealed.getLast4());
                }
            } catch (Exception e) {
                log.error("❌ Failed to migrate user #{}: {}",
                        user.getId(), e.getMessage());
            }
        }

        log.info("📊 Users: {} migrated, {} skipped", migrated, skipped);
    }

    private void migrateDocuments() {
        log.info("📄 Migrating Document Numbers...");

        int migrated = 0;
        int skipped = 0;

        for (DocumentsI9 doc : documentsI9Repository.findAll()) {
            // Пропускаем если уже зашифровано
            if (doc.getDocumentNumberCiphertext() != null) {
                skipped++;
                continue;
            }

            // Пропускаем если нет номера
            String oldNumber = doc.getDocumentNumber();
            if (oldNumber == null || oldNumber.isBlank()) {
                skipped++;
                continue;
            }

            try {
                // Шифруем номер документа
                CryptoService.Sealed sealed = cryptoService.seal(oldNumber);
                if (sealed != null) {
                    doc.setDocumentNumberCiphertext(sealed.getCiphertext());
                    doc.setDocumentNumberIv(sealed.getIv());
                    doc.setDocumentNumberKeyVersion(sealed.getKeyVersion());
                    doc.setDocumentNumberH(sealed.getHmac());
                    doc.setDocumentNumberLast4(sealed.getLast4());

                    // ВАЖНО: очищаем старое поле
                    doc.setDocumentNumber("");

                    documentsI9Repository.save(doc);
                    migrated++;
                    log.info("✓ Document #{} - Number encrypted (...{})",
                            doc.getId(), sealed.getLast4());
                }
            } catch (Exception e) {
                log.error("❌ Failed to migrate document #{}: {}",
                        doc.getId(), e.getMessage());
            }
        }

        log.info("📊 Documents: {} migrated, {} skipped", migrated, skipped);
    }
}