package com.eg.cipher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by EG on 9/15/2018.
 */

public class VideoFragment extends android.support.v4.app.Fragment {

    View view;
    Button btnEncryptVideo;
    Button btnDecryptVideo;
    byte[] videoBytes;
    EditText mediaKey;
    Spinner spinner;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video, container, false);

        spinner= view.findViewById(R.id.sp_audio_video);
       mediaKey=view.findViewById(R.id.et_media_pass);
       btnEncryptVideo = view.findViewById(R.id.btn_encrypt_video);
       btnDecryptVideo=view.findViewById(R.id.btn_decrypt_video);
       btnEncryptVideo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               pickVideo();
           }
       });

       btnDecryptVideo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                    pickEncFile();
           }
       });


       return view;
    }


    public void pickEncFile()
    {
      //  Uri selectedUri = Uri.parse(getActivity().getExternalFilesDir(null));
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath().toString());

        intent.setDataAndType(uri, "text/plain");
        startActivityForResult(Intent.createChooser(intent, "Open folder"),2);
    }
    public void pickVideo() {
        String [] type = new String[2];
        type[0]=new String("video/*");
        type[1]=new String("audio/*");
        Intent intent_upload = new Intent();
        intent_upload.setType(type[0]);
        intent_upload.setType(type[1]);
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Toast.makeText(view.getContext(), "Cant load Video", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                InputStream stream = getActivity().getContentResolver().openInputStream(
                        data.getData());
                videoBytes = readBytes(stream);

                File f = new File(getActivity().getExternalFilesDir(null),"/myCipherVideo"+".txt");
                FileOutputStream fos = new FileOutputStream(f);
                videoBytes=MediaCipher.encryptFile(mediaKey.getText().toString(),videoBytes);
                fos.write(videoBytes);
                fos.close();
                Globals.alert("Encrypted file saved in : "+f.getAbsolutePath(),view.getContext());
            } catch (Exception e) {
                Toast.makeText(view.getContext(),"Error : " +e.getMessage(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else if(requestCode == 2 && resultCode == Activity.RESULT_OK) {
            try {
                InputStream stream = getActivity().getContentResolver().openInputStream(
                        data.getData());

                byte [] bytes=readBytes(stream);
                File f = new File(getActivity().getExternalFilesDir(null),"/DecryptedVideo"+".mp4");
                FileOutputStream fos = new FileOutputStream(f);
                bytes=MediaCipher.decryptFile(mediaKey.getText().toString(),bytes);
                fos.write(bytes);
                fos.close();
                Globals.alert("Decrypted file saved in : "+f.getAbsolutePath(),view.getContext());


            } catch (Exception e) {
                Toast.makeText(view.getContext(),"Wrong key or Data",Toast.LENGTH_LONG);

            }

        }
    }

    public byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

}