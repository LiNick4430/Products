package com.github.lianick.model.eneity;

import java.util.Set;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")		// 角色 用於 RBAC
@SQLRestriction("delete_at IS NULL")
public class Role extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Long roleId;				// 角色 ID
	
	@Column(name = "role_name", unique = true, nullable = false)
	private String name;				// 角色 名
	
	@Column(name = "role_description")
	private String description;			// 角色 描述
	
	@ManyToMany	
	@JoinTable(name = "role_permission",									// 指定中間表名稱
				joinColumns = @JoinColumn(name = "role_id"),				// 本身在中間表的外鍵
				inverseJoinColumns = @JoinColumn(name = "permission_id"))	// 對方在中間表的外鍵
	private Set<Permission> permissions;									// 儲存 該角色 所擁有的 權限集合
}
