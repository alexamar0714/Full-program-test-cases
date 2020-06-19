package com.zhu8fei.framework.test.method;

import com.zhu8fei.framework.EasyMallTestAll;
import com.zhu8fei.framework.test.commons.mybatis.SimpleAbstractProcessorTest;
import com.zhu8fei.framework.test.commons.mybatis.SimpleJsonProcessorIpmlTest;
import com.zhu8fei.framework.test.commons.mybatis.SimpleMybatisSqlProviderSpringTest;
import com.zhu8fei.framework.test.commons.utils.MarkTestTarget;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by zhu8fei on 2017/5/9.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

        SimpleMybatisSqlProviderSpringTest.class , SimpleJsonProcessorIpmlTest.class,

        SimpleAbstractProcessorTest.class,
})
@MarkTestTarget(EasyMallTestAll.class)
public class PrepareAndExpectSqlTest {
}
