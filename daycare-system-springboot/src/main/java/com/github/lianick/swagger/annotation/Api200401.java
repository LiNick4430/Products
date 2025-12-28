package com.github.lianick.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
		@ApiResponse(
				responseCode = "200",
				description = "成功"
				),
		@ApiResponse(
				responseCode = "401",
				description = "失敗"
				)
})
/**
 * 200 -> 成功
 * 401 -> 各 服務的 錯誤
 * */
public @interface Api200401 {

}
