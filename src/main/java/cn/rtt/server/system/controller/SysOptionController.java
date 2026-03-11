package cn.rtt.server.system.controller;

import cn.rtt.server.system.constant.DataRoleEnum;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.domain.response.Option;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author rtt
 * @date 2026/3/11 09:25
 */
@RequestMapping("/system/option")
public class SysOptionController {

    @RequestMapping("/data_role")
    public Result<List<Option>> dataRole() {
        return Result.success(DataRoleEnum.options());
    }
}
