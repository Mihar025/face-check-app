package com.zikpak.conf.tomCat;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Tomcat для жесткого ограничения размера запросов
 * Это решит проблему с 500 ошибкой для больших payload
 */
@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(1048576); // 1MB максимум для POST

            if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>) {
                AbstractHttp11Protocol<?> protocol = (AbstractHttp11Protocol<?>) connector.getProtocolHandler();

                // КРИТИЧЕСКИ ВАЖНО: Устанавливаем максимальный размер на уровне протокола
                protocol.setMaxSwallowSize(1048576); // 1MB
                protocol.setMaxHttpHeaderSize(8192); // 8KB для headers

                // Отклоняем запросы больше 1MB сразу на уровне Tomcat
                protocol.setMaxSavePostSize(1048576);

                // Таймаут для больших запросов
                protocol.setConnectionTimeout(5000);
                protocol.setKeepAliveTimeout(5000);

                // Отключаем сжатие для безопасности
                protocol.setCompression("off");
            }
        });
    }
}