package com.example.home.sqlitedemo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by home on 8/13/2016.
 */

public class DetailsActivity extends AppCompatActivity {


    EditText edtValue, edtMobile;
    Button btnSave;
    Toolbar toolbar;
    Database database;
    Intent in;
    String name, mobile;
    int id_data;
    byte d[];
    ImageView image;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect;
    private ImageView ivImage;
    private String userChoosenTask;
    byte camera[];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();
        in = getIntent();
        id_data = in.getIntExtra("id", 0);
        name = in.getStringExtra("name");
        mobile = in.getStringExtra("mobile");
        d = in.getByteArrayExtra("image");
        Log.d("Data", "ID :=> " + id_data + "Name :=> " + name + " Mobile :=> " + mobile);
        if (!name.isEmpty())
            edtValue.setText(name);
        if (!mobile.isEmpty())
            edtMobile.setText(mobile);

        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(d, 0, d.length);
            image.setImageBitmap(bitmap);

        } catch (Exception e) {
            Log.d("error", e.getMessage());
        }


    }

    private void init() {
        edtValue = (EditText) findViewById(R.id.edtValue);
        edtMobile = (EditText) findViewById(R.id.edtMobile);
        btnSave = (Button) findViewById(R.id.btnSave);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        image = (ImageView) findViewById(R.id.image);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database = new Database(getApplicationContext());
        btnSave.setText("Update");
    }

    public void Image(View view) {
        selectImage();
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(DetailsActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    public void Save(View view) {
        if (Validation() && ValidationMobile()) {
            //Submit
            try {
                database.updateContact(new Contact(id_data, edtValue.getText().toString().trim(), edtMobile.getText().toString().trim(), camera));
                Toast.makeText(getApplicationContext(), "Updated " + name, Toast.LENGTH_SHORT);
                this.finish();
            } catch (Exception e) {
                Log.d("error", e.getMessage());
            }
        }
    }

    public boolean Validation() {
        if (edtValue.getText().toString().trim().isEmpty()) {
            edtValue.setError("enter value");
            edtValue.setFocusable(true);
            return false;
        }
        return true;
    }

    public boolean ValidationMobile() {
        if (edtMobile.getText().toString().trim().isEmpty()) {
            edtMobile.setError("enter mobile");
            edtMobile.setFocusable(true);
            return false;
        } else if (edtMobile.getText().toString().trim().length() < 10) {
            edtMobile.setError("enter 10 digit");
            edtMobile.setFocusable(true);
            return false;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blank_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_add:
                deleteContact();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteContact() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                DetailsActivity.this);
        // set title
        alertDialogBuilder.setTitle("Are you sure want to delete ? " + id_data + " " + name);

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        try {
                            // get image from drawable
                            Bitmap image = BitmapFactory.decodeResource(getResources(),
                                    R.drawable.ic_launcher);

                            // convert bitmap to byte
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte imageInByte[] = stream.toByteArray();


                            database.deleteContact(new Contact(id_data, name, mobile, imageInByte));
                            Toast.makeText(getApplicationContext(), "Deleted" + name, Toast.LENGTH_SHORT);
                            DetailsActivity.this.finish();
                        } catch (Exception e) {
                            Log.d("deleted data", e.getMessage());
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    //


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }


    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        image.setImageBitmap(thumbnail);
        image(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        image.setImageBitmap(bm);
        image(bm);
    }

    public byte[] image(Bitmap bm) {
        // convert bitmap to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte imageInByte[] = stream.toByteArray();
        camera = imageInByte;
        return imageInByte;

    }
}
