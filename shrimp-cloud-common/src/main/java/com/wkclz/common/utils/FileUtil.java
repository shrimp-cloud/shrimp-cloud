package com.wkclz.common.utils;

import com.wkclz.common.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {


    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);


    public static String getTempPath() {
        return getTempPath(null);
    }
    public static String getTempPath(String customPath) {
        File file = getTempPathFile(customPath);
        return file.getAbsolutePath();
    }
    public static File getTempPathFile() {
        return getTempPathFile(null);
    }
    public static File getTempPathFile(String customPath) {
        Object o = System.getProperties().get("user.dir");
        String savePath =  o.toString() + "/temp/" + (customPath == null ? "": customPath+"/");
        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        return saveDir;
    }


    public static List<String> getFileList(List<String> filesResult, String strPath) {

        if (filesResult == null) {
            filesResult = new ArrayList<>();
        }

        File dir = new File(strPath);
        if (!dir.exists()) {
            return filesResult;
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String absolutePath = files[i].getAbsolutePath();
                if (files[i].isDirectory()) {
                    getFileList(filesResult, absolutePath);
                } else {
                    filesResult.add(absolutePath);
                }
            }
        }
        return filesResult;
    }


    /**
     * 读取文件
     *
     * @param path
     * @return
     */
    public static String readFile(String path) {
        File file = new File(path);
        return readFile(file);
    }

    /**
     * 读取文件
     *
     * @param file
     * @return
     */
    public static String readFile(File file) {
        FileReader reader = null;
        BufferedReader bReader = null;
        try {
            if (!file.isFile()) {
                throw new RuntimeException("error file!");
            }
            reader = new FileReader(file);
            bReader = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String s = "";
            while ((s = bReader.readLine()) != null) {
                sb.append(s + "\n");
            }
            bReader.close();
            return sb.toString();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (bReader != null) {
                try {
                    bReader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static File writeFile(String filePath, String context) {
        return writeFile(new File(filePath), context);
    }
    public static File writeFile(File file, String context) {
        FileWriter writer = null;
        try {
            if (file.exists()) {
                throw BizException.error("文件已存在，无法覆盖： {}", file.getAbsolutePath());
            }
            file.createNewFile();
            writer = new FileWriter(file);
            writer.write(context);
            writer.flush();
            writer.close();
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {

                    // do nothing
                }
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("write {} and close error: {}", file.getAbsoluteFile(), e.getMessage());
                    // do nothing
                }
            }
        }
    }


    public static boolean delFile(String path) {
        File file = new File(path);
        return delFile(file);
    }
    public static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }


    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }


}
