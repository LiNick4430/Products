package com.github.lianick.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.lianick.converter.AnnouncementToAnnouncementIdConveter;
import com.github.lianick.converter.BaseEnumToDescriptionConveter;
import com.github.lianick.converter.CaseOrganizationSetToOrganizationNameFirstConveter;
import com.github.lianick.converter.CaseOrganizationSetToOrganizationNameSecondConveter;
import com.github.lianick.converter.CaseOrganizationSetToOrganizationStatusFirstConveter;
import com.github.lianick.converter.CaseOrganizationSetToOrganizationStatusSecondConveter;
import com.github.lianick.converter.CaseSetToCaseNumberListConverter;
import com.github.lianick.converter.ChildInfoToChildNameConveter;
import com.github.lianick.converter.ClassToClassNameConveter;
import com.github.lianick.converter.OrganizationToOrganizationIdConveter;
import com.github.lianick.converter.OrganizationToOrganizationNameConveter;
import com.github.lianick.converter.RoleNumberToRoleConveter;
import com.github.lianick.converter.UsersPublicToUseIdConveter;
import com.github.lianick.converter.UsersToUsernameConveter;
import com.github.lianick.model.dto.announcement.AnnouncementDTO;
import com.github.lianick.model.dto.cases.CaseDTO;
import com.github.lianick.model.dto.child.ChildCreateDTO;
import com.github.lianick.model.dto.child.ChildDTO;
import com.github.lianick.model.dto.child.ChildUpdateDTO;
import com.github.lianick.model.dto.clazz.ClassDTO;
import com.github.lianick.model.dto.documentAdmin.DocumentAnnouncementDTO;
import com.github.lianick.model.dto.documentAdmin.DocumentOrganizationDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicDTO;
import com.github.lianick.model.dto.organization.OrganizationDTO;
import com.github.lianick.model.dto.regulation.RegulationDTO;
import com.github.lianick.model.dto.user.UserForgetPasswordDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;
import com.github.lianick.model.dto.user.UserMeDTO;
import com.github.lianick.model.dto.user.UserRegisterDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.model.dto.user.UserVerifyDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminDTO;
import com.github.lianick.model.dto.userPublic.UserPublicDTO;
import com.github.lianick.model.dto.userPublic.UserPublicUpdateDTO;
import com.github.lianick.model.dto.userPublic.UserPublicCreateDTO;
import com.github.lianick.model.eneity.Announcements;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.Classes;
import com.github.lianick.model.eneity.DocumentAdmin;
import com.github.lianick.model.eneity.DocumentPublic;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Regulations;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.Users;

@Configuration
public class ModelMapperConfig {

	// Conveter 們
	@Autowired
    private RoleNumberToRoleConveter roleTypeToRoleConveter;
	
	@Autowired
	private UsersToUsernameConveter usersToUsernameConveter;
	
	@Autowired
	private UsersPublicToUseIdConveter usersPublicToUseIdConveter;
	
	@Autowired
	private OrganizationToOrganizationIdConveter organizationToOrganizationIdConveter;
	
	@Autowired
	private OrganizationToOrganizationNameConveter organizationToOrganizationNameConveter;
	
	@Autowired
	private CaseSetToCaseNumberListConverter caseSetToCaseNumberListConverter;
	
	@Autowired
	private AnnouncementToAnnouncementIdConveter announcementToAnnouncementIdConveter;
	
	@Autowired
	private ClassToClassNameConveter classToClassNameConveter;
	
	@Autowired
	private CaseOrganizationSetToOrganizationNameFirstConveter caseOrganizationSetToOrganizationNameFirstConveter;
	
	@Autowired
	private CaseOrganizationSetToOrganizationNameSecondConveter caseOrganizationSetToOrganizationNameSecondConveter;
	
	@Autowired
	private CaseOrganizationSetToOrganizationStatusFirstConveter caseOrganizationSetToOrganizationStatusFirstConveter;
	
	@Autowired
	private CaseOrganizationSetToOrganizationStatusSecondConveter caseOrganizationSetToOrganizationStatusSecondConveter;
	
	@Autowired
	private ChildInfoToChildNameConveter childInfoToChildNameConveter;
	
