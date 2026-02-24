package cn.rtt.server.system.domain.dto;


import lombok.Data;

@Data
public class UserBaseEdit {
    private Long userId;

    private String nickname;

    private String personName;

    private Integer gender;

    private String mobile;

    private Integer status;

    private String code;

    private String uuid;


}
