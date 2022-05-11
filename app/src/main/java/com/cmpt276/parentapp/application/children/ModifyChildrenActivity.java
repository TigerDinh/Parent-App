package com.cmpt276.parentapp.application.children;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cmpt276.parentapp.application.children.model.Child;
import com.cmpt276.parentapp.application.children.model.ChildrenManager;
import com.cmpt276.parentapp.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ModifyChildrenActivity extends AppCompatActivity {


    ImageView childImage;
    public static final int CAMERA_REQUEST = 100;
    String[] cameraPermission;
    String[] storagePermission;
    private ChildrenManager manager;
    private String name = "default";
    private Integer childId = 0;
    private boolean isEditing;
    private Integer childIndex;
    private static final String EXTRA_IS_EDIT = "isEditing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delete_edit_children);

        this.setTitle("Modify Child");

        addHomeButton();
        setupAddPhoto();
        setupChildrenManager();
        checkIfEditingChild();
    }

    private void checkIfEditingChild() {
        EditText childName = findViewById(R.id.enterName);

        Intent i = getIntent();
        childIndex = i.getIntExtra(EXTRA_IS_EDIT, -1);
        isEditing = (childIndex != -1);

        if (!isEditing) {
            name = childName.getText().toString();
            Button btn = findViewById(R.id.buttonDelete);
            btn.setOnClickListener(view -> {
                if (!isEditing) {
                    finish();
                }
            });
        }
        if (isEditing) {
            Child currentChild = manager.getChildren(childIndex);
            childId = currentChild.getChildId();
            String name = currentChild.getName();
            childName.setText(name);
            try {
                File f = new File(currentChild.getImageLocation(), childId.toString());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                childImage.setImageBitmap(b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Button btn = findViewById(R.id.buttonDelete);
            btn.setOnClickListener(view -> {
                new AlertDialog.Builder(ModifyChildrenActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Are you sure")
                        .setMessage("Do you want to delete this child")
                        .setPositiveButton("yes", (dialog, which) -> {
                            manager.deleteChild(childIndex);
                            manager.saveData(ModifyChildrenActivity.this);
                            finish();
                        })
                        .setNegativeButton("No",null)
                        .show();
            });
        }
    }

    private void setupChildrenManager() {
        manager = ChildrenManager.getInstance(this);
    }

    private void setupAddPhoto() {
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        childImage = findViewById(R.id.childImage);
        childImage.setOnClickListener(view -> {
            if (!checkCameraPermission()) {
                requestCameraPermission();
            } else {
                pickFromGallery();
            }
        });
    }

    private void pickFromGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void addHomeButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static Intent makeIntent(Context context, int checkType) {
        Intent i = new Intent(context, ModifyChildrenActivity.class);
        i.putExtra(EXTRA_IS_EDIT, checkType);
        return i;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        EditText childName = findViewById(R.id.enterName);
        name = childName.getText().toString();
        ImageView childImage = findViewById(R.id.childImage);
        if (item.getItemId() == R.id.save_game) {
            if (name.equals("")) {
                Toast.makeText(this, "PLEASE ENTER A NAME", Toast.LENGTH_SHORT).show();
            }
            else {
                childImage.buildDrawingCache();
                Bitmap bMap = childImage.getDrawingCache();
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                Child child = new Child(name, directory.getAbsolutePath(), manager.getUniqueID());
                childId = child.getChildId();
                saveToInternalStorage(bMap);

                if (isEditing) {
                    manager.replaceChild(child, childIndex);
                }
                else {
                    manager.add(child);
                }

                manager.saveData(this);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();
                Picasso.with(this).load(resultUri).into(childImage);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == (PackageManager.PERMISSION_GRANTED);
                boolean storageAccepted = grantResults[1] == (PackageManager.PERMISSION_GRANTED);
                if (cameraAccepted && storageAccepted) {
                    pickFromGallery();
                } else {
                    Toast.makeText(this, "Please enable camera and storage permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory, childId.toString());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;   //added last night
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}