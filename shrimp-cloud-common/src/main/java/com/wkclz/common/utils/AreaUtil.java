package com.wkclz.common.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.wkclz.common.entity.AreaEntity;
import com.wkclz.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AreaUtil {

    private static final String BASE_URL = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2021/";
    private static int RETRY_TIMES = 0;
    private static int V1_COUNT = 0;
    private static int V2_COUNT = 0;
    private static int V3_COUNT = 0;
    private static int V4_COUNT = 0;
    private static int V5_COUNT = 0;

    private static final String TEMP_FILE = System.getProperty("user.dir") + "/logs/areas/";

    public static void main(String[] args) {
        List<AreaEntity> areas = getAreas();
        System.out.println("共获取" + areas.size() + "个区域");
        /*
        for (AreaEntity area : areas) {
            System.out.println(area.getAreaCode());
        }
        */
    }

    public static List<AreaEntity> getAreas() {
        List<AreaEntity> areas = new ArrayList<>();
        getProvinces(BASE_URL + "index.html", areas, 0L);
        return areas;
    }

    /**
     * 获取所有省
     *
     * @param url
     * @param areas
     */
    private static void getProvinces(String url, List<AreaEntity> areas, Long parentCode) {
        Document provinceDoc = getDoc(url);
        if (provinceDoc == null) {
            return;
        }
        Elements mastheads = provinceDoc.select("tr.provincetr");
        for (Element masthead : mastheads) {
            Elements resultLinks = masthead.select("td > a");
            for (Element ddd : resultLinks) {
                // 省
                String href = ddd.attr("href");
                String name = ddd.text();
                String code = href.substring(0, 2) + "0000000000";

                AreaEntity area = new AreaEntity();
                area.setParentAreaCode(parentCode);
                area.setLevel(1);
                area.setIsLeaf(0);
                Long areaCode = Long.valueOf(code);
                area.setAreaCode(areaCode);
                area.setName(name);
                areas.add(area);
                V1_COUNT ++;

                // 市 url
                String cityUrl = BASE_URL + href;
                System.out.println(V1_COUNT +"-"+ V2_COUNT +"-"+ V3_COUNT +"-"+ V4_COUNT +"-"+ V5_COUNT + ":" + name+":cityUrl: " + cityUrl);
                getCitys(cityUrl, areas, areaCode);
            }
        }

    }

    /**
     * 获取所有市
     *
     * @param url
     * @param areas
     */
    private static void getCitys(String url, List<AreaEntity> areas, Long parentCode) {
        Document cityDoc = getDoc(url);

        if (cityDoc == null) {
            return;
        }
        url = url.substring(0, url.lastIndexOf("."));
        url = url.substring(0, url.lastIndexOf("/"));
        url = url + "/";

        Elements mastheads = cityDoc.select("tr.citytr");
        for (Element masthead : mastheads) {

            Elements tds = masthead.select("td");
            String code = tds.get(0).text();
            String name = tds.get(1).text();

            AreaEntity area = new AreaEntity();
            area.setParentAreaCode(parentCode);
            area.setLevel(2);
            area.setIsLeaf(0);
            Long areaCode = Long.valueOf(code);
            area.setAreaCode(areaCode);
            area.setName(name);
            areas.add(area);
            V2_COUNT ++;

            // 区/县 url
            Elements a1 = tds.get(0).select("a");
            if (a1 == null) {
                area.setIsLeaf(1);
                continue;
            }
            String current = a1.get(0).attr("href");
            if (StringUtils.isBlank(current)) {
                area.setIsLeaf(1);
                continue;
            }
            String countyUrl = url + current;
            System.out.println(V1_COUNT +"-"+ V2_COUNT +"-"+ V3_COUNT +"-"+ V4_COUNT +"-"+ V5_COUNT + ":" + name + ":countyUrl: " + countyUrl);
            getCountys(countyUrl, areas, areaCode);
        }
    }

    /**
     * 获取所有县
     *
     * @param url
     * @param areas
     */
    private static void getCountys(String url, List<AreaEntity> areas, Long parentCode) {
        Document countryDoc = getDoc(url);

        if (countryDoc == null) {
            return;
        }
        url = url.substring(0, url.lastIndexOf("."));
        url = url.substring(0, url.lastIndexOf("/"));
        url = url + "/";

        Elements mastheads = countryDoc.select("tr.countytr");
        for (Element masthead : mastheads) {

            Elements tds = masthead.select("td");
            String code = tds.get(0).text();
            String name = tds.get(1).text();

            AreaEntity area = new AreaEntity();
            area.setParentAreaCode(parentCode);
            area.setLevel(3);
            area.setIsLeaf(0);
            Long areaCode = Long.valueOf(code);
            area.setAreaCode(areaCode);
            area.setName(name);
            areas.add(area);
            V3_COUNT ++;

            // 乡镇 url
            Elements a1 = tds.get(0).select("a");
            if (a1 == null || a1.isEmpty()) {
                area.setIsLeaf(1);
                continue;
            }
            String current = a1.get(0).attr("href");
            if (StringUtils.isBlank(current)) {
                area.setIsLeaf(1);
                continue;
            }

            String townUrl = url + current;
            System.out.println(V1_COUNT +"-"+ V2_COUNT +"-"+ V3_COUNT +"-"+ V4_COUNT +"-"+ V5_COUNT + ":" + name + ":townUrl: " + townUrl);
            getTowns(townUrl, areas, areaCode);
        }
    }

    /**
     * 获取所有乡镇
     *
     * @param url
     * @param areas
     */
    private static void getTowns(String url, List<AreaEntity> areas, Long parentCode) {
        Document countryDoc = getDoc(url);
        if (countryDoc == null) {
            return;
        }
        url = url.substring(0, url.lastIndexOf("."));
        url = url.substring(0, url.lastIndexOf("/"));
        url = url + "/";

        Elements mastheads = countryDoc.select("tr.towntr");
        for (Element masthead : mastheads) {

            Elements tds = masthead.select("td");
            String code = tds.get(0).text();
            String name = tds.get(1).text();

            AreaEntity area = new AreaEntity();
            area.setParentAreaCode(parentCode);
            area.setLevel(4);
            area.setIsLeaf(0);
            Long areaCode = Long.valueOf(code);
            area.setAreaCode(areaCode);
            area.setName(name);
            areas.add(area);
            V4_COUNT ++;

            // 乡镇 url
            Elements a1 = tds.get(0).select("a");
            if (a1 == null) {
                area.setIsLeaf(1);
                continue;
            }
            String current = a1.get(0).attr("href");
            if (StringUtils.isBlank(current)) {
                area.setIsLeaf(1);
                continue;
            }

            String villagetrUrl = url + current;
            System.out.println(V1_COUNT +"-"+ V2_COUNT +"-"+ V3_COUNT +"-"+ V4_COUNT +"-"+ V5_COUNT + ":" + name + ":villagetrUrl: " + villagetrUrl);
            // getVillagetrs(villagetrUrl, areas, areaCode);
        }
    }

    /**
     * 获取所有村/居委会/街道
     *
     * @param url
     * @param areas
     */
    private static void getVillagetrs(String url, List<AreaEntity> areas, Long parentCode) {
        Document countryDoc = getDoc(url);
        if (countryDoc == null) {
            return;
        }
        Elements mastheads = countryDoc.select("tr.villagetr");
        for (Element masthead : mastheads) {

            Elements tds = masthead.select("td");
            String code = tds.get(0).text();
            String typeCode = tds.get(1).text();
            String name = tds.get(2).text();

            AreaEntity area = new AreaEntity();
            area.setParentAreaCode(parentCode);
            area.setLevel(5);
            area.setAreaCode(Long.valueOf(code));
            area.setTypeCode(Integer.valueOf(typeCode));
            area.setName(name);
            area.setIsLeaf(1);
            areas.add(area);
            V5_COUNT ++;
        }
    }

    /**
     * 从指定 Url 获取文档
     *
     * @param urlStr
     * @return
     */
    private static Document getDoc(String urlStr) {

        // 本地缓存
        File baseFile = new File(TEMP_FILE);
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        // http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2017/13/07/30/130730110.html
        String xPath = urlStr.substring(urlStr.indexOf("/tjyqhdmhcxhfdm/") + "/tjyqhdmhcxhfdm/".length());
        xPath = xPath.substring(0, xPath.lastIndexOf("/"));
        File savePath = new File(TEMP_FILE + xPath);
        if (!savePath.exists()) {
            savePath.mkdirs();
        }
        File file2Save = new File(savePath + urlStr.substring(urlStr.lastIndexOf("/")));

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            Document doc;

            if (file2Save.exists()) {
                doc = Jsoup.parse(file2Save, "UTF-8", urlStr);
            } else {
                HttpRequest get = HttpUtil.createGet(urlStr);
                get.header("User-Agent","PostmanRuntime/7.26.8"+V5_COUNT+" Safari/537."+V5_COUNT);
                get.header("Referer", urlStr.substring(0, urlStr.lastIndexOf("/")+1));
                get.cookie("SF_cookie_1="+V5_COUNT+"; _trs_uv=l13antvx_"+V5_COUNT+"_eeu; wzws_cid=b7bde805496522b5663cd78130f10705a35e24a98e0a1dc6783fc9a931937279a1a6e6fa61cdc80736d5ce7ec36aaf7aa96837d025e326ad668ca0912988ecd74fd50915767a150cfa0944b37ba33f5f");
                HttpResponse execute = get.execute();
                if (execute.getStatus() != 200) {
                    throw BizException.error(execute.getStatus() + ": " +urlStr);
                }
                String html = execute.body();
                if (html.contains("请开启JavaScript并刷新该页")) {
                    throw BizException.error("请开启JavaScript并刷新该页: " +urlStr);
                }

                fos = new FileOutputStream(file2Save, false);
                osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                osw.write(html);
                osw.flush();

                doc = Jsoup.parse(file2Save, "UTF-8", urlStr);
            }

            RETRY_TIMES = 0;
            return doc;
        } catch (Exception e) {
            if (RETRY_TIMES++ < 60) {
                try {
                    Thread.sleep(RETRY_TIMES * 10000L);
                } catch (InterruptedException e2) {
                    System.err.println(e2.getMessage());
                }
                System.out.println("休息"+(RETRY_TIMES * 5)+"秒，失败尝试次数：" + RETRY_TIMES + ", 异常为： " + e.getMessage());
                return getDoc(urlStr);
            }
            System.out.println("太惨了。。被限制了。。" + e.getMessage());
            System.exit(0);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return null;
    }

}
