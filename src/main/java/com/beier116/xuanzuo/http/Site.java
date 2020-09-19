package com.beier116.xuanzuo.http;

import com.beier116.xuanzuo.config.XuanzuoConfig;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Site {
    private String domain = XuanzuoConfig.DOMAIN;
    private String userAgent = XuanzuoConfig.UserAgent;
    private Map<String, String> cookies = new LinkedHashMap<>();
    private Map<String, String> headers = new HashMap<>();
}
