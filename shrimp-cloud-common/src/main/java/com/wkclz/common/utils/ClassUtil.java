package com.wkclz.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 此类用于查询包下的类
 */
public class ClassUtil {


    private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 静态类的方法
     *
     * @param clazz
     * @param methodName
     * @return
     */
    public static Method getModelMethod(Class<?> clazz, String methodName) {
        if (StringUtils.isBlank(methodName)) {
            throw new RuntimeException("method name can not be null");
        }
        if (clazz == Object.class) {
            return null;
        }
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.equals(methodName)) {
                return method;
            }
        }
        Class<?> superClazz = clazz.getSuperclass();
        return getModelMethod(superClazz, methodName);
    }


    /**
     * 从包package中获取所有的Class
     *
     * @param pack
     * @return
     */
    public static Set<Class<?>> getClasses(String pack) {

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageDirName = pack.replace('.', '/');
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
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    String packageName = pack;
                    // System.err.println("file类型的扫描");
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                }

                if ("jar".equals(protocol)) {
                    String packageName = pack;
                    // 如果是jar包文件
                    // 定义一个JarFile
                    // 获取jar
                    try (JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile()) {
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            String clazzName = packageName + '.' + className;
                                            Class<?> clazz = Class.forName(clazzName);
                                            classes.add(clazz);
                                        } catch (ClassNotFoundException e) {
                                            logger.error(e.getMessage(), e);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");
                        logger.error(e.getMessage(), e);
                    }
                }

                // 没有其他可能了
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
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirfiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
                    classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    // classes.add(Class.forName(packageName + '.' + className));
                    // 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(
                        Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Set<Class<?>> getByInterface(Class clazz, Set<Class<?>> classesAll) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        // 获取指定接口的实现类
        if (!clazz.isInterface()) {
            try {
                /**
                 * 循环判断路径下的所有类是否继承了指定类 并且排除父类自己
                 */
                Iterator<Class<?>> iterator = classesAll.iterator();
                while (iterator.hasNext()) {
                    Class<?> cls = iterator.next();
                    /**
                     * isAssignableFrom该方法的解析，请参考博客：
                     * http://blog.csdn.net/u010156024/article/details/44875195
                     */
                    if (clazz.isAssignableFrom(cls)) {
                        // 自身并不加进去
                        if (!clazz.equals(cls)) {
                            classes.add(cls);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("出现异常");
            }
        }
        return classes;
    }


    public static void main(String[] args) {

        // 包下面的类
        Set<Class<?>> clazzs = getClasses("cn.package.test");

        System.out.printf(clazzs.size() + "");
        // 某类或者接口的子类
        Set<Class<?>> inInterface = getByInterface(Object.class, clazzs);
        System.out.printf(inInterface.size() + "");

        for (Class<?> clazz : clazzs) {

            // 获取类上的注解
            Annotation[] annos = clazz.getAnnotations();
            for (Annotation anno : annos) {
                System.out.println(clazz.getSimpleName().concat(".").concat(anno.annotationType().getSimpleName()));
            }

            // 获取方法上的注解
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Annotation[] annotations = method.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    System.out.println(clazz.getSimpleName().concat(".").concat(method.getName()).concat(".")
                        .concat(annotation.annotationType().getSimpleName()));
                }
            }
        }

    }

}