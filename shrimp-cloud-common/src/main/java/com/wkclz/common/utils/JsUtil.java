package com.wkclz.common.utils;

import com.alibaba.fastjson2.JSONObject;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.tools.Md5Tool;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.*;

import java.util.HashMap;
import java.util.Map;

public class JsUtil {

    // 初始化标准对象（如 global, Function 等）
    private static Scriptable scope = null;
    // JavaScript 函数
    private static final Map<String, Function> JS_FUNCTION = new HashMap<>();

    public static String exec(String script, String param) {
        Object[] params = { param };
        return exec(script, params);
    }
    public static String exec(String script, JSONObject param) {
        Object[] params = { param };
        return exec(script, params);
    }
    public static String exec(String script, Map<String, Object> param) {
        Object[] params = { param };
        return exec(script, params);
    }
    private static String exec(String script, Object[] params) {
        String funName = getFunName(script);
        try {
            Context context = ContextFactory.getGlobal().enterContext();
            Function function = getFunction(script, funName, context);
            Object result = function.call(context, scope, scope, params);
            // 处理返回值
            if (result == null || result instanceof Undefined) {
                return null;
            }
            return result.toString();
        } catch (Exception e) {
            throw BizException.error(e.getMessage());
        }
    }

    private static String getFunName(String js) {
        if (StringUtils.isBlank(js)) {
            throw BizException.error("js is empty!");
        }
        js = js.replaceAll("\\s+", " ").trim();
        if (!js.startsWith("function ") || !js.contains("(")) {
            throw BizException.error("error js: " + js);
        }
        return js.substring(9, js.indexOf("("));
    }

    private static synchronized Function getFunction(String script, String funName, Context context) {
        String hash = Md5Tool.md5(script);
        Function function = JS_FUNCTION.get(hash);

        if (function != null) {
            return function;
        }
        if (scope == null) {
            scope = context.initStandardObjects();
        }

        // 初始化函数
        context.evaluateString(scope, script, funName, 1, null);
        // 获取 JavaScript 函数
        function = (Function) scope.get(funName, scope);
        JS_FUNCTION.put(hash, function);

        return function;
    }

    private static String getJsScript() {
        return """
            function    aaa(param)   {
               return "test:" + param;
            }
            """;
    }

    public static void main(String[] args) {
        String js = getJsScript();
        String exec2 = exec(js, "222");
        String exec3 = exec(js, "33");
        System.out.println(exec2);
        System.out.println(exec3);
    }

}
