package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //引入配置类
//哪个类需要ioc容器就去实现ApplicationContextAware接口
public class CommunityApplicationTest implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * 启动springboot时创建spring容器，并扫描到实现ApplicationContextAware接口的类，把创建的ioc容器传入该方法，来供该类使用。
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplication(){
        System.out.println(applicationContext);

        //获取bean对象
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        System.out.println(alphaDao.select());

        //获取bean对象
        alphaDao = (AlphaDao) applicationContext.getBean("alphaHibernate");
        System.out.println(alphaDao.select());
    }

    @Test
    public void testBeanManagement(){
        //获取bean对象
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);

        //获取bean对象
        AlphaService alphaService1 = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService1);
    }

    @Test
    public void testBeanConfig(){
        //从ioc容器中获取配置文件中的bean
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }

    @Autowired
    @Qualifier("alphaHibernate")
    private AlphaDao alphaDao;

    @Autowired
    private AlphaService alphaService;

    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Test
    public void testDI(){
        System.out.println(alphaDao);
        System.out.println(alphaService);
        System.out.println(simpleDateFormat);
    }
}
