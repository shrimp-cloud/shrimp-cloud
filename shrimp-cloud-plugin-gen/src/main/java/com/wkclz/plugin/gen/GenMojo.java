package com.wkclz.plugin.gen;

import com.wkclz.plugin.gen.helper.GenHelper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

@Mojo(
    name = "gen",
    defaultPhase = LifecyclePhase.PACKAGE,
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
        if (url != null && !url.trim().isEmpty()) {
            log.info("gen url: " + url);
            GenHelper.setBaseUrl(url);
        }
        if (options == null || options.isEmpty()) {
            log.error("未发现可用的配置");
        }
        for (String option : options) {
            GenHelper.genCode(option, log);
        }
    }

}