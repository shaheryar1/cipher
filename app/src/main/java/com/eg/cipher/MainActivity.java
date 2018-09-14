package com.eg.cipher;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.microedition.khronos.opengles.GL;

public class MainActivity extends AppCompatActivity {

    //Data Types
    public  final String TEXT="Text";
    public final  String IMAGE="Image";



    public  final int PICK_PHOTO=1;
    Bitmap bitmap;
    byte [] encryptedImageBytes;
    Bitmap outputBitmap;
    String dataType;

    byte [] encryptedTestBytes;

    Button btnLoadImage;
    ImageView imageView;
    ImageView outputImageViewer;
   EditText inputText;
   EditText key ;
  TextView outputText;
    Button btnEncrypt;
    Button btnDecrypt;
    Button btnsaveToFile;
   private  String algo="AES";





    //blowfish
    private final static String KEY = "2356a3a42ba5781f80a72dad3f90aeee8ba93c7637aaf218a8b8c18c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getting references
        setContentView(R.layout.activity_main);
        inputText= findViewById(R.id.et_text);
        key=findViewById(R.id.et_key);
        outputText=findViewById(R.id.tv_output);
        btnEncrypt= findViewById(R.id.btn_encrypt);
        btnDecrypt=findViewById(R.id.btn_decrypt);
        btnsaveToFile=findViewById(R.id.btn_save);
        imageView=findViewById(R.id.iv_image);
        outputImageViewer=findViewById(R.id.iv_output_image);
        btnLoadImage=findViewById(R.id.btn_load_image);
        final Spinner algoSpinner= findViewById(R.id.sp_algo_types);

        isStoragePermissionGranted();

        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        btnsaveToFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        Spinner spinner= findViewById(R.id.sp_options);

        spinner.setPrompt("Data Type");
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.data_types,
                android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1)
                {
                    dataType=TEXT;
                    inputText.setVisibility(View.VISIBLE);
                    key.setVisibility(View.VISIBLE);
                    btnEncrypt.setVisibility(View.VISIBLE);
                    btnDecrypt.setVisibility(View.VISIBLE);
                    btnLoadImage.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    outputImageViewer.setVisibility(View.GONE);
                    algoSpinner.setVisibility(View.VISIBLE);



                }else if(position==2)
                {

                    dataType=IMAGE;
                    btnLoadImage.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    btnEncrypt.setVisibility(View.VISIBLE);
                    btnDecrypt.setVisibility(View.VISIBLE);
                    inputText.setVisibility(View.GONE);
                    key.setVisibility(View.VISIBLE);
                    outputImageViewer.setVisibility(View.VISIBLE);
                    algoSpinner.setVisibility(View.GONE);


                }else
                {

                    Toast.makeText(MainActivity.this,"Coming Soon !",Toast.LENGTH_LONG).show();
                    inputText.setVisibility(View.GONE);
                    btnEncrypt.setVisibility(View.GONE);
                    btnDecrypt.setVisibility(View.GONE);
                    key.setVisibility(View.GONE);
                    btnLoadImage.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    outputImageViewer.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

              //  writeToFile(outputText.getText().toString(),MainActivity.this);
            }
        });


        //Algo type spinner
        algoSpinner.setPrompt("Algo Type");

        ArrayAdapter<CharSequence> algoSpinnerAdapter=ArrayAdapter.createFromResource(this,R.array.algo_types,
                android.R.layout.simple_spinner_item);


        algoSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        algoSpinner.setAdapter(algoSpinnerAdapter);

        algoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                    algo="AES";
                else {
                    algo = "Blowfish";
                    Toast.makeText(MainActivity.this,"BlowFish Dont need any key",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                try {
                    String output;

                    if(dataType==TEXT) {
                        String s = inputText.getText().toString();
                        if(!isValidEditText(s))
                        {
                            inputText.setError("Empty Field");
                            return;
                        }
                        if (algo == "AES")
                            output = MediaCipher.decrypt(outputText.getText().toString(), key.getText().toString());
                        else
                            output = BlowFishdecrypt(KEY, encryptedTestBytes);
                        outputText.setText(output);
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    else if(dataType==IMAGE)
                    {


                           // byte[] byteArray = Globals.BitmapToBytes(outputBitmap);
                            byte[] encimage = MediaCipher.decryptFile(key.getText().toString(), encryptedImageBytes);
                            Bitmap bitmap_tmp=Globals.ByteArrayToBitmap(encimage,outputBitmap);
                            outputImageViewer.setImageBitmap(bitmap_tmp);


                    }
                }catch (Exception e)
                {
                    Toast.makeText(MainActivity.this,"Wrong key or Data",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }



            }
        });


        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                String s = inputText.getText().toString();
                if(!isValidEditText(s) )
                {
                    inputText.setError("Empty Field");
                   // return;
                }


                try {
                    String output;

                    if(dataType==TEXT) {
                        if (algo == "AES")
                            output = MediaCipher.encrypt(inputText.getText().toString(), key.getText().toString());
                        else
                            output = BlowFishencrypt(KEY, inputText.getText().toString());


                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }else if(dataType==IMAGE)
                    {
                        if(bitmap!=null) {
                            byte[] byteArray = Globals.BitmapToBytes(bitmap);
                            byte[] encimage =encryptedImageBytes= MediaCipher.encryptFile(key.getText().toString(), byteArray);
                            Bitmap bitmap_tmp=Globals.ByteArrayToBitmap(encimage,bitmap);
                            outputImageViewer.setImageBitmap(bitmap_tmp);
                            outputBitmap=bitmap_tmp;
                        }
                        output="";
                    }else
                    {
                        output="";
                    }
                    outputText.setText(output);
                    inputText.clearFocus();
                    key.clearFocus();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    //Encryption Function AES



//validation
    private boolean isValidEditText(String pass) {
        if (pass == null || pass.isEmpty()) {
            return false;

        }
        return true;
    }


//BLOWFISH
    private String BlowFishdecrypt(String key, byte[] encryptedText) throws GeneralSecurityException {

        SecretKey secret_key = new SecretKeySpec(key.getBytes(), algo);

        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.DECRYPT_MODE, secret_key);

        byte[] decrypted = cipher.doFinal(encryptedText);

        return new String(decrypted);
    }

    private String BlowFishencrypt(String key, String plainText) throws GeneralSecurityException {

        SecretKey secret_key = new SecretKeySpec(key.getBytes(), algo);

        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.ENCRYPT_MODE, secret_key);
        // cipher.doFinal(plainText.getBytes());
        encryptedTestBytes=cipher.doFinal(plainText.getBytes());
        String encryptedValue = Base64.encodeToString(encryptedTestBytes,Base64.DEFAULT);
        return encryptedValue;
    }



    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
               // Log.v(TAG,"Permission is granted");
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Toast.makeText(MainActivity.this,"Cant load image",Toast.LENGTH_LONG).show();
                return;
            }
            //InputStream inputStream = MainActivity.this.getContentResolver().openInputStream(data.getData());
            // Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...

        try {
                InputStream stream = getContentResolver().openInputStream(
                 data.getData());
                 bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                 imageView.setImageBitmap(bitmap);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
           // imageView.setImageBitmap(bitmap);


        }
    }

}

