package cn.rtt.server.system.controller;

//import com.cjj.car.system.service.SysFileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author rtt
 * @date 2026/1/22 10:47
 */
@RequestMapping("/system/file")
@Controller
@AllArgsConstructor
public class SysFileController {

//    private final SysFileService sysFileService;
//
//    @RequestMapping("/{token}")
//    public void get(HttpServletResponse response, @PathVariable("token") String  token) throws IOException {
//        sysFileService.getFile(response, token);
//    }
}
