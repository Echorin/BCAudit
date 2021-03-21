package com.oschain.fastchaindb.common.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;

public class EncryptUtil {

    public static void main(String[] args) throws InterruptedException {

        String data = "QWWEERR";

        //创建秘钥生成器
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("DSA");

            kpg.initialize(512);
            KeyPair keypair = kpg.generateKeyPair();//生成秘钥对
            DSAPublicKey publickey = (DSAPublicKey) keypair.getPublic();
            DSAPrivateKey privatekey = (DSAPrivateKey) keypair.getPrivate();


            try {
                String ss=new String(publickey.getEncoded(),"utf-8");

                System.out.println("私钥：/n"+ss);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            System.out.println();


            //签名和验证
            //签名
            Signature sign = Signature.getInstance("SHA1withDSA");
            try {

                try {
                    sign.initSign(privatekey);//初始化私钥，签名只能是私钥
                    sign.update(data.getBytes());//更新签名数据
                    byte[] b = sign.sign();//签名，返回签名后的字节数组


                    System.out.println("公钥：/n"+Base64.encodeBase64String(data.getBytes()));
                    System.out.println("私钥：/n"+Base64.encodeBase64String(b));


                    try {
                        String aa=new String(b,"utf-8");
                        System.out.println(new String(b,"utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    //验证
                    sign.initVerify(publickey);//初始化公钥，验证只能是公钥
                    sign.update(data.getBytes());//更新验证的数据
                    boolean result = sign.verify(b);//签名和验证一致返回true  不一致返回false

                    System.out.println(result);

                } catch (SignatureException e) {
                    e.printStackTrace();
                }


            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }



}
