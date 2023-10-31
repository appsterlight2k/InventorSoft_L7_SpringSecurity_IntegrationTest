package co.inventorsoft.academy.spring.restfull.exception;

import co.inventorsoft.academy.spring.restfull.dto.WebResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    public WebResponse<Object> handelItemNotFound(ItemNotFoundException e) {
        return new WebResponse<>(null, e.getMessage(), false, 0);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public WebResponse<Object> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return new WebResponse<>(null, e.getMessage(), false, 0);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public WebResponse<Object> handleAccessDenied(AccessDeniedException e) {
        return new WebResponse<>(null, "Access denied: insufficient privileges.", false, 0);
    }
}
