package com.github.lianick.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.AnnouncementFailureException;
import com.github.lianick.exception.CaseFailureException;
import com.github.lianick.exception.ChildNoFoundException;
import com.github.lianick.exception.ClassesFailureException;
import com.github.lianick.exception.FileStorageException;
import com.github.lianick.exception.OrganizationFailureException;
import com.github.lianick.exception.RegulationFailureException;
import com.github.lianick.exception.RoleFailureException;
import com.github.lianick.exception.TokenFailureException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.model.eneity.Announcements;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.Classes;
import com.github.lianick.model.eneity.DocumentAdmin;
import com.github.lianick.model.eneity.DocumentPublic;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.RefreshToken;
import com.github.lianick.model.eneity.Regulations;
import com.github.lianick.model.eneity.Role;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.UserVerify;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.AnnouncementsRepository;
import com.github.lianick.repository.CasesRepository;
import com.github.lianick.repository.ChildInfoRepository;
import com.github.lianick.repository.ClassesRepository;
import com.github.lianick.repository.DocumentAdminRepository;
import com.github.lianick.repository.DocumentPublicRepository;
import com.github.lianick.repository.OrganizationRepository;
import com.github.lianick.repository.RefreshTokenRepository;
import com.github.lianick.repository.RegulationsRepository;
import com.github.lianick.repository.RoleRepository;
import com.github.lianick.repository.UserAdminRepository;
import com.github.lianick.repository.UserPublicRepository;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.repository.UsersVerifyRepository;

/**
 * 負責處理 各種獲取 Entity 的方法 (含 拋出錯誤) (不包含從 JWT)
 * */
@Service
public class EntityFetcher {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UserPublicRepository userPublicRepository;
	
	@Autowired
	private UserAdminRepository userAdminRepository;
	
	@Autowired
	private UsersVerifyRepository usersVerifyRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private CasesRepository casesRepository;
	
	@Autowired
	private DocumentPublicRepository documentPublicRepository;
	
	@Autowired
	private AnnouncementsRepository announcementsRepository;
	
	@Autowired
	private DocumentAdminRepository documentAdminRepository;
	
	@Autowired
	private ClassesRepository classesRepository;
	
	@Autowired
	private ChildInfoRepository childInfoRepository;
	
	@Autowired
	private RegulationsRepository regulationsRepository;
	
	/**
	 * 使用 username 獲取 Users
	 * */
	public Users getUsersByUsername(String username) {
		Users tableUser = usersRepository.findByAccount(username)
		        .orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
		return tableUser;
	}
	
