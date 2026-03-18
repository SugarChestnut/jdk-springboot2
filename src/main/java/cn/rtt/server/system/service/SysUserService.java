package cn.rtt.server.system.service;


import cn.rtt.server.system.domain.dto.ImageParam;
import cn.rtt.server.system.domain.dto.UpdatePassword;
import cn.rtt.server.system.domain.dto.UserBaseEdit;
import cn.rtt.server.system.domain.entity.SysUser;
import cn.rtt.server.system.domain.request.user.UserSearchRequest;
import cn.rtt.server.system.domain.response.SysPage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface SysUserService {

    /**
     * 用户搜索
     */
    SysPage<SysUser> search(UserSearchRequest request);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUser getUser(String userName);

    /**
     * 更新用户登录IP
     */
    void updateLoginIp(Long userId, String ip);
    // ======================
    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */

    List<SysUser> selectUserList(SysUser user);




    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SysUser selectUserById(Long userId);


    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkUserNameUnique(SysUser user);

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkPhoneUnique(SysUser user);


    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    public void checkUserDataScope(Long userId);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     */
    void insertUser(SysUser user);

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public boolean registerUser(SysUser user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean updateUser(SysUser user);


    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean resetPwd(UpdatePassword user);


    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    boolean deleteUserByIds(Long[] userIds);

    IPage<SysUser> selectUserPage(SysUser user);

    String changeAvatar(MultipartFile file, ImageParam imageParam);

    Boolean checkAllow(Long id);

    void checkAllowToUpdate(SysUser user);

    Boolean checkAllMobile(Long id, String mobile);

    Boolean updateBaseInfo(UserBaseEdit userBaseEdit, HttpServletResponse response) throws FileUploadException, IOException;

    List<SysUser> selectUserByIds(Set<Long> sysUsers);

    List<SysUser> listData();

    List<SysUser> getAdminUser();

    List<SysUser> getBmjl();

    void updateLoginTime(Long id, Long loginTime);
}
