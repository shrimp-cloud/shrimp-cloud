package com.wkclz.gen;

import com.wkclz.gen.helper.GenHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

@Mojo(
    name="gen",
    defaultPhase= LifecyclePhase.PACKAGE,
    aggregator = true
)
public class GenMojo extends AbstractMojo {

    @Parameter
    private String url;

    @Parameter
    private List<String> options;

    @Parameter(property = "args")
    private String args;

    @Override
    public void execute() {
        Log log = getLog();
        if (StringUtils.isNotBlank(url)){
            log.info("gen url: " + url);
            GenHelper.setBaseUrl(url);
        }
        if (options == null || options.size() == 0){
            log.error("未发现可用的配置");
        }
        for (String option : options) {
            GenHelper.genCode(option, log);
        }
    }

}