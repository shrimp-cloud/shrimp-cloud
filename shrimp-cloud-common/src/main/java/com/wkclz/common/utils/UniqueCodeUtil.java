package com.wkclz.common.utils;

import java.util.UUID;

public class UniqueCodeUtil {

    public static String getJavaUuid() {
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        return s.replaceAll("-", "").toLowerCase();
    }
}
