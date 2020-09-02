package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 帖子评论持久层接口
 */
@Mapper
public interface CommentMapper {

    /**
     * 根据实体进行查询
     * @param entityType 评论帖的类型（对课程的评论，习题的评论，书的评论等等...）
     * @param entityId 评论贴的id（是哪条帖子的评论）
     * @param offset 分页的起始索引
     * @param limit 每页查询几条数据
     * @return
     */
    List<Comment> SelectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 查询评论的总条数
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCountByEntity(int entityType, int entityId);

    /**
     * 添加帖子评论的方法
     * @param comment
     * @return
     */
    int insertComment(Comment comment);

}
