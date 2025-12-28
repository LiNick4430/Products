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
		responseCode = "400",
		description = "缺少特定的數值",
		content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
							{
								  "code": 400,
								  "errorCode": "VALUES_MISS",
								  "message": "缺少特定的數值"
							}
                		"""
                		)
            )
		)
/**
 * 400 -> 數值/格式/JSON轉換 錯誤
 * */
public @interface Api400 {

}
