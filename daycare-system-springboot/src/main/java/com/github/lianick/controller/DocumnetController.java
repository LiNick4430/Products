package com.github.lianick.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DocumnetController
 * Request Mapping: "/document"
 * GET 		"/public/find/all", "/public/find/all/"			尋找 民眾底下 全部的附件		"/document/public/find/all/"	AUTHENTICATED
 * POST 	"/public/find", "/public/find/"					尋找 民眾底下 特定的附件		"/document/public/find/"		AUTHENTICATED
 * POST 	"/public/upload", "/public/upload/"				民眾 建立 新的附件			"/document/public/upload/"		AUTHENTICATED
 * POST 	"/public/link/case", "/public/link/case/"		民眾 將 附件 和 案件 建立 關連 	"/document/public/link/case/"	AUTHENTICATED
 * POST 	"/public/unlink/case", "/public/unlink/case/"	民眾 將 附件 和 案件 取消 關聯 	"/document/public/unlink/case/"	AUTHENTICATED
 * DELETE	"/public/delete", "/public/delete/"				民眾 刪除 特定的附件			"/document/public/delete/"		AUTHENTICATED
 * POST 	"/case/upload", "/case/upload/"					案件 建立 新的附件			"/document/case/upload/"		AUTHENTICATED
 * POST 	"/case/link/public", "/case/link/public/"		案件 將 附件 和 民眾 建立 關連 	"/document/case/link/public/"	AUTHENTICATED
 * DELETE	"/case/delete", "/case/delete/"					案件 刪除 特定的附件			"/document/case/delete/"		AUTHENTICATED
 * POST		"/admin/check", "/admin/check/"					員工 審核 特定民眾 的 特定的附件	"/document/admin/check/"		AUTHENTICATED
 * */

@RestController
@RequestMapping("/document")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class DocumnetController {

}
