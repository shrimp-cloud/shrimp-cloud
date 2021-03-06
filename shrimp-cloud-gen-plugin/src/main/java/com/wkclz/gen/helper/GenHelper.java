package com.wkclz.gen.helper;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wkclz.gen.bean.GenResult;
import com.wkclz.gen.bean.GenTaskInfo;
import com.wkclz.gen.utils.CompressUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class GenHelper {

    // 已经部署好的机器
    private static String BASE_URL = "http://gen.wkclz.com/gen";
    private static String AUTH_CODE = null;


    public static boolean genCode(String baseUrl, String authCode, Log log) {
        BASE_URL = baseUrl;
        AUTH_CODE = authCode;
        return genCode(log);
    }

    public static boolean genCode(String authCode, Log log) {
        AUTH_CODE = authCode;
        return genCode(log);
    }


    public static boolean genCode(Log log) {
        String authCode = AUTH_CODE;

        if (StringUtils.isBlank(authCode)){
            log.error("authCode can not be null");
            return false;
        }

        long start = System.currentTimeMillis();

        try {
            String urlStr = getGenAddr(authCode, log);
            URL url = new URL(urlStr);
            System.out.println("=======> download addr: "+ url.getPath());

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(3*1000);
            conn.setRequestProperty("User-Agent", "lz-gen client");

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200){
                log.error("网络请求错误: " + conn.getResponseMessage());
                return false;
            }
            //获取文件信息
            byte[] getData = readInputStream(inputStream);
            String contentType = conn.getHeaderField("Content-Type");
            if (StringUtils.isNotBlank(contentType) && contentType.contains("application/json")) {
                JSONObject obj = JSON.parseObject(new String(getData));
                Object msg = obj.get("msg");
                log.error("代码生成异常: " + msg);
                return false;
            }

            String savePath = getSavePath(conn, log).replace("\\","/");

            // 保存文件
            File file = new File(savePath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);

            // 解压
            int lastSeparator = savePath.lastIndexOf("/");
            String saveDir = savePath.substring(0, lastSeparator);
            CompressUtil.unZip(file, saveDir);

            // 删除压缩文件
            file.delete();

            // 替换
            int lastPort = savePath.lastIndexOf(".");
            String genSrc = savePath.substring(0, lastPort);
            String tagSrc = System.getProperties().get("user.dir").toString();
            List<GenTaskInfo> taskInfos = getRule(authCode, log);
            for (GenTaskInfo taskInfo:taskInfos) {

                // 对于不需要生成的 task， 直接跳过
                Integer needCreate = taskInfo.getNeedCreate();
                if (needCreate == null || needCreate != 1){
                    continue;
                }
                // 特例：baseDao 已经在 MBG 生成时已干掉了，代码覆盖不处理
                if ("baseDao".equals(taskInfo.getTaskName())){
                    continue;
                }

                boolean deleteFlag = taskInfo.getNeedDelete() != null && taskInfo.getNeedDelete() == 1;
                String relativePath = ""
                    + "/" + taskInfo.getProjectBasePath()
                    + "/" + taskInfo.getPackagePath().replaceAll("\\.", "/");

                String genPath = genSrc + relativePath;
                File genPathDirectory = new File(genPath);

                String tagPath = tagSrc + relativePath;
                File tagPathDirectory = new File(tagPath);
                if (!tagPathDirectory.exists()){
                    tagPathDirectory.mkdirs();
                }

                File[] genFiles = genPathDirectory.listFiles();
                for (File genFile:genFiles) {
                    File tagFile = new File(tagPath + "/" + genFile.getName());
                    // 如果存在并定义为删除，直接删除
                    // 特例1. *Example.java 为特例，MBG 生产模型时自动生成，直接删除即可
                    if ((tagFile.exists() && deleteFlag) ||  genFile.getName().endsWith("Example.java")){
                        log.info("=======> 正在删除文件: "+ tagFile.getPath());
                        tagFile.delete();
                    }
                    // 若不存在（或者存在并已删除），都创建
                    if (!tagFile.exists()){
                        log.info("=======> 正在复制文件: "+ tagFile.getPath());
                        Files.copy(genFile.toPath(), tagFile.toPath());
                    }
                }
            }

            delFile(genSrc);


            long end = System.currentTimeMillis();
            log.info("=======> 完成代码生成, 耗时 "+(end - start)+"ms <=========");


        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }




    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 获取文件保存路径
     * @param conn
     * @return
     */
    private static String getSavePath(HttpURLConnection conn, Log log){

        Object o = System.getProperties().get("user.dir");
        String userDir = o.toString();
        String savePath = userDir + "/temp/gen/";

        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdirs();
        }

        log.info("=======> save path: "+ savePath);

        String fileName = "gen.zip";
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> dispositions = headerFields.get("Content-Disposition");
        if (dispositions != null){
            String sign = "filename=";
            for (String d:dispositions) {
                if (d.contains(sign)){
                    fileName = d.substring(d.indexOf(sign)+ sign.length());
                    break;
                }
            }
        }

        String filePath = saveDir+File.separator+fileName;

        return filePath;
    }

    /**
     * 读取生成配置
     * @return
     */
    private static List<GenTaskInfo> getRule(String authCode, Log log){
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String str = null;
        try {
            String urlStr = getGenRule(authCode, log);
            URL url = new URL(urlStr);
            log.info("=======> rule addr: "+ url.getPath());

            HttpURLConnection roleConn = (HttpURLConnection)url.openConnection();
            roleConn.setConnectTimeout(3*1000);
            roleConn.setRequestProperty("Content-Type", "application/json");
            roleConn.setRequestProperty("Connection", "Keep-Alive");
            roleConn.setRequestProperty("Charset", "UTF-8");
            roleConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            int ruleStatus = roleConn.getResponseCode();
            if (ruleStatus != 200){
                log.error("网络请求错误：" + roleConn.getResponseMessage());
                return null;
            }

            is = roleConn.getInputStream();
            isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            br = new BufferedReader(isr);
            StringBuilder buffer = new StringBuilder();
            while((str = br.readLine()) != null){
                buffer.append(str);
            }
            str = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null){
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (str == null){
            return null;
        }

        GenResult result = JSON.parseObject(str, GenResult.class);
        if (!result.isSuccess()){
            log.error("调用规则查询失败!");
            return null;
        }
        Object data = result.getData();
        List<GenTaskInfo> genTaskInfos = JSON.parseArray(data.toString(), GenTaskInfo.class);
        return genTaskInfos;
    }


    private static String getGenAddr(String authCode, Log log){
        if (StringUtils.isBlank(authCode)){
            log.error("authCode can not be blank");
            return null;
        }
        return BASE_URL + "/gen/" + authCode;
    }
    private static String getGenRule(String authCode, Log log){
        if (StringUtils.isBlank(authCode)){
            log.error("authCode can not be blank");
        }
        return BASE_URL + "/gen/rule/" + authCode;
    }


    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    public static void setAuthCode(String authCode) {
        AUTH_CODE = authCode;
    }


    private static boolean delFile(String path) {
        File file = new File(path);
        return delFile(file);
    }
    private static boolean delFile(File file) {
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
}
