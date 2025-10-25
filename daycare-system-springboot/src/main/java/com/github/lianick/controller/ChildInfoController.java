package com.github.lianick.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ChildInfoController
 * Request Mapping: "/child"
 * GET		"/find/all", "/find/all/"	尋找 民眾底下 全部幼兒資料		"/child/find/all/"			AUTHENTICATED
 * POST	"/find", "/find/"				尋找 民眾底下 特定幼兒資料		"/child/find/"				AUTHENTICATED
 * POST	"/information", "/information/"	設定 民眾底下 新幼兒資料		"/child/information/"		AUTHENTICATED
 * POST	"/update", "/update/"			更新 民眾底下 特定幼兒資料		"/child/update/"			AUTHENTICATED
 * DELETE	"/delete", "/delete/"		刪除 民眾底下 特定幼兒資料		"/child/delete/"			AUTHENTICATED
 * */

@RestController
@RequestMapping("/child")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ChildInfoController {

}
