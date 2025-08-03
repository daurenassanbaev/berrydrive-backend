package kz.berrydrive.common.exception.handler;

import kz.berrydrive.common.dto.ErrorResponseDto;
import kz.berrydrive.common.exception.AlreadyExistsException;
import kz.berrydrive.common.exception.NotFoundException;
import kz.berrydrive.file.exception.FileStorageException;
import kz.berrydrive.auth.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(Exception e, WebRequest request, HttpStatus status) {
        String path = request.getDescription(false).replace("uri=", "");
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                path,
                status,
                e.getMessage(),
                LocalDateTime.now()
        );
        log.error("Handled exception: {}", e.getMessage(), e);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(NotFoundException e, WebRequest request) {
        return buildErrorResponse(e, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyExists(AlreadyExistsException e, WebRequest request) {
        return buildErrorResponse(e, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponseDto> handleFileStorage(FileStorageException e, WebRequest request) {
        return buildErrorResponse(e, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({UnauthorizedException.class, AuthenticationServiceException.class})
    public ResponseEntity<ErrorResponseDto> handleUnauthorized(Exception e, WebRequest request) {
        return buildErrorResponse(e, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({S3Exception.class, NoSuchKeyException.class})
    public ResponseEntity<ErrorResponseDto> handleS3(Exception e, WebRequest request) {
        return buildErrorResponse(e, request, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException e, WebRequest request) {
        return buildErrorResponse(e, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntime(RuntimeException e, WebRequest request) {
        return buildErrorResponse(e, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneral(Exception e, WebRequest request) {
        return buildErrorResponse(e, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
