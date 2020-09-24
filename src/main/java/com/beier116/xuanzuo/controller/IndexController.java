package com.beier116.xuanzuo.controller;

import com.beier116.xuanzuo.common.RestResponse;
import com.beier116.xuanzuo.http.LaiXuanZuoHttpClient;
import com.beier116.xuanzuo.http.Site;
import com.beier116.xuanzuo.scheduling.CronTaskRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @RequestMapping("/")
    public String index() {
        return "redirect:/tasks";
    }

    @ResponseBody
    @GetMapping("/general/tasknum")
    public RestResponse<Integer> taskNum() {
        return RestResponse.ok("/general/tasknum", cronTaskRegistrar.getScheduledTasks().size());
    }

    @ResponseBody
    @GetMapping("/cron/example")
    public String cronExample(@RequestParam(value = "cronExpression") String cronExpression) {
        String result = redisTemplate.opsForValue().get(cronExpression);
        if (result != null)
            return result;
        Site site = new Site();
        site.getHeaders().put("Referer", "https://cron.qqe2.com/");
        site.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
        LaiXuanZuoHttpClient httpClient = new LaiXuanZuoHttpClient();
        result = httpClient.getCronExample(cronExpression, site);
        redisTemplate.opsForValue().set(cronExpression, result, 1, TimeUnit.HOURS);
        return result;
    }
}
