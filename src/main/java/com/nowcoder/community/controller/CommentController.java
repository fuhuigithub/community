package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    // 三层架构：表现层调用业务层
    @Autowired
    private CommentService commentService;

    // 注入获取当前登录用户的对象
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        // 设置当前登录用户的id
        comment.setUserId(hostHolder.getUser().getId());
        // 设置评论是有效的
        comment.setStatus(0);
        // 设置评论时间
        comment.setCreateTime(new Date());

        // 添加评论
        commentService.addComment(comment);

        // 重定向到帖子详情页面
        return "redirect:/discuss/detail/" + discussPostId;
    }

}
