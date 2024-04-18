package flo.no.kanji.conf;

import com.moji4j.MojiConverter;
import com.moji4j.MojiDetector;
import io.github.aliasbretaud.mojibox.dictionary.KanjiDictionary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Injects converters for Japanese translations util beans
 *
 * @author Florian
 */
@Configuration
public class CharactersConvertersConfig {

    /**
     * Creates default Moji converter
     *
     * @return Moji converter
     */
    @Bean
    public MojiConverter getMojiConverter() {
        return new MojiConverter();
    }

    @Bean
    public MojiDetector getMojiDetector() {
        return new MojiDetector();
    }

    @Bean
    public KanjiDictionary getKanjiDictionary() {
        return new KanjiDictionary();
    }
}
