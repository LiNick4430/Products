package com.github.lianick.converter;

import java.util.List;
import java.util.Set;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import com.github.lianick.model.eneity.Cases;


/**
 * ModelMapper Converter: 將 Set<Cases> 轉換為 List<String> (僅包含案件編號)。
 * 用於 DocumentPublic Entity 到 DocumentPublicDTO 的映射。
 */

@Component
public class CaseSetToCaseNumberListConverter extends AbstractConverter<Set<Cases>, List<String>> {

	@Override
	protected List<String> convert(Set<Cases> source) {
		if (source == null) {
			return List.of();	// 返回空列表，避免 NullPointerException
		}
		
		return source.stream()
				.map(Cases::getCaseNumber)
				.toList();
	}		
}
