package com.wkclz.mybatis.bean;

/**
 * 数据库字段，和Java, 前端ts, input 类型的映射关系
 * @author shrimp
 */

public enum DataTypeEnum {


    BIT("Boolean", "string", "string"),

    INT("Integer", "number", "number"),
    SMALLINT("Integer", "number", "number"),
    INTEGER("Integer", "number", "number"),
    MEDIUMINT("Integer", "number", "number"),
    TINYINT("Integer", "number", "number"),

    BIGINT("Long", "number", "number"),
    // BIGINT UNSIGNED TODO 如果遇到，需要确认是否有下划线
    BIGINT_UNSIGNED("Long", "number", "number"),

    FLOAT("Float", "number", "number"),

    DOUBLE("Double", "number", "number"),
    REAL("Double", "number", "number"),

    CHAR("String", "string", "string"),
    VARCHAR("String", "string", "string"),
    TEXT("String", "string", "string"),
    MEDIUMTEXT("String", "string", "string"),
    TINYTEXT("String", "string", "string"),
    LONGTEXT("String", "string", "string"),
    JSON("String", "string", "string"),

    TIME("Date", "number", "number"),
    DATE("Date", "number", "number"),
    DATETIME("Date", "number", "number"),
    TIMESTAMP("Date", "number", "number"),
    YEAR("Date", "number", "number"),

    DECIMAL("BigDecimal", "number", "number"),
    NUMERIC("BigDecimal", "number", "number"),


    BINARY("byte[]", "any", "string"),
    BLOB("byte[]", "any", "string"),
    GEOMETRY("byte[]", "any", "string"),
    LONGBLOB("byte[]", "any", "string"),
    MEDIUMBLOB("byte[]", "any", "string"),
    TINYBLOB("byte[]", "any", "string"),
    VARBINARY("byte[]", "any", "string"),

    GEOMETRYCOLLECTION("Object", "any", "string"),
    LINESTRING("Object", "any", "string"),
    MULTILINESTRING("Object", "any", "string"),
    MULTIPOINT("Object", "any", "string"),
    MULTIPOLYGON("Object", "any", "string"),
    POINT("Object", "any", "string"),
    POLYGON("Object", "any", "string"),
    ;

    // 不考虑的类型：ENUM,SET,SMALLINT

    private String javaType;
    private String tsType;
    private String inputType;

    DataTypeEnum(String javaType, String tsType, String inputType) {
        this.javaType = javaType;
        this.tsType = tsType;
        this.inputType = inputType;
    }

    public String getJavaType() {
        return javaType;
    }
    public String getTsType() {
        return tsType;
    }
    public String getInputType() {
        return inputType;
    }

}
