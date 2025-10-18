package com.github.lianick.controller;

import org.springframework.http.HttpStatus;

public abstract class BaseController {
	
	protected int successStatus = HttpStatus.OK.value();		// 200

}