	/**
	 * 使用 username 獲取 UserPublic
	 * */
	public UserPublic getUsersPublicByUsername(String username) {
		Users tableUser = getUsersByUsername(username);
		
		UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("帳號錯誤"));
		return userPublic;
	}
	
	/**
	 * 使用 Users 獲取 UserPublic
	 * */
	public UserPublic getUsersPublicByUser(Users user) {
		UserPublic userPublic = userPublicRepository.findByUsers(user)
				.orElseThrow(() -> new UserNoFoundException("帳號錯誤"));
		return userPublic;
	}
	
	/**
	 * 使用 username 找到 userAdmin
	 * */
	public UserAdmin getUserAdminByUsername(String username) {
		Users tableUser = getUsersByUsername(username);
		
		UserAdmin userAdmin = userAdminRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("用戶存在，但非員工帳號"));
		return userAdmin;
	}
	
	/**
	 * 使用 token 獲取 UserVerify
	 * */
	public UserVerify getUserVerifyByToken(String token) {
		UserVerify userVerify = usersVerifyRepository.findByToken(token)
				.orElseThrow(() -> new TokenFailureException("驗證碼 無效或不存在"));
		return userVerify;
	}
	
	/**
	 * 使用 DocumentPublicId 獲取 DocumentPublic
	 * */
	public DocumentPublic getDocumentPublicById(Long id) {
		DocumentPublic documentPublic = documentPublicRepository.findById(id)
				.orElseThrow(() -> new FileStorageException("查無附件"));
		return documentPublic;
	}
	
	/**
	 * 使用 ChildInfoId 獲取 ChildInfo
	 * */
	public ChildInfo getChildInfoById(Long id) {
		ChildInfo childInfo = childInfoRepository.findById(id)
				.orElseThrow(() -> new ChildNoFoundException("查無幼兒資料"));
		return childInfo;
	}
	
	/**
	 * 使用 OrganizationId 獲取 Organization
	 * */
	public Organization getOrganizationById(Long id) {
		Organization organization = organizationRepository.findById(id)
				.orElseThrow(() -> new OrganizationFailureException("查無機構"));
		return organization;
	}
	
	/**
	 * 使用 OrganizationId 獲取 Organization 附加 錯誤訊息
	 * */
	public Organization getOrganizationById(Long id, String message) {
		Organization organization = organizationRepository.findById(id)
				.orElseThrow(() -> new OrganizationFailureException(message));
		return organization;
	}
	
	/**
	 * 使用 AnnouncementsId 獲取 Announcements
	 * */
	public Announcements getAnnouncementsById(Long id) {
		Announcements announcements = announcementsRepository.findById(id)
				.orElseThrow(() -> new AnnouncementFailureException("查無公告"));
		return announcements;
	}
	
	/**
	 * 使用 AnnouncementsId 和 now 獲取 Announcements
	 * */
	public Announcements getAnnouncementsByIdAndNow(Long id, LocalDateTime now) {
		Announcements announcements = announcementsRepository.findAllById(id, now)
				.orElseThrow(() -> new AnnouncementFailureException("公告已過期"));
		return announcements;
	}
	
	/**
	 * 使用 CaseId 獲取 Case
	 * */
	public Cases getCasesById(Long id) {
		Cases cases = casesRepository.findById(id)
				.orElseThrow(() -> new CaseFailureException("查無案件"));
		return cases;
	}
	
	/**
	 * 使用 DocumentAdminId 和 OrganizationId 獲取 DocumentAdmin
	 * */
	public DocumentAdmin getDocumentAdminByIdAndOrganizationId(Long documentAdminId, Long organizationId) {
		DocumentAdmin documentAdmin = documentAdminRepository.findByIdAndOrganizationId(documentAdminId, organizationId)
				.orElseThrow(() -> new FileStorageException("查無附件"));
		return documentAdmin;
	}
	
	/**
	 * 使用 DocumentAdminId 和 AnnouncementId 獲取 DocumentAdmin
	 * */
	public DocumentAdmin getDocumentAdminByIdAndAnnouncementId(Long documentAdminId, Long announcementId) {
		DocumentAdmin documentAdmin = documentAdminRepository.findByIdAndAnnouncementId(documentAdminId, announcementId)
				.orElseThrow(() -> new FileStorageException("查無附件"));
		return documentAdmin;
	}
	
	/**
	 * 使用 ClassId 獲取 Classes
	 * */
	public Classes getClassesById(Long id) {
		Classes classes = classesRepository.findById(id)
				.orElseThrow(() -> new ClassesFailureException("查無班級"));
		return classes;
	}
	
	/**
	 * 使用 RegulationId 獲取 Regulations
	 * */
	public Regulations getRegulationsById(Long id) {
		Regulations regulations = regulationsRepository.findById(id)
				.orElseThrow(() -> new RegulationFailureException("查無規範"));
		return regulations;
	}
	
	/**
	 * 使用 RoleId 獲取 Role
	 * */
	public Role getRoleById(Long id) {
		Role role = roleRepository.findById(id)
			.orElseThrow(() -> new RoleFailureException("查無角色"));
		return role;
	}
	
	/**
	 * 使用 RoleId 獲取 Role 附加 錯誤訊息
	 * */
	public Role getRoleById(Long id, String message) {
		Role role = roleRepository.findById(id)
			.orElseThrow(() -> new RoleFailureException(message));
		return role;
	}
	
	/**
	 * 使用 token 獲取 RefreshToken 附加 錯誤訊息
	 * */
	public RefreshToken getRefreshTokenByToken(String token, String message) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
				.orElseThrow(() -> new TokenFailureException(message));
		return refreshToken;
	}
	
}
