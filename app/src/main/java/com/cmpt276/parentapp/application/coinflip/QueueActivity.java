package com.cmpt276.parentapp.application.coinflip;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.application.children.model.Child;
import com.cmpt276.parentapp.application.children.model.ChildrenManager;
import com.cmpt276.parentapp.application.coinflip.model.CoinFlipQueue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class QueueActivity extends AppCompatActivity {

    private ChildrenManager manager;
    private CoinFlipQueue queue;
    private ListView queueView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        this.setTitle(getString(R.string.queue_title));
        queueView = findViewById(R.id.queue);
        setup();
    }

    private void setup() {
        manager = ChildrenManager.getInstance(this);
        queue = manager.getQueue();

        ArrayAdapter<Child> adapter = new MyListAdapter();
        queueView.setAdapter(adapter);

        queueView.setOnItemClickListener((adapterView, view, position, l) -> {
            queue.setCandidate(position);
            finish();
        });
    }

    private class MyListAdapter extends ArrayAdapter<Child> {
        public MyListAdapter() {
            super(QueueActivity.this, R.layout.item_view, queue.getChildren());
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