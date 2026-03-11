package cn.rtt.server.system.service;

import cn.rtt.server.system.dao.SysPostRepository;
import cn.rtt.server.system.domain.entity.SysPost;
import cn.rtt.server.system.domain.request.post.PostSearchRequest;
import cn.rtt.server.system.domain.response.SysPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author rtt
 * @date 2026/3/11 13:12
 */
@Service
@AllArgsConstructor
public class SysPostServiceImpl implements SysPostService{

    private final SysPostRepository postRepository;

    @Override
    public SysPage<SysPost> pageSearch(PostSearchRequest request) {
        IPage<SysPost> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<SysPost> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.isNotBlank(request.getPostCode()), SysPost::getPostCode, request.getPostCode());
        w.like(StringUtils.isNotBlank(request.getPostName()), SysPost::getPostName, request.getPostName());
        w.orderByDesc(SysPost::getPostId);
        postRepository.page(page, w);
        return SysPage.transform(page);
    }

    @Override
    public void createPost(SysPost post) {
        checkPost(post);
        postRepository.save(post);
    }

    @Override
    public void updatePost(SysPost post) {
        if (post.getPostId() == null) throw new IllegalArgumentException("未指定岗位");
        checkPost(post);
        postRepository.updateById(post);
    }

    private void checkPost(SysPost post) {
        if (post.getOrderNum() == null) post.setOrderNum(0);

        LambdaQueryWrapper<SysPost> w1 = new LambdaQueryWrapper<>();
        w1.eq(SysPost::getPostCode, post.getPostCode());
        if (post.getPostId() != null) {
            List<SysPost> list = postRepository.list(w1);
            for (SysPost d : list) {
                if (!Objects.equals(d.getPostId(), post.getPostId())) throw new IllegalArgumentException("岗位编码已存在");
            }
        } else {
            if (postRepository.count(w1) > 0) throw new IllegalArgumentException("岗位编码已存在");
        }
    }

    @Override
    public void deletePost(long postId) {

    }
}
