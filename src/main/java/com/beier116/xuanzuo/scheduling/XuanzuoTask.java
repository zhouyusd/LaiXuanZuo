package com.beier116.xuanzuo.scheduling;

import com.beier116.xuanzuo.exceptions.RepeatException;
import com.beier116.xuanzuo.exceptions.SessionExpiredException;
import com.beier116.xuanzuo.exceptions.UnknownException;
import com.beier116.xuanzuo.http.LibTool;
import org.springframework.stereotype.Component;

@Component("xuanzuoTask")
public class XuanzuoTask {
    public void keepAlive(String params) {
        String[] params1 = params.split("@");
        LibTool libTool = new LibTool(params1[0], false);
        libTool.keepAlive();
    }

    public void reserve(String params) throws RepeatException, SessionExpiredException, UnknownException {
        String[] params1 = params.split("@");
        assert params1.length == 2;
        LibTool libTool = new LibTool(params1[0], true);
        String[] params2 = params1[1].split(",");
        assert params2.length % 3 == 0;
        for (int i = 0; i < params2.length; i += 3) {
            libTool.addSeat(params2[i], params2[i + 1], params2[i + 2]);
        }
        libTool.reserve();
    }
}