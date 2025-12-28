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
		responseCode = "409",
		description = "資料已被其他使用者修改，請重新讀取後再試。",
		content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
							{
								  "code": 409,
								  "errorCode": "DATA_CONFLICT",
								  "message": "資料已被其他使用者修改，請重新讀取後再試。"
							}
                		"""
                		)
            )
		)
/**
 * 409 -> 樂觀鎖 衝突錯誤
 * */
public @interface Api409 {

}
