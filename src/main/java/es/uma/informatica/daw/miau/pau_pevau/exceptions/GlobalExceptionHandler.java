package es.uma.informatica.daw.miau.pau_pevau.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

// Manejador de excepciones. Convierte las excepciones personalizadas en respuestas HTTP con códigos de estado y mensajes adecuados segun la especificacion de swagger

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EstudianteNoEncontradoException.class)
    public ProblemDetail handleEstudianteNoEncontrado(EstudianteNoEncontradoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(DniDuplicadoException.class)
    public ProblemDetail handleDniDuplicado(DniDuplicadoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(EstudianteBloqueadoException.class)
    public ProblemDetail handleEstudianteBloqueado(EstudianteBloqueadoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(CatalogoException.class)
    public ProblemDetail handleCatalogoException(CatalogoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(CsvLecturaException.class)
    public ProblemDetail handleCsvLecturaException(CsvLecturaException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
