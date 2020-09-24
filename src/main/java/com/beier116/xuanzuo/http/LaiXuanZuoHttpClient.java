package com.beier116.xuanzuo.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class LaiXuanZuoHttpClient {

    public String getCronExample(String cronExpression, Site site) {
        URIBuilder uriBuilder = null;
        try {
            uriBuilder = new URIBuilder("https://api.qqe2.com/cron");
            uriBuilder.setParameter("CronExpression", cronExpression);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        CloseableHttpClient httpClient = getHttpClient(site);
        CloseableHttpResponse httpResponse = null;
        try {
            HttpGet httpGet = createHttpGet(uriBuilder.build(), site);
            httpResponse = httpClient.execute(httpGet);
            log.info("请求：{}", uriBuilder.getPath());
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        } finally {
            try {
                if (httpClient != null)
                    httpClient.close();
                if (httpResponse != null)
                    httpResponse.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    // 单线程
    public Object get(String url, Site site) {
        CloseableHttpClient httpClient = getHttpClient(site);
        CloseableHttpResponse httpResponse = null;
        try {
            HttpGet httpGet = createHttpGet(url, site);
            httpResponse = httpClient.execute(httpGet);
            log.info("请求：{}", url);
            HttpEntity entity = httpResponse.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
//            log.error(result);
            if (!url.endsWith(".js")) {
                if (result.contains("已经预定了"))
                    return 1;
                if (result.contains("不在预约时间内"))
                    return 2;
                if (result.contains("请在微信客户端打开链接"))
                    return 3;
            }
            return result;
        } catch (Exception e) {
            return -1;
        } finally {
            try {
                if (httpClient != null)
                    httpClient.close();
                if (httpResponse != null)
                    httpResponse.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void getPool(List<String> seatUrls, Site site) {
        List<Thread> threads = new ArrayList<>();
        for (final String oneUrl : seatUrls) {
            final CloseableHttpClient httpClient = getHttpClient(site);
            final CloseableHttpResponse[] httpResponses = {null};
            final HttpGet httpGet = createHttpGet(oneUrl, site);
            Thread thread = new Thread(() -> {
                try {
                    httpResponses[0] = httpClient.execute(httpGet);
                    log.info("请求：{}", oneUrl);
                    HttpEntity entity = httpResponses[0].getEntity();
                    String result = EntityUtils.toString(entity, "UTF-8");
                    log.info("结果：{}", result);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (httpClient != null)
                            httpClient.close();
                        if (httpResponses[0] != null)
                            httpResponses[0].close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            threads.add(thread);
        }
        for (final Thread thread : threads) {
            thread.start();
        }
    }

    private HttpGet createHttpGet(String url, Site site) {
        HttpGet httpGet = new HttpGet(url);
        for (Map.Entry<String, String> entry : site.getHeaders().entrySet()) {
            httpGet.addHeader(entry.getKey(), entry.getValue());
        }
        return httpGet;
    }

    private HttpGet createHttpGet(URI url, Site site) {
        HttpGet httpGet = new HttpGet(url);
        for (Map.Entry<String, String> entry : site.getHeaders().entrySet()) {
            httpGet.addHeader(entry.getKey(), entry.getValue());
        }
        return httpGet;
    }

    //获取httpClient
    private CloseableHttpClient getHttpClient(Site site) {
        try {
            SSLContext sslcontext;
            SSLConnectionSocketFactory sslsf;
            sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(null, (TrustStrategy) (arg0, arg1) -> true)
                    .build();
            sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1"},
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            CookieStore cookieStore = new BasicCookieStore();
            for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie.setDomain(site.getDomain());
                cookie.setPath("/");
                cookieStore.addCookie(cookie);
            }
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            if (site.getUserAgent() != null) {
                httpClientBuilder.setUserAgent(site.getUserAgent());
            } else {
                httpClientBuilder.setUserAgent("");
            }
            return httpClientBuilder
                    .setSSLSocketFactory(sslsf)
                    .setDefaultCookieStore(cookieStore)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}
