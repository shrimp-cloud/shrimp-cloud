package com.wkclz.mybatis.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author shrimp
 */
@Data
@Configuration
public class DbScriptInitConfig {

    // 总开关
    @Value("${db-script.init.enabled:0}")
    private Integer enabled;

    // 表结构自动维护开关
    @Value("${db-script.init.auto-create-table:1}")
    private Integer autoCreateTable;

    @Value("${db-script.init.auto-insert-data:1}")
    private Integer autoInsertData;

    @Value("${db-script.init.auto-exec-add-column:0}")
    private Integer autoExecAddColumn;

    @Value("${db-script.init.auto-exec-modify-column:0}")
    private Integer autoExecModifyColumn;

    @Value("${db-script.init.auto-exec-drop-column:0}")
    private Integer autoExecDropColumn;

    @Value("${db-script.init.auto-exec-add-index:0}")
    private Integer autoExecAddIndex;

    @Value("${db-script.init.auto-exec-drop-index:0}")
    private Integer autoExecDropIndex;



}
