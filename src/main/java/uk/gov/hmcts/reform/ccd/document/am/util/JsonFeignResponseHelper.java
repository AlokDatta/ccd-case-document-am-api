package uk.gov.hmcts.reform.ccd.document.am.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.hmcts.reform.ccd.document.am.model.StoredDocumentHalResource;

@SuppressWarnings("unchecked")
public class JsonFeignResponseHelper {
    private static final ObjectMapper json = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonFeignResponseHelper() {
    }

    public static Optional decode(Response response, Class clazz) {
        try {
            return Optional.of(json.readValue(response.body().asReader(), clazz));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static ResponseEntity toResponseEntity(Response response, Class clazz, UUID documentId) {
        Optional payload = decode(response, clazz);
        addHateoasLinks(payload,documentId);

        return new ResponseEntity(
            payload.orElse(null),
            convertHeaders(response.headers()),
            HttpStatus.valueOf(response.status()));
    }

    public static MultiValueMap<String, String> convertHeaders(Map<String, Collection<String>> responseHeaders) {
        MultiValueMap<String, String> responseEntityHeaders = new LinkedMultiValueMap<>();
        responseHeaders.entrySet().stream().forEach(e -> {
            if (!(e.getKey().equalsIgnoreCase("request-context") || e.getKey().equalsIgnoreCase("x-powered-by"))) {
                responseEntityHeaders.put(e.getKey(), new ArrayList<>(e.getValue()));
            }
        });

        return responseEntityHeaders;
    }

    public static void addHateoasLinks(Optional payload,UUID documentId) {
        if (payload.isPresent()) {
            Object obj = payload.get();
            if (obj instanceof StoredDocumentHalResource) {
                ((StoredDocumentHalResource) obj).addLinks(documentId);
            }

        }

    }
}
