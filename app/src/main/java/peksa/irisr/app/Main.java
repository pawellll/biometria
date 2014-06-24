package peksa.irisr.app;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;


public class Main extends ActionBarActivity {

    /*
    *
    * members
    *
    */
    private ImageView takenPictureView;
    private String mCurrentPhotoPath;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private Bitmap takenPictureBitmap;
    private Uri mImageUri;
    private static final int PROGRESS_CONSTANT = 10000;
    private ProgressBar mProgressBar;

    /**
     * ******************************************
     */

    /*
    *
    * Picture handling
    *
    */
    public void grabImage(ImageView imageView) {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            takenPictureBitmap = Util.resizeBitmap(bitmap,1024);
            bitmap.recycle(); // to save memory

            imageView.setImageBitmap(takenPictureBitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("a", "Failed to load", e);
        }
    }


    private File createImageFile(String imageFileName) throws IOException {
        File storageDir = Environment.getExternalStorageDirectory();

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    // save second copy of picture but app doesn't work without it, I have no idea why
    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            File photoFile = null;
            try {
                photoFile = createImageFile("tmp");
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.err.println(ex.getCause());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mImageUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        mImageUri);

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                // photoFile.delete();
            }
        }
    }

    /**
     * ******************************************
     */


    /*
    * Activity methods
    *
    */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            this.grabImage(takenPictureView);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takenPictureView = (ImageView) findViewById(R.id.takenPicture);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_takePicture) {
            takePicture();
            return true;
        } else if (id == R.id.action_exit) {
            System.exit(0);
        }else if(id== R.id.action_setExample){
            takenPictureBitmap=((BitmapDrawable) getResources().getDrawable(R.drawable.example)).getBitmap();
            takenPictureView.setImageBitmap(takenPictureBitmap);
        } else if (id == R.id.action_process) {

            try {
                ProcessThread thread = new ProcessThread(takenPictureBitmap,takenPictureView,mProgressBar);
                thread.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**********************************************/


}

