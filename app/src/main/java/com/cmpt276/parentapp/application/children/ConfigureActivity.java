package com.cmpt276.parentapp.application.children;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmpt276.parentapp.application.children.model.Child;
import com.cmpt276.parentapp.application.children.model.ChildrenManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;



public class ConfigureActivity extends AppCompatActivity {

    private ChildrenManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        this.setTitle("Children");

        manager = ChildrenManager.getInstance(this);
        updateUI();
        setAddChildrenBtn();
        addHomeButton();
    }

    private void addHomeButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setAddChildrenBtn() {
        Button btnCoinFlip = findViewById(R.id.addChildrenButton);
        btnCoinFlip.setOnClickListener((v)-> {
            startActivity(ModifyChildrenActivity.makeIntent(this,-1));
        });
    }

    protected void onResume(){
        super.onResume();
        updateUI();
    }
    private void updateUI() {
        manager = ChildrenManager.getInstance(this);
        ListView listView = findViewById(R.id.list);
        ArrayAdapter<Child> adapter = new MyListAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = ModifyChildrenActivity.makeIntent(ConfigureActivity.this,position);
            startActivity(intent);
        });
    }
    /**
     * When the home button is clicked treat it as a back press
     * @param item Auto-called object selected
     * @return If an item was selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        // If the arrow (top left) is pressed go back a screen
        if (itemID == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, ConfigureActivity.class);
    }

    private class MyListAdapter extends ArrayAdapter<Child> {
        public MyListAdapter() {
            super(ConfigureActivity.this,R.layout.item_view,manager.getList());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null)
            {
                itemView = getLayoutInflater().inflate(R.layout.item_view,parent,false);
            }
            Child child = manager.getList().get(position);

            //Loading up the image of the child
            try {
                File f = new File(child.getImageLocation(), child.getChildId().toString());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                ImageView imageView = itemView.findViewById(R.id.childPhoto);
                imageView.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            TextView makeText = itemView.findViewById(R.id.childName);
            makeText.setText(child.getName());

            return itemView;
        }
    }

}


