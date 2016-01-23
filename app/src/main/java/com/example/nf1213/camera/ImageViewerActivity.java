package com.example.nf1213.camera;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageViewerActivity extends Activity {

    public static final String IMAGE_PATH = "imagePath";

    String imagePath;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imagePath = getIntent().getStringExtra(IMAGE_PATH);

        imageView = (ImageView) findViewById(R.id.image);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
    }
}
