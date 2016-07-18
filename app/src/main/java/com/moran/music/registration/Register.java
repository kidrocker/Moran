package com.moran.music.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
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
public class Register extends Activity {
    Button submit;
    EditText name, lastName, email, phone, pass, confirm;
    TextView tvWarn, login;
     boolean confirmed = false;
     boolean okayed = false;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.ed_name);

        pass = (EditText) findViewById(R.id.ed_pass);
        confirm = (EditText) findViewById(R.id.ed_pass_confirm);
        submit = (Button) findViewById(R.id.bt_submit);
        tvWarn = (TextView) findViewById(R.id.tv_warn);
        login = (TextView) findViewById(R.id.tv_login);
        pd = new ProgressDialog(this);

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tvWarn.setText("Minimum 8 characters");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                int min = 8;

                if (start < min) {
                    okayed = false;
                    tvWarn.setText("Minimum 8 characters");
                    tvWarn.setTextColor(Register.this.getResources().getColor(R.color.buttonDecline));
                } else {
                    okayed = true;
                    tvWarn.setText("Password Okay");
                    tvWarn.setTextColor(Register.this.getResources().getColor(R.color.buttonPressed));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text1 = pass.getText().toString();
                if (text1.equals(s.toString())) {
                    confirm.setTextColor(Register.this.getResources().getColor(R.color.buttonPressed));
                    tvWarn.setTextColor(Register.this.getResources().getColor(R.color.buttonPressed));
                    tvWarn.setText("Passwords Match");
                    confirmed = true;
                } else {
                    confirm.setTextColor(Register.this.getResources().getColor(R.color.buttonDecline));
                    tvWarn.setTextColor(Register.this.getResources().getColor(R.color.buttonDecline));
                    tvWarn.setText("The passwords do not match");
                    confirmed = false;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text1 = pass.getText().toString();
                String text2 = confirm.getText().toString();

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticater();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent(Register.this, Login.class);
                startActivity(x);
            }
        });
    }
    @Override
    public void onBackPressed() {
            super.onBackPressed();
        Intent q = new Intent(Register.this,Licence.class);
        startActivity(q);
    }

    public void authenticater() {
        if (name.getText().length() != 0 && okayed && confirmed) {
            pd.setMessage("Just a moment while we set up your account");
            pd.show();
            getReg();

        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
            builder.setMessage("One or more field is not okay. Double check and try again");
            builder.setCancelable(true);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.create();
            builder.show();
        }
    }
    public void getReg(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.3.2/test/register.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        pd.cancel();
                        String rail = "SMS";
                        if (s.toString().length() != 0 && s.toString().equals("1001")){
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                            builder.setMessage("Account successfully created. An authentication code has been set by "+ rail+" enter it to continue");
                            builder.setCancelable(true);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.create();
                            builder.show();

                        }else if (s.toString().length() != 0 && s.toString().equals("1002")){
                             rail = "email";
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                            builder.setMessage("Account successfully created. An authentication code has been set by "+ rail+"  enter it to continue");
                            builder.setCancelable(true);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.create();
                            builder.show();


                        }else if (s.toString().equals("4001")){
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                            builder.setMessage("Phone Already Exists");
                            builder.setCancelable(true);
                            builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent x = new Intent(Register.this,Login.class);
                                    startActivity(x);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.create();
                            builder.show();
                        }else if (s.toString().equals("3001")) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                            builder.setMessage("Error with Registration");
                            builder.setCancelable(true);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                builder.setMessage("We could not Connect. Is you internet connection Okay?");
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
                params.put("name", name.getText().toString());
                params.put("password", pass.getText().toString());
                return params;
            }

        };
        queue.add(stringRequest);
    }
}

