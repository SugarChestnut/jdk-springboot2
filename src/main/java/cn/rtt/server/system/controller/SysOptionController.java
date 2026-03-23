package cn.rtt.server.system.controller;

import cn.rtt.server.system.constant.DataScopeEnum;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.domain.response.Option;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author rtt
 * @date 2026/3/11 09:25
 */
@RestController
@RequestMapping("/system/option")
public class SysOptionController {

    @GetMapping("/data_scope")
    public Result<List<Option>> dataScope() {
        return Result.success(DataScopeEnum.options());
    }
}
