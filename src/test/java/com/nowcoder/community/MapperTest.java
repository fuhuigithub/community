package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserService userService;
    //三层架构：表现层调用业务层
    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 测试根据id查询一个
     */
    @Test
    public void testSelectById(){
        //调用方法查询一个
        User user = userMapper.selectById(12);
        System.out.println(user);
    }

    /**
     * 根据用户名查询一个
     */
    @Test
    public void testSelectByName(){
        //调用方法
        User user = userMapper.selectByName("liubei");
        System.out.println(user);
    }

    /**
     * 根据邮箱查询
     */
    @Test
    public void testSelectByEmail(){
        //调用方法
        User user = userMapper.selectByEmail("nowcoder102@sina.com");
        System.out.println(user);
    }

    /**
     * 添加用户
     */
    @Test
    public void testInsertUser(){
        //创建user对象
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        //打印数据库受影响的行数。rows = 1（断言）
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        //打印自动生成并回填的id
        System.out.println(user.getId());
    }

    /**
     * 修改状态(激活、未激活)的方法
     */
    @Test
    public void testUpdateStatus(){
        //调用方法
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);
    }

    /**
     * 测试修改头像
     */
    @Test
    public void testUpdateHeader(){
        //调用方法
        int rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);
    }

    /**
     * 测试修改密码
     */
    @Test
    public void testUpdatePassword(){
        //调用方法
        int rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }

    ////////////////////////////////DiscussPostMapper////////////////////////////////////

    /**
     * 测试首页帖子分页查询
     */
    @Test
    public void testSelectPost(){
        //调用方法
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        //遍历获取每条记录
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        //总记录数
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    /**
     * 展示首页数据
     * @return
     */
    @Test
    public void getIndexPage(){
        //分页查询帖子信息
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, 0, 10);
        //创建一个集合用于装DiscussPost（帖子信息）和 user信息
        List<Map<String, Object>> discussPosts = new ArrayList<>();

        if (list != null){
            //遍历list集合，取出每条帖子的信息
            for (DiscussPost post : list) {
                //创建一个map集合，用于存放帖子和user信息
                Map<String, Object> map = new HashMap<>();
                System.out.println(post);

                //将DiscussPost（帖子信息）信息添加到map集合中
                map.put("post", post);

                //查询user信息
                User user = userService.findUserById(post.getUserId());
                System.out.println(user);
                //将user信息添加到map集合中
                map.put("user", user);

                //将map集合添加到list集合中
                discussPosts.add(map);
                System.out.println(discussPosts);
            }
        }

    }

    ////////////////////////////////////////测试LoginTicketMapper///////////////////////////////////////////

    /**
     * 测试插入一条登录凭证
     */
    @Test
    public void testInsertLoginTicket(){
        // 创建LoginTicket对象。（登陆凭证对象）
        LoginTicket loginTicket = new LoginTicket();
        // 设置值
        loginTicket.setUserId(162);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date());

        // 调用方法插入
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    /**
     * 测试根据票（ticket）查询凭证
     */
    @Test
    public void testSelectByTicket(){
        // 调用方法查询
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    /**
     * 根据票（ticket）修改登录状态
     */
    @Test
    public void testUpdateByStatus(){
        // 调用方法
        loginTicketMapper.updateStatus("abc", 1);
    }

    @Test
    public void testSelectConversations() {
        // 测试：查询当前用户的会话列表，针对每个会话只返回一条最新的私信
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message message : messages) {
            System.out.println(message);
        }

        // 测试：查询当前用户的会话数量
        int count = messageMapper.selectConversationCount(111);
        System.out.println("会话数量：" + count);

        // 测试：查询某个会话所包含的私信列表
        List<Message> comments = messageMapper.selectLetters("111_112", 0, 10);
        for (Message comment : comments) {
            System.out.println(comment);
        }

        // 测试：查询某个会话包含的私信数量
        int count2 = messageMapper.selectLetterCount("111_112");
        System.out.println("私信数量：" + count2);

        // 测试：未读私信的数量
        int count3 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println("未读私信数量：" + count3);
    }



}
