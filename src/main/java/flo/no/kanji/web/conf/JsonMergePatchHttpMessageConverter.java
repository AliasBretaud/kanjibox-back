package flo.no.kanji.web.conf;

import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

/**
 * Converter class for JSON merge HTTP method
 * @author Florian
 */
@Component
public class JsonMergePatchHttpMessageConverter extends AbstractHttpMessageConverter<JsonMergePatch> {

	/** Default constructor */
    public JsonMergePatchHttpMessageConverter() {
        super(MediaType.valueOf("application/merge-patch+json"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean supports(Class<?> clazz) {
        return JsonMergePatch.class.isAssignableFrom(clazz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JsonMergePatch readInternal(Class<? extends JsonMergePatch> clazz, HttpInputMessage inputMessage)
            throws HttpMessageNotReadableException {

        try (JsonReader reader = Json.createReader(inputMessage.getBody())) {
            return Json.createMergePatch(reader.readValue());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeInternal(JsonMergePatch jsonMergePatch, HttpOutputMessage outputMessage)
            throws HttpMessageNotWritableException {

        try (JsonWriter writer = Json.createWriter(outputMessage.getBody())) {
            writer.write(jsonMergePatch.toJsonValue());
        } catch (Exception e) {
            throw new HttpMessageNotWritableException(e.getMessage(), e);
        }
    }
    
}