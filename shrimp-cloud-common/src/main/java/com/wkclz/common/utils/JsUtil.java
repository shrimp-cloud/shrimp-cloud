package com.wkclz.common.utils;

import com.wkclz.common.exception.BizException;
import com.wkclz.common.tools.Md5Tool;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.*;

import java.util.HashMap;
import java.util.Map;

public class JsUtil {

    // 保存 Context 上下文
    private static final ThreadLocal<Context> CONTEXT_HOLDER = new ThreadLocal<>();
    // 初始化标准对象（如 global, Function 等）
    private static Scriptable SCOPE = null;
    // JavaScript 函数
    private final static Map<String, Function> JS_FUNCTION = new HashMap<>();

    public static String exec(String script, String param) {
        String funName = getFunName(script);
        Context context = getContext();
        Function function = getFunction(script, funName, context);
        Object[] params = { param };
        Object result = function.call(context, SCOPE, SCOPE, params);
        // 处理返回值
        if (result == null || result instanceof Undefined) {
            return null;
        }
        return result.toString();
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

    private static Function getFunction(String script, String funName, Context context) {
        String hash = Md5Tool.md5(script);
        Function function = JS_FUNCTION.get(hash);

        if (function != null) {
            return function;
        }
        synchronized (hash.intern()) {
            function = JS_FUNCTION.get(hash);
            if (function != null) {
                return function;
            }
            if (SCOPE == null) {
                SCOPE = context.initStandardObjects();
            }

            // 初始化函数
            context.evaluateString(SCOPE, script, funName, 1, null);
            // 获取 JavaScript 函数
            function = (Function) SCOPE.get(funName, SCOPE);
            JS_FUNCTION.put(hash, function);
        }
        return JS_FUNCTION.get(hash);
    }

    private static Context getContext() {
        Context context = CONTEXT_HOLDER.get();
        if (context == null) {
            context = ContextFactory.getGlobal().enterContext();
            CONTEXT_HOLDER.set(context);
        }
        return context;
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
