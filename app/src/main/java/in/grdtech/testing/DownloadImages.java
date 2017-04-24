package in.grdtech.testing;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadImages extends AppCompatActivity {

    FirebaseDatabase databse;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    ImageView image;
    //this position is for the which image is selected to deleted....
    protected int pos;

    ListView imageList;
    public static ProgressDialog progressDialog;
    ArrayList<String> imagesNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_images);

        image = (ImageView) findViewById(R.id.imageView2);
        imageList = (ListView) findViewById(R.id.imageList);

        //loading dialog
        progressDialog = new ProgressDialog(this);
        imagesNames = new ArrayList<>();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading11...");
        progressDialog.show();


        // database reference
        databse = FirebaseDatabase.getInstance();
        DatabaseReference ref = databse.getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("photos")){
                    showImages();

                }else{
                    new Miscellaneous().myToat(getApplicationContext(), "No photos saved.");
                    progressDialog.cancel();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                new Miscellaneous().myToat(getApplicationContext(), "check 2");
            }
        });



    }

    protected void showImages(){
        DatabaseReference ref2 = databse.getReference("photos");//changing the target location to photos folder

        //making array list to store the names

        final FirebaseListAdapter<ImageToDownloadNames> firebaseListAdapter = new FirebaseListAdapter<ImageToDownloadNames>(
                this,
                ImageToDownloadNames.class,
                android.R.layout.simple_list_item_1,
                ref2
        ) {
            @Override
            protected void populateView(View v, ImageToDownloadNames model, int position) {
                ((TextView)v.findViewById(android.R.id.text1)).setText(model.getImageName());
                imagesNames.add(model.getImageName());
                progressDialog.cancel();
            }


        };


        imageList.setAdapter(firebaseListAdapter);
        imageList.setLongClickable(true);

        //testing long click
        imageList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                AlertDialog dialog = new AlertDialog.Builder(DownloadImages.this)
                        .setTitle("Delete")
                        .setMessage("Do you want to delete?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //new Miscellaneous().myToat(getApplicationContext(), "ok deleted");
                                new DeleteImage(getApplicationContext(), imagesNames.get(pos));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Miscellaneous().myToat(getApplicationContext(), "not deleted");
                            }
                        })
                        .create();
                dialog.show();
                return true;
            }
        });


        imageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                mFirebaseStorage = FirebaseStorage.getInstance();
                mStorageReference = mFirebaseStorage.getReference("photos/");
                StorageReference x = mStorageReference.child(imagesNames.get(position));
                Glide.with(getApplicationContext())
                        .using(new FirebaseImageLoader())
                        .load(x)
                        .listener(new RequestListener<StorageReference, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressDialog.cancel();
                                return false;
                            }
                        })
                        .into(image);
            }
        });
    }
}
