package cn.rtt.server.system.controller;

import cn.rtt.server.system.domain.entity.SysPost;
import cn.rtt.server.system.domain.request.post.PostSearchRequest;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.domain.response.SysPage;
import cn.rtt.server.system.service.SysPostService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author rtt
 * @date 2026/3/11 09:14
 */
@RestController
@RequestMapping("/system/post")
@AllArgsConstructor
public class SysPostController {

    private final SysPostService postService;

    /**
     * 获取当前登录用户菜单信息
     */
    @RequestMapping("/search")
    @PreAuthorize("@ss.hasPermission('system:post:list')")
    public Result<SysPage<SysPost>> search(@RequestBody PostSearchRequest request) {
        return Result.success(postService.search(request));
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermission('system:post:create')")
    @PostMapping("create")
    public Result<?> create(@Validated @RequestBody SysPost post) {
        postService.createPost(post);
        return Result.success();
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermission('system:post:edit')")
    @PostMapping("update")
    public Result<?> edit(@Validated @RequestBody SysPost post) {
        postService.updatePost(post);
        return Result.success();
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermission('system:post:delete')")
    @GetMapping("/delete/{deptId}")
    public Result<?> delete(@PathVariable("deptId") Long deptId) {
        postService.deletePost(deptId);
        return Result.success();
    }
}
