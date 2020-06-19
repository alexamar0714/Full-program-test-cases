package com.zhu8fei.framework.core.mybatis.plugin;


import com.zhu8fei.framework.core.mybatis.mapper.UserMapper;
import com.zhu8fei.framework.core.mybatis.model.User;
import com.zhu8fei.framework.test.commons.BaseTransactionalSpringTest;
import com.zhu8fei.framework.test.commons.utils.MarkTestTarget;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by zhu8fei on 2017/3/28.
 */
@MarkTestTarget
public class InsertSqlPluginTransactionalSpringTest extends BaseTransactionalSpringTest {
    Logger logger = LoggerFactory.getLogger(InsertSqlPluginTransactionalSpringTest.class);
    @Autowired
    UserMapper userMapper;

    @Test
    public void insert() {
        User user = new User();
        user.setName("test");
        user.setRealName("last11");
        user.setEmail("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="6f1517172f1515410c0002">[email protected]</a>");
        user.setMobile("13100912233");
        user.setPassword("123");
        user.setSalt("321");
        userMapper.insert(user);

        User result = userMapper.selectByPrimaryKey(user.getId());
        logger.info(result.getName());
        logger.info(result.getEmail());

        user = new User();
        user.setRealName("last11");
        List<User> list = userMapper.select(user);
        result = list.get(0);
        logger.info(result.getName());
        logger.info(result.getEmail());
        assertNotNull(result);
    }

}