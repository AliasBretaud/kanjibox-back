package flo.no.kanji;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Kanji App Main class
 * Launches Spring Boot configuration
 *
 * @author Florian
 */
@SpringBootApplication
@Slf4j
public class FloNoKanjiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FloNoKanjiApplication.class, args);
    }

    /**
     * Log the auto-translation feature flag value on startup
     *
     * @param autoTranslationEnabled the value of kanji.translation.auto.enable
     * @return CommandLineRunner
     */
    @Bean
    CommandLineRunner logTranslationFeatureFlag(@Value("${kanji.translation.auto.enable}") String autoTranslationEnabled) {
        return args -> {
            log.info("Auto translation activated = {}", autoTranslationEnabled);
        };
    }

}