	@Autowired
	private BaseEnumToDescriptionConveter baseEnumToDescriptionConveter;
	
	// 主要 邏輯
	@Bean	// @Bean 預設 Public
	ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		
		// Entity -> DTO
		// User 相關
		modelMapper.typeMap(Users.class, UserRegisterDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserRegisterDTO::setId);
			mapper.map(Users::getAccount, UserRegisterDTO::setUsername);
		});
		modelMapper.typeMap(Users.class, UserVerifyDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserVerifyDTO::setId);
			mapper.map(Users::getAccount, UserVerifyDTO::setUsername);
		});
		modelMapper.typeMap(Users.class, UserLoginDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserLoginDTO::setId);
			mapper.map(Users::getAccount, UserLoginDTO::setUsername);
		});
		modelMapper.typeMap(Users.class, UserForgetPasswordDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserForgetPasswordDTO::setId);
			mapper.map(Users::getAccount, UserForgetPasswordDTO::setUsername);
		});
		modelMapper.typeMap(Users.class, UserUpdateDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserUpdateDTO::setId);
			mapper.map(Users::getAccount, UserUpdateDTO::setUsername);
		});
		modelMapper.typeMap(Users.class, UserMeDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserMeDTO::setId);
			mapper.map(Users::getAccount, UserMeDTO::setUsername);
		});
		
		// UserPublic 相關
		modelMapper.typeMap(UserPublic.class, UserPublicDTO.class).addMappings(mapper -> {
			mapper.map(UserPublic::getPublicId, UserPublicDTO::setId);
			mapper.using(usersToUsernameConveter)
					.map(UserPublic::getUsers, UserPublicDTO::setUsername);
		});
		
		// UserAdmin 相關
		modelMapper.typeMap(UserAdmin.class, UserAdminDTO.class).addMappings(mapper -> {
			mapper.map(UserAdmin::getAdminId, UserAdminDTO::setId);
			mapper.using(usersToUsernameConveter)
					.map(UserAdmin::getUsers, UserAdminDTO::setUsername);
			mapper.using(organizationToOrganizationIdConveter)
					.map(UserAdmin::getOrganization, UserAdminDTO::setOrganizationId);
			mapper.using(organizationToOrganizationNameConveter)
					.map(UserAdmin::getOrganization, UserAdminDTO::setOrganizationName);
		});
		
		// ChildInfo 相關
		modelMapper.typeMap(ChildInfo.class, ChildDTO.class).addMappings(mapper -> {
			mapper.map(ChildInfo::getChildId, ChildDTO::setId);
		});
		modelMapper.typeMap(ChildInfo.class, ChildCreateDTO.class).addMappings(mapper -> {
			mapper.map(ChildInfo::getChildId, ChildCreateDTO::setId);
		});
		modelMapper.typeMap(ChildInfo.class, ChildUpdateDTO.class).addMappings(mapper -> {
			mapper.map(ChildInfo::getChildId, ChildUpdateDTO::setId);
		});
		
		// Organization 相關
		modelMapper.typeMap(Organization.class, OrganizationDTO.class).addMappings(mapper -> {
			mapper.map(Organization::getOrganizationId, OrganizationDTO::setId);
		});
		
		// Class 相關
		modelMapper.typeMap(Classes.class, ClassDTO.class).addMappings(mapper -> {
			mapper.map(Classes::getClassId, ClassDTO::setId);
			mapper.using(organizationToOrganizationIdConveter)
					.map(Classes::getOrganization, ClassDTO::setOrganizationId);
			mapper.using(organizationToOrganizationNameConveter)
					.map(Classes::getOrganization, ClassDTO::setOrganizationName);
		});
		
		// Announcement 相關
		modelMapper.typeMap(Announcements.class, AnnouncementDTO.class).addMappings(mapper -> {
			mapper.map(Announcements::getAnnouncementId, AnnouncementDTO::setId);
			mapper.using(organizationToOrganizationIdConveter)
				.map(Announcements::getOrganization, AnnouncementDTO::setOrganizationId);
			mapper.using(organizationToOrganizationNameConveter)
				.map(Announcements::getOrganization, AnnouncementDTO::setOrganizationName);
		});
		
		// Regulation 相關
		modelMapper.typeMap(Regulations.class, RegulationDTO.class).addMappings(mapper -> {
			mapper.map(Regulations::getRegulationId, RegulationDTO::setId);
			mapper.using(baseEnumToDescriptionConveter)
				.map(Regulations::getType, RegulationDTO::setType);
			mapper.using(organizationToOrganizationIdConveter)
				.map(Regulations::getOrganization, RegulationDTO::setOrganizationId);
		});
		
		// DocumentPublic 相關
		modelMapper.typeMap(DocumentPublic.class, DocumentPublicDTO.class).addMappings(mapper -> {
			mapper.map(DocumentPublic::getPublicDocId, DocumentPublicDTO::setId);
			mapper.using(usersPublicToUseIdConveter)
				.map(DocumentPublic::getUserPublic, DocumentPublicDTO::setUserId);
			mapper.using(caseSetToCaseNumberListConverter)
				.map(DocumentPublic::getCases, DocumentPublicDTO::setCaseNumbers);
			mapper.using(baseEnumToDescriptionConveter)
				.map(DocumentPublic::getDocType, DocumentPublicDTO::setType);
		});
		
		// DocumentAdmin 相關
		modelMapper.typeMap(DocumentAdmin.class, DocumentOrganizationDTO.class).addMappings(mapper -> {
			mapper.map(DocumentAdmin::getAdminDocId, DocumentOrganizationDTO::setId);
			mapper.map(DocumentAdmin::getFileName, DocumentOrganizationDTO::setName);
			mapper.using(baseEnumToDescriptionConveter)
				.map(DocumentAdmin::getDocType, DocumentOrganizationDTO::setType);
			mapper.using(organizationToOrganizationIdConveter)
				.map(DocumentAdmin::getOrganization, DocumentOrganizationDTO::setOrganizationId);
		});
		modelMapper.typeMap(DocumentAdmin.class, DocumentAnnouncementDTO.class).addMappings(mapper -> {
			mapper.map(DocumentAdmin::getAdminDocId, DocumentAnnouncementDTO::setId);
			mapper.map(DocumentAdmin::getFileName, DocumentAnnouncementDTO::setName);
			mapper.using(baseEnumToDescriptionConveter)
				.map(DocumentAdmin::getDocType, DocumentAnnouncementDTO::setType);
			mapper.using(announcementToAnnouncementIdConveter)
				.map(DocumentAdmin::getAnnouncements, DocumentAnnouncementDTO::setAnnouncementId);
		});
		
		// Cases 相關
		modelMapper.typeMap(Cases.class, CaseDTO.class).addMappings(mapper -> {
			mapper.map(Cases::getCaseId, CaseDTO::setId);
			mapper.using(childInfoToChildNameConveter)
				.map(Cases::getChildInfo, CaseDTO::setChildName);
			mapper.using(caseOrganizationSetToOrganizationNameFirstConveter)
				.map(Cases::getOrganizations, CaseDTO::setOrganizationNameFirst);
			mapper.using(caseOrganizationSetToOrganizationNameSecondConveter)
				.map(Cases::getOrganizations, CaseDTO::setOrganizationNameSecond);
			mapper.using(caseOrganizationSetToOrganizationStatusFirstConveter)
				.map(Cases::getOrganizations, CaseDTO::setOrganizationNameFirstStatus);
			mapper.using(caseOrganizationSetToOrganizationStatusSecondConveter)
				.map(Cases::getOrganizations, CaseDTO::setOrganizationNameSecondStatus);
			mapper.using(classToClassNameConveter)
				.map(Cases::getClasses, CaseDTO::setClassName);
		});
		
		// ------------------------------------------------------------------------------------
		// DTO -> Entity		
		// User 相關
		modelMapper.typeMap(UserRegisterDTO.class, Users.class).addMappings(mapper -> {
			mapper.map(UserRegisterDTO::getUsername, Users::setAccount);
			// 使用自定義 Converter 進行映射
			mapper.using(roleTypeToRoleConveter)
				.map(UserRegisterDTO::getRoleNumber, Users::setRole);
		});
		
		// ------------------------------------------------------------------------------------
		// DTO -> DTO
		// UserAdminCreateDTO -> UserRegisterDTO	(用於 申請 員工帳號)
		
		return modelMapper;
	}
}
