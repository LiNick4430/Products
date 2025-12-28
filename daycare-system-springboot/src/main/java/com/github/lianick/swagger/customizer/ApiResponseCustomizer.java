package com.github.lianick.swagger.customizer;

import java.util.HashSet;
import java.util.Set;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.web.method.HandlerMethod;

import com.github.lianick.swagger.annotation.Api200401;
import com.github.lianick.swagger.annotation.Api400;
import com.github.lianick.swagger.annotation.Api403;
import com.github.lianick.swagger.annotation.Api409;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponses;

/** 依照 ApiXXX 的註解 來刪除 不需要的 HTTP CODE */
public class ApiResponseCustomizer implements OperationCustomizer{

	@Override
	public Operation customize(Operation operation, HandlerMethod handlerMethod) {

		ApiResponses responses = operation.getResponses();
		if (responses == null) {
			return operation;
		}

		// 建立空規格
		Set<String> allowed = new HashSet<String>();

		// 依照 註解添加
		if (handlerMethod.hasMethodAnnotation(Api200401.class)) {
			allowed.addAll(Set.of("200", "401"));
		}
		
		if (handlerMethod.hasMethodAnnotation(Api400.class)) {
			allowed.addAll(Set.of("400"));
		}

		if (handlerMethod.hasMethodAnnotation(Api403.class)) {
			allowed.addAll(Set.of("403"));
		}

		if (handlerMethod.hasMethodAnnotation(Api409.class)) {
			allowed.addAll(Set.of("409"));
		}
		
		// 假設 沒有添加註解 按照預設行為
		if (allowed.isEmpty()) {
			return operation;
		}

		// 依照添加的註解 刪除掉 沒有在裡面的
		responses.keySet().removeIf(code -> !allowed.contains(code));
		return operation;
	}

}
