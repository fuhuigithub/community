package com.nowcoder.community;

import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试过滤敏感词
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //引入配置类
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 测试过滤敏感词的方法
     */
    @Test
    public void testSensitiveFilter() {
        // 定义一个带有敏感词的字符串
        String text = "这里可以赌博、嫖娼、吸毒、开票，哈哈哈!";

        // 调用方法
        String filterText = sensitiveFilter.filter(text);
        System.out.println(filterText);

        // 定义一个带有敏感词的字符串并用特殊符号隔开
        text = "这里可☆以☆赌☆博、☆嫖☆娼☆、☆吸☆毒☆、开☆票，哈哈哈!";
        filterText = sensitiveFilter.filter(text);
        System.out.println(filterText);
    }

    /**
     * 测试转化json格式的工具类
     */
    @Test
    public void testJsonString() {
        // 创建一个map
        Map<String, Object> map = new HashMap<>();
        // 添加值
        map.put("name", "张三");
        map.put("age", 18);
        System.out.println(CommunityUtil.getJSONString(0, "yes", map));
    }

}
