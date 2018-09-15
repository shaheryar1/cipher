package com.eg.cipher;

import android.util.Base64;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by EG on 9/11/2018.
 */

public class MediaCipher {


    public  static Key f() throws NoSuchAlgorithmException, InstantiationException, IllegalAccessException, IOException
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        Key key = keyGenerator.generateKey();
        return key;
    }

    public static byte[] encryptFile(String key, byte[] content) {
        Cipher cipher;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, Globals.generateKey(key));
            encrypted = cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;

    }

    public static byte[] decryptFile(String key, byte[] textCryp)throws Exception {
        Cipher cipher;
        byte[] decrypted = null;

            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, Globals.generateKey(key));
            decrypted = cipher.doFinal(textCryp);

        return decrypted;
    }

    public static  String encrypt(String Data ,String password) throws  Exception
    {
        SecretKeySpec keySpec= Globals.generateKey(password);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE,keySpec);
        byte[] encVal=c.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        return encryptedValue;
    }

    //Decryption Function AES
    public static String decrypt(String Data,String password) throws  Exception
    {
        SecretKeySpec keySpec= Globals.generateKey(password);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE,keySpec);
        byte[] decryptedValue = Base64.decode(Data,Base64.DEFAULT);
        byte[] decVal=c.doFinal(decryptedValue);
        return new String(decVal);
    }




}
