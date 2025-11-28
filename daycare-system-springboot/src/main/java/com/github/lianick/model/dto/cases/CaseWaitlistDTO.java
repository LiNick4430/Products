package com.github.lianick.model.dto.cases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaseWaitlistDTO {

	private Long id;
    private Long organizationId; // 標示是哪個機構的備取名單（1st 或 2nd）
}
