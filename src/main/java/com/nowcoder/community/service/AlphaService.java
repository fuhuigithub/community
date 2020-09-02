package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
//@Scope("prototype")
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    // 注入spring提供的事务管理对象
    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService() {
//        System.out.println("实例化AlphaService");
    }

    @PostConstruct
    public void init() {
//        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy() {
//        System.out.println("销毁AlphaService");
    }

    public String find() {
        return alphaDao.select();
    }

    /**
     * 常用的3种传播机制：
     * REQUIRED：支持当前事务（外部事物），如果不存在则创建新事物
     * REQUIRES_NEW：创建一个新事务，并且暂停当前事务（外部事物）
     * NESTED：如果当前存在事务（外部事务），则嵌套在该事务中执行（独立的提交和回滚），否则就会和REQUIRED一样。
     * @return
     */
    // 声明式事务管理
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        // 新增用户
        User user = new User();
        // 设置值
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        // 调用方法，添加用户
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost post = new DiscussPost();
        // 设置属性
        post.setUserId(user.getId());
        post.setTitle("hello");
        post.setContent("新人报到!");
        post.setCreateTime(new Date());
        // 调用方法新增帖子
        discussPostMapper.insertDiscussPost(post);

        int i = 2/0;

        return "ok";
    }

    /**
     * 编程式事务管理
     * @return
     */
    public Object save2() {
        // 设置事务隔离级别
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        // 设置十五传播机制
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                // 新增用户
                User user = new User();
                // 设置值
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                // 调用方法，添加用户
                userMapper.insertUser(user);

                // 新增帖子
                DiscussPost post = new DiscussPost();
                // 设置属性
                post.setUserId(user.getId());
                post.setTitle("你好");
                post.setContent("我是新人!");
                post.setCreateTime(new Date());
                // 调用方法新增帖子
                discussPostMapper.insertDiscussPost(post);

                int i = 2/0;

                return "ok";
            }
        });
    }

}
