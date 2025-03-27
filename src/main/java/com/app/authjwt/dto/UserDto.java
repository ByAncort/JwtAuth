package com.app.authjwt.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String usermane;
    private String Email;
    private List<RoleDto> roles;
    private boolean enabled;

}
