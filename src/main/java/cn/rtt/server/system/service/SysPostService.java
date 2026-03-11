package cn.rtt.server.system.service;


import cn.rtt.server.system.domain.entity.SysPost;
import cn.rtt.server.system.domain.request.post.PostSearchRequest;
import cn.rtt.server.system.domain.response.SysPage;

public interface SysPostService {

    SysPage<SysPost> pageSearch(PostSearchRequest request);

    void createPost(SysPost post);

    void updatePost(SysPost post);

    void deletePost(long postId);
}
