package scanner.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "Scanner already works")
public class ScannerAlreadyWorksException extends RuntimeException {
}
