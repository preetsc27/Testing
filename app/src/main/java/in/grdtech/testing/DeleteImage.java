package in.grdtech.testing;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Gammy on 22/04/17.
 */

public class DeleteImage {

    Context cc;

    DeleteImage(Context c, final String name){

        //deleting from the storage...
        this.cc = c;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference("photos/" + name);

        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                new Miscellaneous().myToat(cc, "Deleted Successfully:" + name);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new Miscellaneous().myToat(cc, "Cannot Delete:" + name);
            }
        });

        //deleting from the databse...
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        Query query = reference.child("photos").orderByChild("imageName").equalTo(name);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()){
                    x.getRef().removeValue();
                }
                new Miscellaneous().myToat(cc, "Deleted from database:"+name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                new Miscellaneous().myToat(cc, "Cannot Delete from database:"+name);
            }
        });
    }


}
