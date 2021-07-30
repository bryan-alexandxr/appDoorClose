package com.grupo8.iot;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.os.StrictMode;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.vishnusivadas.advanced_httpurlconnection.PutData;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivityRegistro extends AppCompatActivity {

    EditText etnombres, etapellidos, etcontrasena, etEmail;
    String correo;
    String contrasena;
    String mensaje;
    Session session;
    ProgressBar p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_registro);
        etnombres = (EditText) findViewById(R.id.nombreReg);
        etapellidos = (EditText) findViewById(R.id.apellidoReg);
        etcontrasena = (EditText) findViewById(R.id.contrasenaReg);
        etEmail = (EditText) findViewById(R.id.emailReg);

        mensaje = " ";
        correo = "doorcloseIoT@gmail.com"; //Correo creado para la APP
        contrasena = "doorclose_IOT";
        p = findViewById(R.id.progressBar);
    }

    public void regresar(View v) {
        finish();
    }

    public void enviarCorreoDeRegistro(View v) {
        mensaje = "<font color='#05C6DF'>Successfully registered account!</font>" + "<br>" +
                "Your information registered is:" + "<br>" +
                "Name: " + etnombres.getText().toString() + "<br>" +
                "Last Name: " + etapellidos.getText().toString() + "<br>" +
                "Password: " + etcontrasena.getText().toString() + "<br>" +
                "Email: " + etEmail.getText().toString() + "<br>";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.googlemail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");

        try {
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correo, contrasena);
                }
            });

            if (session != null) {
                javax.mail.Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(correo));
                message.setSubject("Account Registration");
                message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(etEmail.getText().toString()));
                message.setContent(mensaje, "text/html; charset=utf-8");
                Transport.send(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        etnombres.setText("");
        etapellidos.setText("");
        etcontrasena.setText("");
        etEmail.setText("");
    }

    public void intentoDeRegistro(View v) {
        if ((esSoloLetras(etnombres.getText().toString()) && esSoloLetras(etapellidos.getText().toString())
                && esEmail(etEmail.getText().toString()) && !etcontrasena.getText().toString().equals(""))) {
            enviarAXAMPP(v);
        } else {
            Context context = getApplicationContext();
            CharSequence text = Html.fromHtml("Your account could not be registered," +
                    " check the data entered and try again.");

            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void enviarAXAMPP(View v){
        p.setVisibility(View.VISIBLE);
        String name, lastname, password, email;
        name = etnombres.getText().toString();
        lastname = etapellidos.getText().toString();
        password = etcontrasena.getText().toString();
        email = etEmail.getText().toString();
        Handler handler = new Handler();
        handler.post(() -> {
            String[] field = new String[4];
            String[] data = new String[4];
            field[0] = "name";
            field[1] = "lastname";
            field[2] = "password";
            field[3] = "email";
            data[0] = name;
            data[1] = lastname;
            data[2] = password;
            data[3] = email;

            PutData putData = new PutData("http://192.168.100.170/loginregister/signup.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    p.setVisibility(View.GONE);
                    String result = putData.getResult();
                    if(result.equals("Successfully registered account!")){
                        enviarCorreoDeRegistro(v);
                    }
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static boolean esSoloLetras(String cadena) {
        if (cadena.isEmpty()) {
            return false;
        } else {
            //Para verificar, se lo pasa a mayuscula y se consulta su numero ASCII.
            //Si está fuera del rango 65 - 90, es que NO son letras. El valor 165 equivalente a la Ñ
            for (int i = 0; i < cadena.length(); i++) {
                char caracter = cadena.toUpperCase().charAt(i);
                if ((int) caracter != 165 && ((int) caracter < 65 || (int) caracter > 90))
                    return false; //Se ha encontrado un caracter que no es letra
            }
            return true; //toda la cadena tiene letras
        }
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