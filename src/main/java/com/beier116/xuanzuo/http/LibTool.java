package com.beier116.xuanzuo.http;

import com.beier116.xuanzuo.config.XuanzuoConfig;
import com.beier116.xuanzuo.exceptions.RepeatException;
import com.beier116.xuanzuo.exceptions.SessionExpiredException;
import com.beier116.xuanzuo.exceptions.UnknownException;
import com.beier116.xuanzuo.util.XuanzuoUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LibTool {
    private Site site = new Site();
    private List<String> seatUrls = new ArrayList<>();
    private String codeStr;

    public LibTool(String sessionValue, Boolean flag) {
        site.getCookies().put("wechatSESS_ID", sessionValue);
        if (flag)
            codeStr = XuanzuoUtils.getCodeStr(site);
    }

    public LibTool addSeat(String libId, String x, String y) {
        String bookUrl = "http://wechat.laixuanzuo.com/index.php/prereserve/save/libid=" + libId + "&" + codeStr + "=" + x + "," + y + "&yzm=";
        seatUrls.add(bookUrl);
        return this;
    }

    public void reserve() throws UnknownException, RepeatException, SessionExpiredException {
        if (this.codeStr == null || this.codeStr.equals("获取主页html失败")) {
            throw new UnknownException();
        } else if (this.codeStr.equals("已经预定成功")) {
            throw new RepeatException();
        } else if (this.codeStr.equals("sessionID失效")) {
            throw new SessionExpiredException();
        }
        site.getHeaders().put("Referer", XuanzuoConfig.Referer);
        LaiXuanZuoHttpClient httpClient = new LaiXuanZuoHttpClient();
        httpClient.getPool(seatUrls, site);
    }

    public void keepAlive() {
        site.getHeaders().put("Referer", XuanzuoConfig.Referer);
        LaiXuanZuoHttpClient httpClient = new LaiXuanZuoHttpClient();
        Object indexHtml = httpClient.get(XuanzuoConfig.indexUrl, site);
        if (indexHtml instanceof Integer && (int) indexHtml == 3) {
            log.error("sessionID失效，请重新设置定时任务");
        } else {
            log.info("sessionID存活");
        }
    }
}
