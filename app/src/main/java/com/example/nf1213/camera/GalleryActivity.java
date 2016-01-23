package com.example.nf1213.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends Activity {

    List<String> imagePaths;
    GridView imageGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        imagePaths = getImages();
    }

    @Override
    protected void onResume() {
        super.onResume();

        imageGrid = (GridView) findViewById(R.id.gridview);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        int windowWidth = Math.round(displayMetrics.widthPixels);
        int columnWidth = ( windowWidth / 3 );
        imageGrid.setColumnWidth(columnWidth);

        imageGrid.setAdapter(new ImageAdapter(imagePaths, this, columnWidth));
    }

    public List<String> getImages() {
        List<String> images = new ArrayList<>();

        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "NicoleCameraApp");

        if(dir.exists() && dir.isDirectory()) {
            for (File f: dir.listFiles()) {
                images.add(f.getAbsolutePath());
            }
        }

        return images;
    }

    public class ImageAdapter extends BaseAdapter {

        List<String> data;
        Context context;
        int width;

        public ImageAdapter(List<String> data, Context context, int width) {
            this.data = data;
            this.context = context;
            this.width = width;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(width - 4, (int) Math.round(width * (.75)) - 4));
                imageView.setPadding(2,2,2,2);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageBitmap(BitmapFactory.decodeFile(data.get(position)));
            return imageView;
        }
    }
}
