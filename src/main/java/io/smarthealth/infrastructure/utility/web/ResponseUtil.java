package io.smarthealth.infrastructure.utility.web;

/**
 *
 * @author Kelsas
 */
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

/**
 * Utility class for ResponseEntity creation.
 */
public interface ResponseUtil {

    static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse) {
        return wrapOrNotFound(maybeResponse, null);
    }

    static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse, HttpHeaders header) {
        return maybeResponse.map(response -> ResponseEntity.ok().headers(header).body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
