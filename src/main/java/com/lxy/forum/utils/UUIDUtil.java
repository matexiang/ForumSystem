package com.lxy.forum.utils;

import java.util.UUID;

public class UUIDUtil {

    public static String UUID36(){
        return UUID.randomUUID().toString();
    }

    public static String UUID32(){
        return UUID.randomUUID().toString().replace("-","");
    }


}
