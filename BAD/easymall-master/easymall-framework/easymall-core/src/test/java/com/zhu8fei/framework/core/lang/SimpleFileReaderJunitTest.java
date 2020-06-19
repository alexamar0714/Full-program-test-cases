package com.zhu8fei.framework.core.lang;

import com.zhu8fei.framework.core.exception.EasyMallCoreException;
import com.zhu8fei.framework.core.method.LangTest;
import com.zhu8fei.framework.test.commons.BaseJunitTest;
import com.zhu8fei.framework.test.commons.utils.MarkTestTarget;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

/**
 * Created by zhu8fei on 2017/5/7.
 */
@MarkTestTarget(LangTest.class)
public class SimpleFileReaderJunitTest extends BaseJunitTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void readAnFileContext() throws EasyMallCoreException, IOException {
        SimpleFileReader simpleFileReader = new SimpleFileReader();
        logger.info("SimpleFileReader.toString {}", simpleFileReader.toString());

        String path = getClass().getResource("/").getFile();
        logger.info(path);
        String context = SimpleFileReader.readAnFileContext(path + "SimpleFileReader.json");
        logger.info(context);
        Assert.assertNotNull(context);
        String empty = SimpleFileReader.readAnFileContext(path + "emptyFile");
        logger.info(empty);
        Assert.assertEquals(empty, "");

        // 断言会抛出这个异常. 使用这种方式的exception断言时,后续代码并不执行
        exception.expect(EasyMallCoreException.class);
        // 断言异常内容.但是不需要写全,只要包含即可.写{} 估计是必错了.
        exception.expectMessage("文件不存在");
        SimpleFileReader.readAnFileContext("/xx");

        // Do nothing, and can not do!

    }
}