package com.grupo8.iot;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import com.vishnusivadas.advanced_httpurlconnection.PutData;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText signEmail, signPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        signEmail = (EditText) findViewById(R.id.emailSignIn);
        signPassword = (EditText) findViewById(R.id.contrasenaSignIn);
    }

    public void registro(View v){
        Intent i = new Intent(MainActivity.this, MainActivityRegistro.class);
        startActivity(i);
    }

    public void intentoLogIn(View v) {
        if ((esEmail(signEmail.getText().toString()))) {
            recibirDeXAMPP(v);
        } else {
            Context context = getApplicationContext();
            CharSequence text = Html.fromHtml("Email or Password wrong.");

            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void recibirDeXAMPP(View v){
        String password, email;
        password = signPassword.getText().toString();
        email = signEmail.getText().toString();
        Handler handler = new Handler();
        handler.post(() -> {
            String[] field = new String[2];
            String[] data = new String[2];
            field[0] = "email";
            field[1] = "password";
            data[0] = email;
            data[1] = password;

            PutData putData = new PutData("http://192.168.100.170/loginregister/login.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    if(result.equals("Successfully Logged In.")){
                        signEmail.setText("");
                        signPassword.setText("");
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this, ActivityLogeado.class);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
}
     private static boolean esEmail(String email) {
        if (email.isEmpty()) {
            return false;
        } else {
            Pattern pattern = Pattern
                    .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

            Matcher mather = pattern.matcher(email);

            return mather.find(); //email es valido (true) o invalido (false)
        }
    }
}