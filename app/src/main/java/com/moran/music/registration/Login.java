package com.moran.music.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moran.music.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 05/04/2016.
 */
public class Login extends Activity {
    TextView register;
    EditText username, password;
    Button login;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = (TextView) findViewById(R.id.tv_reg);
        username = (EditText) findViewById(R.id.ed_username);
        password = (EditText) findViewById(R.id.ed_password);
        login = (Button) findViewById(R.id.bt_log);
        pd = new ProgressDialog(Login.this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().length() !=0 && password.getText().length() !=0) {
                   pd.setMessage("Logging you in:");
                    pd.show();
                    getAuth();
                }else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setMessage("Either username or password is empty");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.create();
                    builder.show();
                }
            }


        });
    }
    private void getAuth() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.3.2/test/login.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        pd.cancel();
                        if (s.toString().length() != 0){
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                            builder.setMessage(s.toString());
                            builder.setCancelable(true);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.create();
                            builder.show();
                        }else{
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                            builder.setMessage("The server did not return anything: Maybe try again in a moment");
                            builder.setCancelable(true);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.create();
                            builder.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.cancel();
                final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setMessage("We are experiencing errors connecting");
                builder.setCancelable(true);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create();
                builder.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());

                return params;
            }

        };
        queue.add(stringRequest);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent q = new Intent(Login.this,Licence.class);
        startActivity(q);
    }
}
