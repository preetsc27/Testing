package in.grdtech.testing;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

import java.lang.reflect.Array;

public class GoodLayout extends AppCompatActivity {

    protected SignInButton sign_in_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_layout);

        sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GoodLayout.this, HomePage.class);
                startActivity(i);
            }
        });



    }

    public void dhandhanramdasguru(){
        //you are the besk
    }
}
