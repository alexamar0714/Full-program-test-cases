package com.zhu8fei.framework.test.commons.utils;

import com.zhu8fei.framework.core.lang.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * @author zhu8fei Wong
 */
@MarkTestTarget(MarkTestTarget.class)
public class FindNotMakeTest {
    private String logPath = null;

    @Before
    public void init() {
        logPath = System.getProperty("findClassLogPath");
        if (StringUtils.isEmpty(logPath)) {
            logger.warn("看到这句话,正常情况是,你手动启动了test case, 请到target/logs/findClass.log 查看结果");
            logPath = System.getProperty("user.dir") + "/target/logs/findClass.log";
        }
    }

    private Logger logger = LoggerFactory.getLogger(FindNotMakeTest.class);
    private static final String PROJECT_NAME_PRE = "easymall-";
    private static final String CLASS_NAME_PREFIX = "Test";
    private static final String PACKAGE_NAME_PREFIX = "com.zhu8fei";

    @Test
    public void findNotMakeTest() {
        Set<Class<?>> javaClass = getClasses(PACKAGE_NAME_PREFIX);
        // WTF windows
        logInfo("============    project : " + System.getProperty("user.dir") + " Without control Test cases:   ==============\n");
        for (Class<?> clazz : javaClass) {
            if (clazz.getName().contains("$")) {
                // 不监视内部类等
                continue;
            }
            if (clazz.getName().indexOf(CLASS_NAME_PREFIX) == clazz.getName().length() - 4) {
                MarkTestTarget mtt = clazz.getAnnotation(MarkTestTarget.class);
                if (mtt == null) {
                    logInfo(clazz.getName());
                    continue;
                }
                Class[] testTypes = mtt.value();
                if (testTypes.length == 0) {
                    logInfo(clazz.getName());
                }
            }
        }
        logInfo("\n");
        logInfo("============+++++++++++++++++++++++++++++++++++++++++++++++++==============");
        logInfo("\n\n");
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param pack
     * @return
     */
    private Set<Class<?>> getClasses(String pack) {

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<>();
        // 获取包的名字 并进行替换
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上 只需要扫文件即可
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, classes);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param classes
     */
    private void findAndAddClassesInPackageByFile(String packageName, String packagePath, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirFiles = dir.listFiles((file) -> {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            return (file.isDirectory()) || (file.getName().endsWith(".class"));
        });
        if (dirFiles == null || dirFiles.length == 0) {
            return;
        }
        // 循环所有文件
        for (File file : dirFiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    // classes.add(Class.forName(packageName + '.' +
                    // className));
                    // 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private void logInfo(String msg) {
        if (logPath == null) {
            logger.info(msg);
        } else {
            try {
                FileUtils.createFile(logPath);
                // WTF windows
                FileWriter fw = new FileWriter(logPath, true);
                fw.write(msg);
                fw.write("\n");
                fw.flush();
                fw.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}