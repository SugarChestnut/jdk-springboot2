package cn.rtt.server.system.controller;


import cn.rtt.server.system.domain.dto.*;
import cn.rtt.server.system.domain.entity.SysUser;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.exception.SystemException;
import cn.rtt.server.system.service.SysRoleService;
import cn.rtt.server.system.service.SysUserService;
import cn.rtt.server.system.utils.CollectionUtils;
import cn.rtt.server.system.utils.SecurityUtils;
import cn.rtt.server.system.utils.StringProUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息
 *
 * @author author
 * @since 2024-07-17
 */
@RestController
@RequestMapping("/system/user")
@AllArgsConstructor
public class SysUserController {
    private final SysUserService userService;

    private final SysRoleService roleService;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermission('system:user:list')")
    @PostMapping("/page")
    public Result list(@RequestBody SysUser user) {
        return Result.success(userService.selectUserPage(user));
    }

    @GetMapping("list")
    public Result list() {
        return Result.success(userService.listData());
    }

    @PostMapping("/forSelect")
    public Result forSelect(@RequestBody SysUser user) {
        return Result.success(userService.selectUserList(user));
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    @GetMapping("getInfo")
    public Result getInfo(@RequestParam(value = "userId", required = false) Long userId) {
        Map<String, Object> map = new HashMap<>();
        if (userId != null) {
            SysUser user = userService.selectUserById(userId);
            user.setPassword(null);
            if (CollectionUtils.isNotEmpty(user.getRoles())) {
                Long roleId = user.getRoles().get(0).getRoleId();
//                user.setRoleIds(roleId);
            }
            map.put("ToolSysUser", user);
        }
        return Result.success(map);
    }

    /**
     * 编辑基础信息
     */
    @RequestMapping(consumes = {"multipart/form-data"}, value = "editBaseInfo")
    public Result editBaseInfo(UserBaseEdit formData, HttpServletResponse response) throws IOException {
        return Result.success(userService.updateBaseInfo(formData, response));
    }


    /**
     * 编辑基础信息
     */
    @RequestMapping(consumes = {"application/json"}, value = "editBaseInfo")
    public Result editBaseInfoJson(@RequestBody(required = false) UserBaseEdit jsonData, HttpServletResponse response) throws IOException {

        return Result.success(userService.updateBaseInfo(jsonData, response));
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermission('system:user:add')")
    @PostMapping("add")
    public Result add(@Validated @RequestBody SysUser user) {
        user.setAvatarUrl("https://img2.baidu.com/it/u=2370931438,70387529&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500");
        userService.checkAllowToUpdate(user);

        String mobile = user.getMobile();
        if (StringUtils.isNotEmpty(mobile) && !StringProUtils.isMobile(mobile)) {
            throw new SystemException("请输入正确手机号码");
        }
        if (!userService.checkUserNameUnique(user)) {
            return Result.error("新增用户'" + user.getUsername() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getMobile()) && !userService.checkPhoneUnique(user)) {
            return Result.error("新增用户'" + user.getUsername() + "'失败，手机号码已存在");
        }
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        userService.insertUser(user);
        return Result.success();
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermission('system:user:edit')")
    @PostMapping("edit")
    public Result edit(@Validated @RequestBody SysUser user) {
        userService.checkAllow(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        if (StringUtils.isNotEmpty(user.getMobile()) && !userService.checkPhoneUnique(user)) {
            return Result.error("修改用户'" + user.getUsername() + "'失败，手机号码已存在");
        }

        return Result.success(userService.updateUser(user));
    }

    /**
     * 修改头像
     */
    @PostMapping("/changeAvatar")
    public Result changeAvatar(UserChangeImg userChangeImg) {
        if (userChangeImg.getImageParam() == null) userChangeImg.setImageParam(new ImageParam(500, 500));
        return Result.success(userService.changeAvatar(userChangeImg.getFile(), userChangeImg.getImageParam()));
    }


    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermission('system:user:remove')")
    @GetMapping("/deleteById")
    public Result remove(@RequestParam("userIds") Long[] userIds) {
        if (ArrayUtils.contains(userIds, SecurityUtils.getUserId())) {
            return Result.error("当前用户不能删除");
        }
        return Result.success(userService.deleteUserByIds(userIds));
    }


    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermission('system:user:resetPwd')")
    @PostMapping("/resetPwd")
    public Result resetPwd(@RequestBody UpdatePassword user) {
        Long userId = SecurityUtils.getUserId();
        if (!userId.equals(user.getUserId())) {
            throw new SystemException("不允许修改他人密码!");
        }

        return Result.success(userService.resetPwd(user));
    }


}
