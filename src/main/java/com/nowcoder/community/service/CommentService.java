package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * 帖子评论业务层
 */
@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    // 注入过滤敏感词的对象
    @Autowired
    private SensitiveFilter sensitiveFilter;

    // 注入跟新帖子评论数量的类
    @Autowired
    private DiscussPostService discussPostService;

    // 分页查询评论
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.SelectCommentsByEntity(entityType, entityId, offset, limit);
    }

    // 查询总评论数量
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    // 添加评论的方法
    // 添加事务控制。使两次对数据库的操作要成功都成功，要失败都失败。
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        // 空值处理
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 过滤html标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        // 过滤敏感词
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        // 添加评论
        int rows = commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 调用方法查询帖子评论数量
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());

            // 调用方法，更新帖子评论数量
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

    return rows;
    }

}
