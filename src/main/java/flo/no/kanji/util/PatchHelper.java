package flo.no.kanji.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * HTTP patch method helper class
 *
 * @author Florian
 */
@Component
public class PatchHelper {

    /**
     * Jackson object mapper (JSON conversions)
     **/
    @Autowired
    private ObjectMapper mapper;

    /**
     * Merge modification using patch method
     *
     * @param <T>        Object class type
     * @param patch      JSON merge path
     * @param targetBean Object type
     * @param beanClass  Output object class
     * @return Validated updated object
     */
    public <T> T mergePatch(T targetBean, JsonNode patch, Class<T> beanClass) {
        try {
            var mergePatch = JsonMergePatch.fromJson(patch);
            var patched = mergePatch.apply(mapper.convertValue(targetBean, JsonNode.class));
            return convertAndValidate(patched, beanClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts and validates input value to output merged object
     *
     * @param <T>       Return type
     * @param jsonNode  String JSON of input object
     * @param beanClass Output bean class
     * @return Merged entity
     */
    private <T> T convertAndValidate(JsonNode jsonNode, Class<T> beanClass) {
        return mapper.convertValue(jsonNode, beanClass);
    }
}
