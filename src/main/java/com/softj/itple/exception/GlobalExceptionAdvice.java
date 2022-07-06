package com.softj.itple.exception;

import com.softj.itple.domain.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionAdvice {

  @ExceptionHandler(ApiException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected ErrorResponse ApiServerError(ApiException e) {
    log.error("{}: {}", HttpStatus.INTERNAL_SERVER_ERROR, e);
    return buildError(ErrorCode.INTERNAL_SERVER_ERROR, e);
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  protected ErrorResponse accessDeniedException(AccessDeniedException e) {
    log.error("{}: {}", HttpStatus.UNAUTHORIZED, e);
    return buildError(ErrorCode.UNAUTHORIZED, e);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected ErrorResponse handleInternalServerError(Exception e) {
    log.error("{}: {}", HttpStatus.INTERNAL_SERVER_ERROR, e);
    return buildError(ErrorCode.INTERNAL_SERVER_ERROR, e);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    log.error("{}: {}", HttpStatus.BAD_REQUEST, e);
    final List<ErrorResponse.FieldError> fieldErrors = getFieldErrors(e.getBindingResult());
    return buildFieldErrors(ErrorCode.BAD_REQUEST, fieldErrors);
  }

  private List<ErrorResponse.FieldError> getFieldErrors(BindingResult bindingResult) {
    final List<FieldError> errors = bindingResult.getFieldErrors();
    return errors.parallelStream()
            .map(error -> ErrorResponse.FieldError.builder()
                    .reason(error.getDefaultMessage())
                    .field(error.getField())
                    .value((String) error.getRejectedValue())
                    .build())
            .collect(Collectors.toList());
  }

  private ErrorResponse buildError(ErrorCode errorCode, Exception e) {
    return ErrorResponse.builder()
            .code(errorCode.getCode())
            .status(errorCode.getStatus())
            .message(errorCode.getMessage())
            .build();
  }

  private ErrorResponse buildError(ErrorCode errorCode, ApiException e) {
    return ErrorResponse.builder()
            .code(e.getErrorCode().getCode())
            .status(e.getErrorCode().getStatus())
            .message(e.getMessage())
            .build();
  }

  private ErrorResponse buildFieldErrors(ErrorCode errorCode, List<ErrorResponse.FieldError> errors) {
    return ErrorResponse.builder()
            .code(errorCode.getCode())
            .status(errorCode.getStatus())
            .message(errorCode.getMessage())
            .errors(errors)
            .build();
  }
}
