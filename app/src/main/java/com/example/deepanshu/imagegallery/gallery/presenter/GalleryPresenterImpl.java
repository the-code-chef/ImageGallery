package com.example.deepanshu.imagegallery.gallery.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.util.Base64;

import com.example.deepanshu.imagegallery.gallery.adapter.ImageGalleryAdapter;
import com.example.deepanshu.imagegallery.gallery.db.GalleryDataHandler;
import com.example.deepanshu.imagegallery.gallery.view.GalleryView;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by deepanshu on 10/5/17.
 */

public class GalleryPresenterImpl implements GalleryPresenter {

    private static final int CAMERA_REQUEST = 99;

    private ImageGalleryAdapter mImageGalleryAdapter;

    private GalleryView mGalleryView;

    private GalleryPresenterImpl(GalleryView galleryView) {
        this.mGalleryView = galleryView;
    }

    public static GalleryPresenterImpl newInstance(GalleryView galleryView) {
        return new GalleryPresenterImpl(galleryView);
    }

    @Override
    public void onActivityCreated() {
        // set the adapter
        mGalleryView.setAdapter(mImageGalleryAdapter = new ImageGalleryAdapter());

        // add local stored images
        addStoredImagesInAdapter();
    }

    private void addStoredImagesInAdapter() {
        List<String> imageList = GalleryDataHandler.getInstance().getImageList();

        if (null != imageList && imageList.size() > 0) {
            for (String image : imageList) {
                addInAdapter(StringToBitMap(image));
            }
            mImageGalleryAdapter.notifyDataSetChanged();
        } else {
            mGalleryView.showEmptyMessage();
        }
    }

    @Override
    public void onClickCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        mGalleryView.startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    /**
     * Adding image in adapter after clicking an image
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            GalleryDataHandler.getInstance().saveImage(BitMapToString(photo));
            addInAdapter(photo);
        }
    }

    private void addInAdapter(Bitmap photo) {
        mImageGalleryAdapter.add(photo);
        mImageGalleryAdapter.notifyDataSetChanged();
        if (mImageGalleryAdapter.getItemCount() == 0) {
            mGalleryView.showEmptyMessage();
        } else {
            mGalleryView.showImages();
        }
    }

    /**
     * converting bitmap to string
     * @param bitmap
     * @return string(from given bitmap)
     */
    private String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /**
     * converting string to bitmap
     * @param encodedString
     * @return bitmap (from given string)
     */
    private Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
