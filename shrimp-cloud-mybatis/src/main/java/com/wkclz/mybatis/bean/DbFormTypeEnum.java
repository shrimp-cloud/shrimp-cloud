package com.wkclz.mybatis.bean;

/**
 * 数据库字段，和前端 input 类型的映射关系
 * @author shrimp
 */
public enum DbFormTypeEnum {

    INT("Integer", "Number"),
    SMALLINT("Integer", "Number"),
    INTEGER("Integer", "Number"),
    MEDIUMINT("Integer", "Number"),
    TINYINT("Integer", "Number"),

    BIGINT("Long", "Number"),
    // BIGINT UNSIGNED TODO 如果遇到，需要确认是否有下划线
    BIGINT_UNSIGNED("Long", "Number"),

    FLOAT("Float", "Number"),

    DOUBLE("Double", "Number"),
    REAL("Double", "Number"),

    CHAR("String", "String"),
    VARCHAR("String", "String"),
    TEXT("String", "String"),
    MEDIUMTEXT("String", "String"),
    TINYTEXT("String", "String"),
    LONGTEXT("String", "String"),
    JSON("String", "String"),

    TIME("Date", "String"),
    DATE("Date", "Date"),
    DATETIME("Date", "Datetime"),
    TIMESTAMP("Date", "Number"),
    YEAR("Date", "Number"),

    DECIMAL("BigDecimal", "Number"),
    NUMERIC("BigDecimal", "Number"),

    ;



    // 不考虑的类型：ENUM,SET,SMALLINT

    private String javaType;
    private String inputType;

    DbFormTypeEnum(String javaType, String inputType) {
        this.javaType = javaType;
        this.inputType = inputType;
    }

    public String getJavaType() {
        return javaType;
    }
    public String getInputType() {
        return inputType;
    }

}
