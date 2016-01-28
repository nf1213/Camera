package com.example.nf1213.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private Camera mCamera;
    private Parameters cameraParameters;
    private CameraPreview mPreview;
    private ImageView imageView;
    private GestureDetector gestureDetector;
    private int cameraId = 0;

    private String mostRecentImage;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mostRecentImage = getMostRecentImage();
        imageView = (ImageView) findViewById(R.id.image);
        if (!TextUtils.isEmpty(mostRecentImage)) {
            Glide.with(this)
                    .load(mostRecentImage)
                    .into(imageView);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GalleryActivity.class);
                startActivity(intent);
            }
        });

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                releaseCamera();

                cameraId = cameraId == 1 ? 0 : 1;

                initializeCamera();

                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
            return true;
        return super.onTouchEvent(event);
    }

    public String getMostRecentImage() {
        String imagePath = "";
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "NicoleCameraApp");

        if(dir.exists() && dir.isDirectory()) {
            if(dir.listFiles().length > 0) {
                imagePath = dir.listFiles()[0].getAbsolutePath();
            }
        }

        return imagePath;
    }

    public void initializeCamera() {
        if(mCamera == null) {
            // Create an instance of Camera
            mCamera = getCameraInstance(cameraId);

            if (mCamera == null) {
                Intent intent = new Intent(this, NoCameraActivity.class);
                startActivity(intent);
            }

            // Create our Preview view and set it as the content of our activity.
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.removeAllViews();

            if (mPreview == null) {
                mPreview = new CameraPreview(this, mCamera, cameraId);
                preview.addView(mPreview);
            } else {
                mPreview.setCamera(mCamera, cameraId);
                // this will trigger a surface changed
                preview.addView(mPreview);
            }

            // Add a listener to the Capture button
            FloatingActionButton captureButton = (FloatingActionButton) findViewById(R.id.button_capture);
            captureButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // get an image from the camera
                            mCamera.takePicture(null, null, jpegCallback);
                        }
                    }
            );

        }
    }

    public Camera getCameraInstance(int id) {
        if (mCamera != null) {
            return mCamera;
        } else {
            try {
                mCamera = Camera.open(id); // attempt to get a Camera instance
                return mCamera; // returns null if camera is unavailable
            } catch (Exception e) {
                // Camera is not available (in use or does not exist)
            }
            return null;
         }
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "NicoleCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");

        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeCamera();
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    final Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();
            new SaveTask(getApplicationContext(), imageView).execute(data);
        }
    };

    public class SaveTask extends AsyncTask<byte[], String, File> {
        Context context;
        ImageView imageView;

        public SaveTask(Context context, ImageView imageView) {
            this.context = context;
            this.imageView = imageView;
        }
        @Override
        protected File doInBackground(byte[]... data) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return null;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data[0]);
                fos.close();

                return pictureFile;

            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(File file) {
            Glide.with(context)
                    .load(file)
                    .into(imageView);

            super.onPostExecute(file);
        }
    }
}
