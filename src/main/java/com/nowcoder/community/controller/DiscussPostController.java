package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    // 三层架构：表现层调用业务层
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 发布帖子的方法
     */
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录哦!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 报错的情况,将来统一处理.
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    /**
     * 帖子详情页面
     */
    @RequestMapping(path = "/detail/{discussPo" +
            "stId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 调用方法查询帖子详情
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        // 将帖子信息存入model中
        model.addAttribute("post", post);

        // 帖子表中只有用户id，而显示帖子时显然要的是显示用户名。则可做如下处理
        // 根据用户id查询用户信息
        User user = userService.findUserById(post.getUserId());
        // 将user信息存入model中。从中获取帖子作者的名字
        model.addAttribute("user", user);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论：给帖子的评论
        // 回复：给评论的评论
        // 调用分页查询方法。查询评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            // 遍历评论列表
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>();
                // 添加评论
                commentVo.put("comment", comment);
                // 添加评论者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 回复列表。Integer.MAX_VALUE：把回复全部查出来
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复vo列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    // 遍历回复列表
                    for (Comment reply : replyList) {
                        // 回复Vo
                        Map<String, Object> replyVo = new HashMap<>();
                        // 添加回复
                        replyVo.put("reply", reply);
                        // 添加回复者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 添加回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        // 将回复Vo添加到list（回复列表）集合
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        // 将评论数据存入model中
        model.addAttribute("comments", commentVoList);

        // 跳转帖子详情页面
        return "/site/discuss-detail";
    }
}
