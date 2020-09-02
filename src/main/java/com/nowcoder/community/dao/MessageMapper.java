package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 私信持久层接口
 */
@Mapper
public interface MessageMapper {

    /**
     * 查询当前用户的会话列表，针对每个会话只返回一条最新的私信
     * @param userId 用户id
     * @param offset 分页查询的起始索引
     * @param limit 每页显示的数据条数
     * @return
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 查询当前用户的会话数量
     * @param userId
     * @return
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个会话所包含的私信列表
     * @param conversationId 会话id。用于查询会话列表
     * @param offset
     * @param limit
     * @return
     */
     List<Message> selectLetters(String conversationId, int offset, int limit);

    /**
     * 查询某个会话所包含的私信数量
     * @param conversationId
     * @return
     */
     int selectLetterCount(String conversationId);

    /**
     * 查询未读私信的数量
     * @param userId
     * @param conversationId
     * @return
     */
     int selectLetterUnreadCount(int userId, String conversationId);

    /**
     * 新增一个消息（发私信的方法）
     * @param message
     * @return
     */
     int insertMessage(Message message);

    /**
     * 修改私信消息状态（0-未读，1-已读，2-删除）
     * 注：一次可能要修改多个，所以要传多个id，因此传个id的集合
     * @param ids
     * @param status
     * @return
     */
     int updateStatus(List<Integer> ids, int status);
}
