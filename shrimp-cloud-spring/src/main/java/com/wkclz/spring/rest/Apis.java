package com.wkclz.spring.rest;

import cn.hutool.core.util.StrUtil;
import com.wkclz.common.entity.Result;
import com.wkclz.common.exception.BizException;
import com.wkclz.spring.entity.RestInfo;
import com.wkclz.spring.utils.RestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class Apis {

    @GetMapping(Routes.APIS_LIST)
    public Result apisList(){
        List<RestInfo> mapping = RestUtil.getMapping();
        return Result.data(mapping);
    }

    @GetMapping(Routes.APIS_CODE_V1)
    public Result apisCodeV1(HttpServletRequest request, String router){
        List<RestInfo> mappings = getMappings(request, router);

        String lineSeparator = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append("import request from '@/utils/request';");
        sb.append(lineSeparator).append(lineSeparator);
        for (RestInfo mapping :mappings) {
            String funTemp;
            if (RequestMethod.GET.name().equals(mapping.getMethod())) {
                funTemp = "// {}\nexport function {}(params: any) {\n  return request({ url: '{}', method: 'get', params });\n}";
            } else {
                funTemp = "// {}\nexport function {}(data: any) {\n  return request({ url: '{}', method: 'post', data });\n}";
            }
            String fun = StrUtil.format(funTemp, mapping.getDesc(), mapping.getName(), router + mapping.getUri());
            sb.append(fun).append(lineSeparator);
        }
        return Result.data(sb.toString());
    }

    @GetMapping(Routes.APIS_CODE_V2)
    public Result apisCodeV2(HttpServletRequest request, String router){
        List<RestInfo> mappings = getMappings(request, router);

        String lineSeparator = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append("import request from '@/utils/request';");
        sb.append(lineSeparator).append(lineSeparator);
        for (RestInfo mapping :mappings) {
            String funTemp;
            if (RequestMethod.GET.name().equals(mapping.getMethod())) {
                funTemp = "// {}\nexport const {} = (params: any) => request({ url: '{}', method: 'get', params })\n";
            } else {
                funTemp = "// {}\nexport const {} = (data: any) => request({ url: '{}', method: 'post', data })\n";
            }
            String fun = StrUtil.format(funTemp, mapping.getDesc(), mapping.getName(), router + mapping.getUri());
            sb.append(fun).append(lineSeparator);
        }
        return Result.data(sb.toString());
    }

    @GetMapping(Routes.APIS_CODE_V3)
    public Result apisCodeV3(HttpServletRequest request, String router){
        List<RestInfo> mappings = getMappings(request, router);

        String lineSeparator = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append("import request from '@/utils/request';");
        sb.append(lineSeparator).append(lineSeparator);
        for (RestInfo mapping :mappings) {
            String funTemp;
            if (RequestMethod.GET.name().equals(mapping.getMethod())) {
                funTemp = "// {}\nexport function {}(params) {\n  return request({ url: '{}', method: 'get', params });\n}";
            } else {
                funTemp = "// {}\nexport function {}(data) {\n  return request({ url: '{}', method: 'post', data });\n}";
            }
            String fun = StrUtil.format(funTemp, mapping.getDesc(), mapping.getName(), router + mapping.getUri());
            sb.append(fun).append(lineSeparator);
        }
        return Result.data(sb.toString());
    }

    private static List<RestInfo> getMappings(HttpServletRequest request, String router) {
        if (StringUtils.isBlank(router)) {
            Object module = request.getAttribute("module");
            if (module != null){
                router = "/" + module;
            }
        }
        if (StringUtils.isBlank(router)) {
            throw BizException.error("router can not be null");
        }
        if (!router.startsWith("/")) {
            router = "/" + router;
        }
        List<RestInfo> mappings = RestUtil.getMapping("com.wkclz." + router.replace("/",""));
        mappings = mappings.stream().filter(m->!m.getUri().contains("{")).collect(Collectors.toList());
        return mappings;
    }

}
