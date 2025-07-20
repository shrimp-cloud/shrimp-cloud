package com.wkclz.xxljob.demo;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

@Component
public class XxlJobDemo {
    @XxlJob("demoJobHandler")
    public void demoJobHandler() {
        XxlJobHelper.log("XXL-JOB, Hello World.");
    }

}