package com.santiago.talkinghand.providers;

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santiago.talkinghand.utils.CompressorBitmapImage;

import java.io.File;
import java.util.Date;

public class ImagenProvider {
    StorageReference mStorage;
    public ImagenProvider(){
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public UploadTask guardar(Context context, File file){
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);
        StorageReference storage = mStorage.child(new Date() + ".jpg");
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);

        return task;
    }

    public StorageReference getStorage(){
        return mStorage;
    }

}
