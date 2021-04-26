package com.example.imagepick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.crazyorr.zoomcropimage.CropShape;
import com.github.crazyorr.zoomcropimage.ZoomCropImageActivity;
import com.soundcloud.android.crop.Crop;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    int IMAGE_PICK_REQUEST = 10;
    ImageView image;
  Uri mPictureUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image=findViewById(R.id.image);
    }

    public void SelectImageClick(View view) {


        Intent inten = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        inten.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(
                Intent.createChooser(inten, "Select profile picture"), IMAGE_PICK_REQUEST);



   /*     Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPictureUri = Uri.fromFile(createPictureFile("picture.png"));

        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPictureUri);

        Intent chooserIntent = Intent.createChooser(pickIntent,
                "Choose image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                new Intent[]{takePhotoIntent});

        startActivityForResult(chooserIntent, IMAGE_PICK_REQUEST);*/



    }

    public static File createPictureFile(String fileName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File picture = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + BuildConfig.APPLICATION_ID,
                fileName);

        File dirFile = picture.getParentFile();
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        return picture;
    }
    // Scale and maintain aspect ratio given a desired width
    // BitmapScaler.scaleToFitWidth(bitmap, 100);
    public static Bitmap scaleToFitWidth(Bitmap b, int width)
    {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }


    // Scale and maintain aspect ratio given a desired height
    // BitmapScaler.scaleToFitHeight(bitmap, 100);
    public static Bitmap scaleToFitHeight(Bitmap b, int height)
    {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                String destinationFileName="cropped_image.jpg";
                UCrop.Options options=new UCrop.Options();
              Uri uri=data.getData();
                UCrop.of(uri, Uri.fromFile(new File(getCacheDir(),destinationFileName)))
                        .withMaxResultSize(450, 500)
                        .withAspectRatio(450,500)

                        .start(MainActivity.this);




             /*   UCrop.of(sourceUri, destinationUri)
                        .withAspectRatio(16, 9)
                        .withMaxResultSize(450, 500)
                        .start(this);*/
/*                Uri selectedImageUri = null;
                if (data != null) {
                    selectedImageUri = data.getData();
                }
                if (selectedImageUri == null) {
                    selectedImageUri = mPictureUri;
                }

                Intent intent = new Intent(this, ZoomCropImageActivity.class);
                intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_URI, selectedImageUri);
                intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_OUTPUT_WIDTH, 450);
                intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_OUTPUT_HEIGHT, 500);
                intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_CROP_SHAPE, CropShape.SHAPE_OVAL);   //optional
                File croppedPicture = createPictureFile("cropped.png");
                if (croppedPicture != null) {
                    intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_SAVE_DIR,
                            croppedPicture.getParent());   //optional
                    intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_FILE_NAME,
                            croppedPicture.getName());   //optional
                }
                startActivityForResult(intent, 3);*/

            }
        }
        if (requestCode == 3){
            if (resultCode == ZoomCropImageActivity.CROP_SUCCEEDED && data != null) {
                Uri croppedImageUri = data
                        .getParcelableExtra(ZoomCropImageActivity.INTENT_EXTRA_URI);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                Bitmap bitmap=BitmapFactory.decodeFile(new File(croppedImageUri.getPath()).getAbsolutePath(), options);
               /* Bitmap bheight=scaleToFitWidth(bitmap,450);
                Bitmap bwidth=scaleToFitWidth(bheight,500);*/
               Bitmap bbb= getResizedBitmap(bitmap, 450, 500);
/*               Bitmap bbb= Bitmap.createScaledBitmap(bitmap, 450, 500, false);*/
                String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bbb, "Title", null);
                Uri urri=Uri.parse(path);
                //getDropboxIMGSize(urri);
                //image.setImageURI(urri);
                /*getDropboxIMGSize(croppedImageUri);
                image.setImageURI(croppedImageUri);*/
            }
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
        //    Bitmap bitmap=BitmapFactory.decodeFile(new File(resultUri.getPath()).getAbsolutePath(), options);
            Bitmap bitmapp = null;
            try {
                bitmapp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultUri);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }


            // Bitmap bitmap=BitmapFactory.decodeFile(new File(resultUri.getPath()).getAbsolutePath(), options);
            Bitmap bbb= Bitmap.createScaledBitmap(bitmapp, 450, 500, false);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bbb, "Title", null);
            Uri urri=Uri.parse(path);
            image.setImageURI(urri);
          /*  File fdelete = new File(urri.getPath());
                  getApplicationContext().deleteFile(fdelete.getName());*/
               /* if (fdelete.delete()) {
                    Toast.makeText(getApplicationContext(),"file Deleted :" + urri.getPath(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"file not Deleted :" + urri.getPath(),Toast.LENGTH_SHORT).show();
                }*/

            Toast.makeText(getApplicationContext(), "" + bbb.getHeight() + "   " + bbb.getWidth(), Toast.LENGTH_SHORT).show();

            // getDropboxIMGSize(urri);
            //Toast.makeText(getApplicationContext(), "ok" , Toast.LENGTH_SHORT).show();

        }
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
// create a matrix for the manipulation
        Matrix matrix = new Matrix();
// resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
// recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;

    }
    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }
    private void getDropboxIMGSize(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        Toast.makeText(getApplicationContext(), "" + imageHeight + "   " + imageWidth, Toast.LENGTH_SHORT).show();

    }
}