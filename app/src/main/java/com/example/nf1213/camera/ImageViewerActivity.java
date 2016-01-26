package com.example.nf1213.camera;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class ImageViewerActivity extends Activity {

    public static final String IMAGE_PATH = "imagePath";

    String imagePath;
    ImageView imageView;
    FloatingActionButton deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imagePath = getIntent().getStringExtra(IMAGE_PATH);

        imageView = (ImageView) findViewById(R.id.image);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath, options));

        deleteButton = (FloatingActionButton) findViewById(R.id.delete);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(imagePath);
                if (file.exists()) {
                    file.delete();
                    setResult(GalleryActivity.IMAGE_DELETED);
                    finish();
                }
            }
        });
    }
}
