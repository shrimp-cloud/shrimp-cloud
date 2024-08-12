package com.wkclz.common.tools;

import java.util.Base64;

public class Base64Tool {



    /**
     * byte[] 2 Base64
     */
    public static String base64Encode(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }

    public static String base64Encode(String b) {
        if (b == null){
            return null;
        }
        return Base64.getEncoder().encodeToString(b.getBytes());
    }

    /**
     * Base64 2 byte[]
     */
    public static byte[] base64Decode(String base64Code)  {
        return Base64.getDecoder().decode(base64Code);
    }

    public static String base64Decode2String(String base64Code) {
        byte[] decode = Base64.getDecoder().decode(base64Code);
        return new String(decode);
    }


}
