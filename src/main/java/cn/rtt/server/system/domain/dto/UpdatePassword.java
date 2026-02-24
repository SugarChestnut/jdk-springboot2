package cn.rtt.server.system.domain.dto;

import lombok.Data;

@Data
public class UpdatePassword {
    private Long userId;
    private String password;
    private String oldPassword;
    private String uuid;
    private String code;

}
