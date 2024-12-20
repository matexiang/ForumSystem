package com.lxy.forum.utils;

import org.apache.commons.codec.digest.DigestUtils;


public class MD5Util {

     //对字符串进行MD5加密
    public static String md5 (String str) {
        return DigestUtils.md5Hex(str);
    }

    /**
     * 对用户密码进行加密
     * @param str 密码明文
     * @param salt 扰动字符
     * @return 密文
     */
    public static String md5Salt (String str, String salt) {
        return md5(md5(str) + salt);
    }
}

