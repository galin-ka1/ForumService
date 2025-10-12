package cohort_65.java.forumservice.exceptionHandler;

import cohort_65.java.forumservice.accounting.dto.exception.UserExistsException;
import cohort_65.java.forumservice.accounting.dto.exception.UserNotFoundException;
import cohort_65.java.forumservice.post.dto.exception.PostNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExecutionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
      Map<String, String> errors = new HashMap<>();
      e.getBindingResult().getFieldErrors().forEach(error->errors.put(error.getField(), error.getDefaultMessage()));
      return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    //    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<Map<String, String>> handleCustomException(RuntimeException e) {
//        return new ResponseEntity<>(Map.of("error", e.getMessage()+" "), HttpStatus.BAD_REQUEST);
//    }
    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<Map<String, String>> handleCustomException(RuntimeException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePostNotFound(PostNotFoundException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
    }
    }
