package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    // 三层架构：业务层调用持久层
    @Autowired
    private MessageMapper messageMapper;

    // 注入过滤敏感词的工具类
    @Autowired
    private SensitiveFilter sensitiveFilter;

   // 查询当前用户的会话列表，针对每个会话只返回一个最新的私信
    public List<Message> findConversations(int userId, int offset, int limit) {
        // 调用方法查询数据
        return messageMapper.selectConversations(userId, offset, limit);
    }

    // 查询当前用户的会话数量
    public int findConversationCount(int userId) {
        // 调用方法查询数据
        return messageMapper.selectConversationCount(userId);
    }

    // 查询某个会话所包含的私信列表
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    // 查询某个会话所包含的私信数量
    public int findLetterCount(String conversationId) {
        // 调用方法查询
        return messageMapper.selectLetterCount(conversationId);
    }

    // 查询未读的私信数量
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    // 新增私信的方法（发消息的方法）
    public int addMessage(Message message) {
        // 对html标签进行过滤
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        // 过滤敏感词
        message.setContent(sensitiveFilter.filter(message.getContent()));
        // 添加私信
        return messageMapper.insertMessage(message);
    }

    // 修改私信消息状态的方法（0-未读，1-已读，2-删除）
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }
}