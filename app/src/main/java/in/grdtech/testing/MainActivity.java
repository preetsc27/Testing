package in.grdtech.testing;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button signup;
    private Button login;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //image uploading
    private Button fireBaseUploadImage;
    final int RQS_IMAGE1 = 1;
    Uri source1;
    TextView uploadStat;
    final int MY_PERMISSIONS = 11;
    //ends

    //testing starts
    Button imageProcessing;
    //tesnting ends

    //database
    FirebaseDatabase mDatabase;
    DatabaseReference mDatabaseReference;
    //end
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //firebase database
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        //end

        //testing starts

        imageProcessing = (Button) findViewById(R.id.imageProcessing);
        imageProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ImageEditing.class);
                startActivity(i);
            }
        });
        //tesnting ends

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                            signUp();
                                      }
                                  });
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "c", Toast.LENGTH_LONG).show();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    // user is logged in
                    Log.d("ok", "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else{
                    Log.d("ok", "signed out");
                }
            }
        };

        //image upload using firebase
        fireBaseUploadImage = (Button) findViewById(R.id.fireBaseImageUpload);
        fireBaseUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //download();
                //return;
                //checkForPermission();
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("file/*");
                startActivityForResult(i, RQS_IMAGE1);
            }
        });

        //download images
        Button downloadImages = (Button) findViewById(R.id.downloadedImages);
        downloadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DownloadImages.class);
                startActivity(i);
            }
        });

        // good layout content
        Button goodLayout = (Button) findViewById(R.id.goodLayout);
        goodLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GoodLayout.class);
                startActivity(i);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK ){
            if(requestCode == RQS_IMAGE1) {
                source1 = data.getData();
                upload(source1);
            }
        }
        else{
            Toast.makeText(getApplicationContext(), requestCode + "", Toast.LENGTH_LONG).show();
        }
    }


    protected void upload(final Uri uri){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference photosReference = storage.getReference("photos/" + uri.getLastPathSegment());

        UploadTask uploadTask = photosReference.putFile(uri);

        uploadStat = (TextView) findViewById(R.id.uploadinStat);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadStat.setText("Done."+taskSnapshot.getDownloadUrl().getLastPathSegment());
                mDatabaseReference.child("photos").push().child("imageName").setValue(uri.getLastPathSegment());

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                uploadStat.setText(String.format("%.2f", progress)+"%");
            }
        });


        new Miscellaneous().myToat(getApplicationContext(), photosReference+"");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void checkForPermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS); ;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS){

            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                new Miscellaneous().myToat(getApplicationContext(), "image cannot be read plzz accept the request.");
                return;
            }
        }
    }

    public void signUp(){
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getApplicationContext(), "You Have Been registered.", Toast.LENGTH_LONG).show();
                        if(!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "not logged in", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "logged in", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(MainActivity.this, HomeScreen.class);
                            MainActivity.this.startActivity(i);
                        }

                    }
                });

    }


}
