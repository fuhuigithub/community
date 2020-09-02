package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
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
public class MessageController {

    // 三层架构：表现层调用业务层
    @Autowired
    private MessageService messageService;

    // 获取当前用户的对象
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    /**
     * 获取会话列表的方法
     *
     * @param model
     * @param page  分页组件
     * @return
     */
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        // 获取当前登录的用户
        User user = hostHolder.getUser();

        // 设置分页信息

        // 每页显示5条信息
        page.setLimit(5);
        // 分页访问路径
        page.setPath("/letter/list");
        // 分页的数据总量。（用来计算总页数）需要查询数据库
        page.setRows(messageService.findConversationCount(user.getId()));

        // 获取会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());

        // 封装会话信息的集合
        List<Map<String, Object>> conversations = new ArrayList<>();

        // 遍历会话列表，构造map集合
        if (conversationList != null) {
            for (Message message : conversationList) {
                // 创建存列表信息的map集合
                Map<String, Object> map = new HashMap<>();
                // 存储私信信息
                map.put("conversation", message);
                // 存储私信数量
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                // 存储指定会话的未读消息数量
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));

                // 获取收消息人的id
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                // 存储收件人信息。
                map.put("target", userService.findUserById(targetId));

                // 将会话信息，添加到会话集合
                conversations.add(map);
            }
        }
        // 将会话列表信息存入model中
        model.addAttribute("conversations", conversations);

        // 查询全部会话的综合未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        // 存入model
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        // 跳转会话列表页
        return "/site/letter";
    }

    /**
     * 获取私信列表中未读消息的id。
     * @param letterList
     * @return
     */
    public List<Integer> getLetterIds(List<Message> letterList) {
        // 存储未读消息的id
        List<Integer> ids = new ArrayList<>();

        // 遍历letterList集合
        if (letterList != null) {
            for (Message message : letterList) {
               // 判断此用户是否是消息的接收者，和此消息是否是未读状态
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        // 返回未读消息id的集合
        return ids;
    }

    /**
     * 私信列表详情的方法
     * @param conversationId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        // 封装分页信息

        // 每页显示5条数据
        page.setLimit(5);
        // 分页路径
        page.setPath("/letter/detail/" + conversationId);
        // 分页数据总条数。（用于计算分页总页数）
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());

        // 封装私信信息的集合
        List<Map<String, Object>> letters = new ArrayList<>();

        // 遍历私信列表集合
        if (letterList != null) {
            for (Message message : letterList) {
                // 存私信信息的集合
                Map<String, Object> map = new HashMap<>();
                // 添加私信信息
                map.put("letter", message);
                // 将fromId转化成fromUser。（需要在页面展示用户名，而不是用户id）
                map.put("fromUser", userService.findUserById(message.getFromId()));

                // 将map集合存入list（封装私信信息的集合）
                letters.add(map);
            }
        }

        // 将私信详情信息存入model中
        model.addAttribute("letters", letters);

        // 私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        // 设置未读消息为已读状态
        List<Integer> ids = getLetterIds(letterList);
        // 判空
        if (!ids.isEmpty()) {
            // 调用方法。将对应id的消息设置为已读状态
            messageService.readMessage(ids);
        }

        // 跳转私信详情页面
        return "/site/letter-detail";
    }

    // 获取私信目标用户
    private User getLetterTarget(String conversationId) {
        // 切割conversationId。把发送者id和接受者id分离
        String[] ids = conversationId.split("_");
        // 获取拆分后的id
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        // 获取私信的目标用户
        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    /**
     * 发私信的请求
     * @param toName 发送给谁（收信人名字）
     * @param content 发送的内容
     * @return
     */
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        // 根据用户名查询用户
        User target = userService.findUserByName(toName);
        // 空值处理
        if (target == null) {
            // 采用异步请求给出提示信息
            return CommunityUtil.getJSONString(1, "目标用户不存在!");
        }

        // 封装私信消息
        Message message = new Message();
        // 设置发送者的id
        message.setFromId(hostHolder.getUser().getId());
        // 设置接收者的id
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            // 设置会话id
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            // 设置会话id
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        // 设置私信消息内容
        message.setContent(content);
        // 消息状态使用默认值。设置创建时间
        message.setCreateTime(new Date());

        // 添加私信消息
        messageService.addMessage(message);

        // 采用异步请求给出提示信息
        return CommunityUtil.getJSONString(0);
    }

}
