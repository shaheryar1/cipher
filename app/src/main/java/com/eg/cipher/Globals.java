package com.eg.cipher;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by EG on 9/11/2018.
 */

public class Globals {

    public static SecretKeySpec generateKey(String password) throws  Exception{
        final MessageDigest digest=MessageDigest.getInstance("SHA-256");
        byte[] bytes=password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] k = digest.digest();
        return new SecretKeySpec(k,"AESAES/ECB/PKCS5Padding");
    }

    public  static byte [] BitmapToBytes(Bitmap bitmap)
    {
        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);
        byte[] byteArray = byteBuffer.array();
        return  byteArray;
    }
    public static  Bitmap ByteArrayToBitmap(byte [] bytes,Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap.Config configBmp = Bitmap.Config.valueOf(bitmap.getConfig().name());
        Bitmap bitmap_tmp = Bitmap.createBitmap(width, height, configBmp);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
         bitmap_tmp.copyPixelsFromBuffer(buffer);
         return  bitmap_tmp;
    }

    public static void alert(String message, Context ctx)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(ctx);

        alert.setTitle("Doctor");
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
    }
}
