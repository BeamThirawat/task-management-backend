package com.example.taskmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class StandardResponseDto<T> {

	@JsonProperty(value = "status")
    private boolean status;
	
	
    @JsonProperty(value = "code")
    private String code;

    @JsonIgnore
    private String specificErrorCode;

    @JsonProperty(value = "message")
    private String message;

    @JsonProperty(value = "data")
    private T data;


    private static final String SUCCESS_CODE = "200";
    private static final String SUCCESS_MESSAGE = "Success";

    private static final String UNAUTHORIZED = "401";
    public static final String BAD_REQUEST_CODE = "400";
    public static final String PAGE_NOT_FOUND = "404";
    private static final String INTERNAL_SERVER_ERROR_CODE = "500";
    public static final String FAIL_CODE = "E999";

    public static <T> StandardResponseDto< T > createSuccessResponse(T data) {
        return new StandardResponseDto< T >()
        	.setStatus(true)
            .setCode(SUCCESS_CODE)
            .setMessage(SUCCESS_MESSAGE)
            .setData(data);
    }

    public static <T> StandardResponseDto<T> createBadRequestResponse(String message,T data) {
        return new StandardResponseDto< T >()
        	.setStatus(false)
            .setCode(BAD_REQUEST_CODE)
            .setMessage(message)
            .setData(data);
    }

    public static <T> StandardResponseDto<T> createBadRequestResponse(String message, String code, T data) {
        return new StandardResponseDto< T >()
        		.setStatus(false)
                .setCode(code)
                .setMessage(message)
                .setData(data);
    }
    
    public static <T> StandardResponseDto<T> createBadRequestSpecificErrorCodeResponse(String message, String specificErrorCode, T data) {
        return new StandardResponseDto< T >()
        		.setStatus(false)
                .setCode(BAD_REQUEST_CODE)
                .setSpecificErrorCode(specificErrorCode)
                .setMessage(message)
                .setData(data);
    }


    public static <T> StandardResponseDto<T> createPageNotFoundResponse(String message, T data) {
        return new StandardResponseDto< T >()
        	.setStatus(false)
            .setCode(PAGE_NOT_FOUND)
            .setMessage(message)
            .setData(data);
    }

    public static <T> StandardResponseDto<T> createInternalServerErrorResponse(String message, T data) {
        return new StandardResponseDto< T >()
        	.setStatus(false)
            .setCode(INTERNAL_SERVER_ERROR_CODE)
            .setMessage(message)
            .setData(data);
    }

    public static <T> StandardResponseDto<T> createUnauthorizedResponse(String message, T data) {
        return new StandardResponseDto<T>()
        		.setStatus(false)
                .setCode(UNAUTHORIZED)
                .setMessage(message)
                .setData(data);
    }
    
    public static StandardResponseDto<Void> createFailResponse(String message) {
        return new StandardResponseDto<Void>()
        	.setStatus(false)
            .setCode(FAIL_CODE)
            .setMessage(message)
            .setData(null);
    }
    
    public static <T> StandardResponseDto<T> createFailResponse(String message, T data) {
        return new StandardResponseDto< T >()
        	.setStatus(false)
            .setCode(FAIL_CODE)
            .setMessage(message)
            .setData(data);
    }
    
    public static <T> StandardResponseDto<T> createFailResponse(String message, String code, T data) {
        return new StandardResponseDto< T >()
        	.setStatus(false)
            .setCode(code)
            .setMessage(message)
            .setData(data);
    }
}
