package com.github.lianick.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
		summary = "使用者登錄",
		description = """
				輸入帳號密碼, 進行登入, 同時回傳 JWT 和 刷新TOKEN
				"""
		)
@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "登入成功",
				content = @Content(
		                mediaType = "application/json",
		                examples = @ExampleObject(value = """
		                				{
										  "code": 200,
										  "message": "登入成功",
										  "data": {
										    "accessToken": "accessToken",
										    "refreshToken": "refreshToken",
										    "username": "manager",
										    "roleName": "ROLE_MANAGER"
										  }
										}
		                		"""
		                		)
		            )
				),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "401",
				description = "帳號或密碼錯誤",
				content = @Content(
		                mediaType = "application/json",
		                examples = @ExampleObject(value = """
									{
										  "code": 401,
										  "errorCode": "USER_NOT_FOUND",
										  "message": "帳號或密碼錯誤"
									}
		                		"""
		                		)
		            )
				)
})
public @interface LoginApiDoc {

}
