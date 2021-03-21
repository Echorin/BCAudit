package com.oschain.fastchaindb.common.utils;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Util {

    /***
     * 利用Apache的工具类实现SHA-256加密
     * @param str 加密后的报文
     * @return
     */
    public static String getSHA256Str(byte[] bytes){
        MessageDigest messageDigest;
        String encdeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(bytes);
            encdeStr = Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encdeStr;
    }

    /**
     * 　　* 利用java原生的摘要实现SHA256加密
     * 　　* @param str 加密后的报文
     * 　　* @return
     *
     */
    public static String getSHA256StrJava(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
     * 　　* 将byte转为16进制
     * 　　* @param bytes
     * 　　* @return
     *
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
//    public static void main(String[] args) throws Exception {
//        String as="sdf";
//        byte[] s = as.getBytes();
//
//
//        System.out.println(SHA256Util.getSHA256Str(s));
//
//        System.out.println("加密结果>>>" +SHA256Util.getSHA256StrJava(as));
//          as="sdfddaf加密结果加密结果加密结果加密结果加密结果加密结果加密结果加密结果byte元素是由8位 2进制组成的，字符串内部实际上是char[]是由4位16进制 组成的。 2位的16进制的数就可以完全对应8位的二进制的数。所以";
//        System.out.println("加密结果2>>>" +SHA256Util.getSHA256StrJava(as));
//
//
//    }
}
