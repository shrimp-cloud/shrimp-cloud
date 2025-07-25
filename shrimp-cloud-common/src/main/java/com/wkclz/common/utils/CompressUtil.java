package com.wkclz.common.utils;


import com.wkclz.common.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * CompressUtil
 *
 * @author wangkc
 * @date 2019年09月09日 20:47:57
 */

public class CompressUtil {

    private static final Logger logger = LoggerFactory.getLogger(CompressUtil.class);
    private static final int BUFFER_SIZE = 2 * 1024;


    /**
     * 压缩成ZIP
     *
     * @param srcDir 压缩文件夹路径
     * @param out    压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void zip(String srcDir, OutputStream out) {
        zip(srcDir, out, true);
    }


    /**
     * 压缩成ZIP
     *
     * @param srcDir           压缩文件夹路径
     * @param out              压缩文件输出流
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     */
    public static void zip(String srcDir, OutputStream out, boolean keepDirStructure) {
        long start = System.currentTimeMillis();
        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            File sourceFile = new File(srcDir);
            zip(sourceFile, zos, sourceFile.getName(), keepDirStructure);
            long end = System.currentTimeMillis();
            logger.info("压缩完成，耗时：{} ms", (end - start));
        } catch (IOException e) {
            throw BizException.error(e.getMessage());
        }
    }


    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     */
    private static void zip(File sourceFile, ZipOutputStream zos, String name, boolean keepDirStructure) {
        byte[] buf = new byte[BUFFER_SIZE];

        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            // copy文件到zip输出流中
            int len;
            try (FileInputStream in = new FileInputStream(sourceFile)) {
                zos.putNextEntry(new ZipEntry(name));

                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
            } catch (IOException e) {
                throw BizException.error(e.getMessage());
            }

            // Complete the entry
            try {
                zos.closeEntry();
            } catch (IOException e) {
                throw BizException.error(e.getMessage());
            }
            return;
        }

        File[] listFiles = sourceFile.listFiles();
        if (listFiles == null || listFiles.length == 0) {
            // 需要保留原来的文件结构时,需要对空文件夹进行处理
            if (keepDirStructure) {
                try {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                } catch (IOException e) {
                    throw BizException.error(e.getMessage());
                }
            }
            return;
        }

        for (File file : listFiles) {
            // 判断是否需要保留原来的文件结构
            if (keepDirStructure) {
                // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                zip(file, zos, name + "/" + file.getName(), keepDirStructure);
            } else {
                zip(file, zos, file.getName(), keepDirStructure);
            }
        }
    }

    /**
     * zip解压
     *
     * @param srcFile     zip源文件
     * @param destDirPath 解压后的目标文件夹
     * @throws RuntimeException 解压失败会抛出运行时异常
     */
    public static void unZip(File srcFile, String destDirPath) {
        long start = System.currentTimeMillis();
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        // 开始解压
        try (ZipFile zipFile = new ZipFile(srcFile)) {
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                logger.info("解压 {}", entry.getName());
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(destDirPath + "/" + entry.getName());
                    // 保证这个文件的父文件夹必须要存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    boolean newFile = targetFile.createNewFile();
                    if (!newFile) {
                        throw BizException.error("创建文件失败");
                    }
                    // 将压缩文件内容写入到这个文件中
                    try (
                        InputStream is = zipFile.getInputStream(entry);
                        FileOutputStream fos = new FileOutputStream(targetFile);
                        ) {
                        int len;
                        byte[] buf = new byte[BUFFER_SIZE];
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                    }
                }
            }
            long end = System.currentTimeMillis();
            logger.info("解压完成，耗时：{} ms", (end - start));
        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);
        }
    }


    public static void main(String[] args) {
        try {
            FileOutputStream fos = new FileOutputStream(new File("/Users/wangkc/project/code/shrimp-gen/temp/gen/20190908113830301/src.zip"));
            CompressUtil.zip("/Users/wangkc/project/code/shrimp-gen/temp/gen/20190908113830301/src", fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
