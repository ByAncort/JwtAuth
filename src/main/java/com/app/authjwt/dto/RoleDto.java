package com.app.authjwt.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Data
@Builder
public class RoleDto {
    private String name;
    private Set<PermissionDto> permissions;
}
