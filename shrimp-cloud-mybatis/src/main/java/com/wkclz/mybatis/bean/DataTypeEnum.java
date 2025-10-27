package com.wkclz.mybatis.bean;

/**
 * 数据库字段，和Java, 前端ts, input 类型的映射关系
 * @author shrimp
 */

public enum DataTypeEnum {


    BIT("Boolean", "string", "TEXT"),

    INT("Integer", "number", "NUMBER"),
    SMALLINT("Integer", "number", "NUMBER"),
    INTEGER("Integer", "number", "NUMBER"),
    MEDIUMINT("Integer", "number", "NUMBER"),
    TINYINT("Integer", "number", "NUMBER"),

    BIGINT("Long", "number", "NUMBER"),
    // BIGINT UNSIGNED TODO 如果遇到，需要确认是否有下划线
    BIGINT_UNSIGNED("Long", "number", "NUMBER"),

    FLOAT("Float", "number", "NUMBER"),

    DOUBLE("Double", "number", "NUMBER"),
    REAL("Double", "number", "NUMBER"),

    CHAR("String", "string", "TEXT"),
    VARCHAR("String", "string", "TEXT"),
    TEXT("String", "string", "TEXTAREA"),
    MEDIUMTEXT("String", "string", "TEXT"),
    TINYTEXT("String", "string", "TEXT"),
    LONGTEXT("String", "string", "TEXTAREA"),
    JSON("String", "string", "TEXTAREA"),

    TIME("Date", "number", "NUMBER"),
    DATE("Date", "number", "DATE"),
    DATETIME("Date", "number", "DATETIME"),
    TIMESTAMP("Date", "number", "NUMBER"),
    YEAR("Date", "number", "NUMBER"),

    DECIMAL("BigDecimal", "number", "NUMBER"),
    NUMERIC("BigDecimal", "number", "NUMBER"),

    BINARY("byte[]", "any", "TEXT"),
    BLOB("byte[]", "any", "TEXTAREA"),
    GEOMETRY("byte[]", "any", "TEXT"),
    LONGBLOB("byte[]", "any", "TEXT"),
    MEDIUMBLOB("byte[]", "any", "TEXT"),
    TINYBLOB("byte[]", "any", "TEXT"),
    VARBINARY("byte[]", "any", "TEXT"),

    GEOMETRYCOLLECTION("Object", "any", "TEXT"),
    LINESTRING("Object", "any", "TEXT"),
    MULTILINESTRING("Object", "any", "TEXT"),
    MULTIPOINT("Object", "any", "TEXT"),
    MULTIPOLYGON("Object", "any", "TEXT"),
    POINT("Object", "any", "TEXT"),
    POLYGON("Object", "any", "TEXT"),
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
