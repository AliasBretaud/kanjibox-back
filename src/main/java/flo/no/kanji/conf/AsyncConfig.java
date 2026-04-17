package flo.no.kanji.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Thread pool executor for async translation and kanji enrichment tasks.
 * Isolates DeepL and dictionary calls from the JVM common ForkJoinPool.
 */
@Configuration
public class AsyncConfig {

    @Bean("translationExecutor")
    public Executor translationExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("translation-");
        executor.initialize();
        return executor;
    }
}
