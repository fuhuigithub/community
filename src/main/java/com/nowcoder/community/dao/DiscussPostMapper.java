package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    /**
     * 分页查询
     * @param userId 用户id
     * @param offset 查询数据的起始索引
     * @param limit 查几条数据
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 发帖子的方法
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 查询帖子详情的方法
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);

    /**
     * 更新评论帖子的数量
     * @param id 帖子id
     * @param commentCount 帖子评论数量
     * @return
     */
    int updateCommentCount(int id, int commentCount);
}
