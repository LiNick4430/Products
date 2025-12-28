package com.github.lianick.swagger.template;

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
		summary = "",
		description = """
				
				"""
		)
@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "說明文",
				content = @Content(
		                mediaType = "application/json",
		                examples = @ExampleObject(value = """
		                				{
										  "code": 200,
										  "message": "說明文",
										  "data": {
										    
										  }
										}
		                		"""
		                		)
		            )
				),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "401",
				description = "說明文",
				content = @Content(
		                mediaType = "application/json",
		                examples = @ExampleObject(value = """
									{
										  "code": 401,
										  "errorCode": "錯誤代碼",
										  "message": "帳號或密碼錯誤"
									}
		                		"""
		                		)
		            )
				)
})
public @interface Template {

}
