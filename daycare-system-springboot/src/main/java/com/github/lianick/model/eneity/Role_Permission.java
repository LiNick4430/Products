package com.github.lianick.model.eneity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role_permission", 
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"role_id", "permission_id"})	// 確保 role_id 和 permission_id 的組合是唯一的
		})
public class Role_Permission extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_permission_id")
	private Long rolePermissionId;				// 角色-權限 ID
	
	@ManyToOne
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;							// 角色 ID
	
	@ManyToOne
	@JoinColumn(name = "permission_id", nullable = false)
	private Permission permission;				// 權限 ID
	
}
