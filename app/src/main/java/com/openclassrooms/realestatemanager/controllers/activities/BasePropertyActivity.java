package com.openclassrooms.realestatemanager.controllers.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;


import com.openclassrooms.realestatemanager.controllers.fragments.AddAgentBottomSheetFragment;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.injection.ViewModelFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public abstract class BasePropertyActivity extends AppCompatActivity {

    // ----- FOR DATA -----

    BasePropertyActivityViewModel mPropertyActivityViewModel;
    private static final String READ_EXT_STORAGE_PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;

    private static final String CAMERA_PERMS = Manifest.permission.CAMERA;
    static final int RC_CHOOSE_PHOTO = 100;
    public static final int RC_TAKE_PHOTO = 200;
    private static final String TAG = "TAG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        mPropertyActivityViewModel = new ViewModelProvider(this, viewModelFactory).get(BasePropertyActivityViewModel.class);
    }

    void descriptionTitleLengthListener(EditText editText, TextView textView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int maxLength = 300;
                textView.setText("Description (" + (maxLength - editText.length()) + "/300)");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    // ------ LISTENERS ------

    void onClickChooseAgent(ImageButton imageButton) {
        imageButton.setOnClickListener(view -> {
            AddAgentBottomSheetFragment.newInstance().show(getSupportFragmentManager(),"AddAgentBottomSheetFragment");
        });
    }

    @AfterPermissionGranted(RC_CHOOSE_PHOTO)
    void onClickChoosePicture(ImageButton imageButton) {
        imageButton.setOnClickListener(view ->{
            if (!EasyPermissions.hasPermissions(this, READ_EXT_STORAGE_PERMS))
            {
                EasyPermissions.requestPermissions(this,"Real Estate Manager needs to access your photo storage",RC_CHOOSE_PHOTO, READ_EXT_STORAGE_PERMS);
                return;
            }
            // Intent for Selection Image Activity
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RC_CHOOSE_PHOTO);
        });
    }

    @AfterPermissionGranted(RC_TAKE_PHOTO)
    void onClickTakePicture(ImageButton imageButton){
        imageButton.setOnClickListener(view ->{
            if(!EasyPermissions.hasPermissions(this, CAMERA_PERMS)){
                EasyPermissions.requestPermissions(this,"Real Estate Manager needs your permission to use camera", RC_TAKE_PHOTO, CAMERA_PERMS);
                return;
            }
            takePictureIntent();
        });

    }

    void onClickDatePicker(ImageButton imageButton, BasePropertyActivityViewModel viewModel){
        imageButton.setOnClickListener(view -> {
            int currentYear, currentMonth, currentDayOfMonth ;
            Calendar calendar = Calendar.getInstance();
            currentYear = calendar.get(Calendar.YEAR);
            currentMonth = calendar.get(Calendar.MONTH);
            currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    viewModel.getDateSelected().setValue(calendar.getTime());
                }
            }, currentYear, currentMonth, currentDayOfMonth);
            datePickerDialog.show();
        });
    }

    // PHOTO METHODS

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.e(TAG, "Create File" + photoFile.toString());
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Error while creating the File", ex);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.openclassrooms.realestatemanager.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, RC_TAKE_PHOTO);
            }
        }
    }

    String takePhotoPath;
    String selectPhotoPath;

    File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        takePhotoPath = image.getAbsolutePath();
        Log.e(TAG, "Photo Path :" + takePhotoPath);

        return image;
    }


    void createFileFromBitmap(File destinationFile, Bitmap bitmap) throws IOException {
        //create a file to write bitmap data
        //destinationFile.createNewFile();

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapData = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(destinationFile);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
    }

    void setPictureFromPath(String photoPath, List<Bitmap> bitmapList, List<String> imagePathList) {
        // Get the dimensions of the View
        int targetW = 400;
        int targetH = 400;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(photoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);

        bitmapList.add(bitmap);
        mPropertyActivityViewModel.getBitmapList().setValue(bitmapList);

        Log.e(TAG, "Bitmap List after add :" + bitmapList.size() + bitmapList);


        imagePathList.add(photoPath);
        mPropertyActivityViewModel.getPathList().setValue(imagePathList);

    }

    //helper to retrieve the path of an image URI
    public String getRealPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor!=null)
        {
            cursor.moveToFirst();
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            return cursor.getString(column_index);
        }
        else{
            return uri.getPath();
        }
    }

}
