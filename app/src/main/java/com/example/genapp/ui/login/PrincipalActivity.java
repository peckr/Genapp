package com.example.genapp.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genapp.R;
import com.example.genapp.data.Configuraciones;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.regex.Pattern;

public class PrincipalActivity extends AppCompatActivity {
    String iddispositivo;
    TextView txtResultado;
    ImageView imageView;
    Button btnprincipal;
    String resultado;
    String valor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        imageView= (ImageView) findViewById(R.id.imageView2);
        btnprincipal = (Button)findViewById(R.id.button);
        txtResultado = (TextView)findViewById(R.id.textView2);

        Bundle b = getIntent().getExtras();
        valor = "";
        if(b != null){
            valor = b.getString("nombre");
        }

        btnprincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerarTask ngt = new GenerarTask(valor);
                try {
                    ngt.execute();
                    txtResultado.setVisibility(View.VISIBLE);
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(),"Oops problemas al generar",Toast.LENGTH_LONG);
                };

            }
        });
    }

    public class GenerarTask extends AsyncTask<Void, Void, String> {


       // private final String id_huella;
        private final String cedula;


        private String dataecriptada = "";

        URL url = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        InputStreamReader streamReader = null;
        InputStream inputStream;
        //  AesCipher seguridad;
        String keypro = "0123456789abcdef";
        String iv = "abcdef9876543210";
        //  AesCipher encrypted;
        //  AesCipher decrypted ;


        GenerarTask(String _cedula) {

            cedula = _cedula;



        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service

            JSONObject parametros = new JSONObject();
            String data = cedula.trim();
            // Log.d("data",data);
          /*  try {
                encrypted=  seguridad.encrypt(keypro,data);
                dataecriptada = encrypted.getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //    Log.d("dataencriptada",dataecriptada);
            decrypted = seguridad.decrypt(keypro, encrypted.getData());*/
            // Log.d("datadesencriptada",decrypted.getData());
            //  Log.d("initvector",encrypted.getInitVector());

            try {
                url = new URL(Configuraciones.urlServer + "generarqr?params=" + data.trim());
            } catch (MalformedURLException e) {
                e.printStackTrace();

                Toast.makeText( getApplicationContext(), "Opps problemas de conexión", Toast.LENGTH_LONG);
                return "E";
            }
            Log.d("coxion)", "entra a conectar");
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Opps problemas de conexión", Toast.LENGTH_LONG);
                return "E";
            }
            try {
                urlConnection.setRequestMethod("GET");
            } catch (ProtocolException e) {

                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Opps problemas de conexión", Toast.LENGTH_LONG);
                return "E";
            }

            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //  Log.d("Connect)","ok");


            try {
                inputStream = urlConnection.getInputStream();
                Log.d("inputStream)", "ok");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("inputStream)", e.toString());
              //  Toast.makeText(getApplicationContext(), "Opps problemas de conexión", Toast.LENGTH_LONG);
                txtResultado.setText("Oops tenemos problemas para conectar intentalo más tarde" );

                return "E";
            }


            try {
                streamReader = new InputStreamReader(inputStream, "UTF-8");
                Log.d("InputStreamReader", "OK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.d("InputStreamReader)", e.toString());
            }

            bufferedReader = new BufferedReader(streamReader);

            StringBuffer buffer = new StringBuffer();
            String line = null;
            try {
                line = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (line != null) {

                buffer.append(line);
                break;
            }

            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            resultado = buffer.toString().replaceAll("\"", "");
            Log.d("perfil", resultado.toString());
            // TODO: register the new account here.
            String[] paramsx = resultado.toString().split(Pattern.quote("|"));
            if (paramsx[0].trim().toString().equals("S")) {
                String img = Configuraciones.urlServerimg+paramsx[1];
              //  GlideApp.with(getApplicationContext()).load(img).into(imageView);
               // Pica.with(getApplicationContext()).load(img).into(imageView);
                txtResultado.setText("Qr válido por 2 minutos a partir de "+paramsx[2].trim());

                return img;
            } else {
                txtResultado.setText(paramsx[1].trim());
                return "N"; }
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                Picasso.get().load(s).into(imageView);
            }catch(Exception e) {Log.d("ERPi", e.getMessage());}
            super.onPostExecute(s);

        }

        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while (itr.hasNext()) {

                String key = itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }

            Log.d("from", result.toString());
            return result.toString();
        }

        public Boolean getResultjson(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first;
            first = params.getBoolean("status");
            return first;
        }

        public String getResultjsonError(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            String first;
            first = params.getString("data");
            return first;
        }

    }
}