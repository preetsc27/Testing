package in.grdtech.testing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * Created by Gammy on 16/03/17.
 */

public class Miscellaneous {

    protected Context con;

    public static void myToat(Context cd , String str){
        Toast.makeText(cd, str, Toast.LENGTH_LONG).show();
    }

    //to delete a picture
    public AlertDialog getDialog(Context c, String imageName){
        this.con = c;
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Delete")
                .setMessage("Do you want to delete?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myToat(con, "ok deleted");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myToat(con, "not deleted");
                    }
                })
                .create();
        return dialog;

    }
}
