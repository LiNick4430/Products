package com.github.lianick.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
		responseCode = "403",
		description = "權限不足，無法訪問此資源",
		content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
							{
								  "code": 403,
								  "errorCode": "ACCESS_DENIED",
								  "message": "權限不足，無法訪問此資源"
							}
                		"""
                		)
            )
		)
/**
 * 403 -> JWT 權限不足的錯誤
 * */
public @interface Api403 {

	
	
}
