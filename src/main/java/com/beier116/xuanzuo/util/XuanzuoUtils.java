package com.beier116.xuanzuo.util;

import com.beier116.xuanzuo.config.XuanzuoConfig;
import com.beier116.xuanzuo.http.LaiXuanZuoHttpClient;
import com.beier116.xuanzuo.http.Site;
import lombok.extern.slf4j.Slf4j;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class XuanzuoUtils {

    private static boolean getTestCode(String jsText) {
        Pattern r = Pattern.compile("\\w{1}\\(\\w{10}\\)");
        Matcher m = r.matcher(jsText);
        return m.find();
    }

    private static String getJsonStr(String jsText) {
        Pattern r = Pattern.compile("JSON\\.parse\\(\"(\\S+)\"\\)");
        Matcher m = r.matcher(jsText);
        if (m.find())
            return m.group(1);
        return null;
    }

    private static String getDecStr(String jsText) {
        Pattern r = Pattern.compile("dec\\(\"(\\w+)\"\\)");
        Matcher m = r.matcher(jsText);
        if (m.find())
            return m.group(1);
        return null;
    }

    private static String getCodeStr(String jsonStr, String decStr) {
        if (jsonStr == null || decStr == null)
            return null;
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        String codeStr = null;
        try {
            engine.eval(XuanzuoConfig.JS);
            if (engine instanceof Invocable) {
                Invocable invoke = (Invocable) engine;
                codeStr = (String) invoke.invokeFunction("getCode", jsonStr, decStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return codeStr;
    }

    private static String getJsUrl(String html) {
        Pattern r = Pattern.compile(XuanzuoConfig.URL_JS);
        Matcher m = r.matcher(html);
        if (m.find())
            return m.group();
        return null;
    }

    public static String getCodeStr(Site site) {
        LaiXuanZuoHttpClient httpClient = new LaiXuanZuoHttpClient();
        String jsText = null;
        do {
            Object indexHtml = httpClient.get(XuanzuoConfig.indexUrl, site);
            if (indexHtml instanceof Integer) {
                int flag = (Integer) indexHtml;
                if (flag == -1) {
                    log.error("获取主页html失败");
                    return "获取主页html失败";
                } else if (flag == 1) {
                    log.info("已经预定成功，不要重复预订");
                    return "已经预定成功";
                } else if (flag == 2) {
                    try {
                        log.info("预定还未开始，{}ms 后继续", 200);
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (flag == 3) {
                    log.error("sessionID失效，请重新设置定时任务");
                    return "sessionID失效";
                }
            } else if (indexHtml instanceof String) {
                String html = (String) indexHtml;
                String jsUrl = XuanzuoUtils.getJsUrl(html);
                jsText = (String) httpClient.get(jsUrl, site);
            }
        } while (jsText == null || XuanzuoUtils.getTestCode(jsText));

        String jsonStr = XuanzuoUtils.getJsonStr(jsText);
        String decStr = XuanzuoUtils.getDecStr(jsText);
        return XuanzuoUtils.getCodeStr(jsonStr, decStr);
    }
}
