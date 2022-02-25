package flo.no.kanji.util;

import javax.json.JsonMergePatch;
import javax.json.JsonValue;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * HTTP patch method helper class
 * 
 * @author Florian
 *
 */
@Component
public class PatchHelper {

	/** Jackson object mapper (JSON conversions) **/
    private final ObjectMapper mapper;

    /** Default constructor **/
    public PatchHelper() {
        this.mapper = new ObjectMapper()
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .findAndRegisterModules();
    }

    /**
     * Merge modification using patch method
     * 
     * @param <T>
     * 			Object class type
     * @param mergePatch
     * 			JSON merge path
     * @param targetBean
     * 			Object type
     * @param beanClass
     * 			Output object class
     * @return
     * 			Validated updated object
     */
    public <T> T mergePatch(JsonMergePatch mergePatch, T targetBean, Class<T> beanClass) {
        JsonValue target = mapper.convertValue(targetBean, JsonValue.class);
        JsonValue patched = applyMergePatch(mergePatch, target);
        return convertAndValidate(patched, beanClass);
    }
    
    /**
     * Update JSON merge to actual object
     * 
     * @param mergePatch
     * 			JSON merge path
     * @param target
     * 			Target object
     * @return
     * 			Merged object
     */
    private JsonValue applyMergePatch(JsonMergePatch mergePatch, JsonValue target) {
        try {
            return mergePatch.apply(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T convertAndValidate(JsonValue jsonValue, Class<T> beanClass) {
        T bean = mapper.convertValue(jsonValue, beanClass);
        return bean;
    }
}
