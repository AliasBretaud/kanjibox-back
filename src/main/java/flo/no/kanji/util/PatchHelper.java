package flo.no.kanji.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import flo.no.kanji.business.exception.InvalidInputException;
import org.springframework.stereotype.Component;

/**
 * HTTP patch method helper class
 *
 * @author Florian
 */
@Component
public class PatchHelper {

    private final ObjectMapper mapper;

    public PatchHelper(ObjectMapper baseMapper) {
        this.mapper = baseMapper.copy()
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Applies a JSON Merge Patch (RFC 7396) onto the given object and returns the patched result.
     *
     * @param <T>        Object class type
     * @param targetBean Original object to patch
     * @param patch      JSON merge patch node
     * @param beanClass  Output object class
     * @return Patched object
     * @throws InvalidInputException if the patch payload is malformed
     */
    public <T> T mergePatch(T targetBean, JsonNode patch, Class<T> beanClass) {
        try {
            var mergePatch = JsonMergePatch.fromJson(patch);
            var patched = mergePatch.apply(mapper.convertValue(targetBean, JsonNode.class));
            return mapper.convertValue(patched, beanClass);
        } catch (Exception e) {
            throw new InvalidInputException("Invalid patch payload: " + e.getMessage());
        }
    }
}
