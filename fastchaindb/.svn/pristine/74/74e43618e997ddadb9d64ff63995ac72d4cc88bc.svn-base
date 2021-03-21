package com.oschain.fastchaindb.common.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class EncryptUtil2 {

    /*
     * 生成密钥
     */
    public static byte[] initKey() throws Exception{
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56);
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }


    /*
     * DES 加密
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception{
        SecretKey secretKey = new SecretKeySpec(key, "DES");

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherBytes = cipher.doFinal(data);
        return cipherBytes;
    }


    /*
     * DES 解密
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception{
        SecretKey secretKey = new SecretKeySpec(key, "DES");

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] plainBytes = cipher.doFinal(data);
        return plainBytes;
    }

    //Test
    public static void main(String[] args) throws Exception {
        String DATA=UUIDUtil.randomUUID32();

        System.out.println(DATA);

        byte[] desKey = EncryptUtil2.initKey();

        byte[] desResult = EncryptUtil2.encrypt(DATA.getBytes(), desKey);


        String str = Base58.encode(desResult);

        System.out.println(">>>DES 加密结果>>>" + str);


        byte[] desPlain = EncryptUtil2.decrypt(Base58.decode(str), desKey);
        System.out.println(">>>DES 解密结果>>>" + new String(desPlain));



    }
}