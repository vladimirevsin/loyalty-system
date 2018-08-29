package fitu.npi.clientapp;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;

public class TSCActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            //_regClient(new Abonent("Иванов ИИ", "+79884578895 ", "123"));
            //_regWallet("+79884578895", 1);
            //_getBalance("+79884578895", 1)

            //_addToken(new TokenChangeModel(, 3.0, 1));
            //_destToken(new TokenChangeModel("+79884578895", 3.0, 1));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void signIn(View view) {
        TextView textViewLogin = findViewById(R.id.editTextLogin);
        TextView textViewPassword = findViewById(R.id.editTextPassword);


        try {
            if (textViewLogin.getText().toString().equals("TSC") && textViewPassword.getText().toString().equals("123")) {
                setContentView(R.layout.tsc_main);
            } else {
                boolean result = _searchClient(textViewLogin.getText().toString(), textViewPassword.getText().toString());
                if (result) {

                } else {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                    dlgAlert.setMessage("Вы не идентифицированы");
                    dlgAlert.setTitle("");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                }
            }
        } catch (Exception ex) {
            Log.e("Error:", ex.getMessage());
        }
    }

    public void register(View view) {
    }


    private void addBalls(View view) throws JsonProcessingException, JSONException {
        TextView editTextPhone = (TextView) findViewById(R.id.editTextPhone);
        TextView editTextSumPay = (TextView) findViewById(R.id.editTextSumPay);

        _addToken(new TokenChangeModel(editTextPhone.getText().toString(), Double.parseDouble(editTextSumPay.getText().toString()), 1));
    }

    //region Клиенты
    private void _regClient(Abonent abonent) throws JsonProcessingException, JSONException {
        MessageResult messageResult;

        final ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(abonent);
        JSONObject jsonObj = new JSONObject(jsonInString);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String addString = "/reg_client";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Settings.URL.concat(addString),
                jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MessageResult message = mapper.readValue(response.toString(), MessageResult.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest responce", error.toString());
                    }
                }
        );

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 150000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 150000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);
    }

    private void _regWallet(String phoneNumber, int marker) throws JsonProcessingException, JSONException {
        MessageResult messageResult;
        final ObjectMapper mapper = new ObjectMapper();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String addString = "/reg_wallet?phoneNumber=" + phoneNumber +
                "&marker=" + marker;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Settings.URL.concat(addString),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MessageResult message = mapper.readValue(response.toString(), MessageResult.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest responce", error.toString());
                    }
                }
        );

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 150000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 150000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);
    }

    private void _getBalance(String phoneNumber, int marker) throws JsonProcessingException, JSONException {
        MessageResult messageResult;
        final ObjectMapper mapper = new ObjectMapper();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String addString = "/get_balance?phoneNumber=" + phoneNumber +
                "&marker=" + marker;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Settings.URL.concat(addString),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MessageResult message = mapper.readValue(response.toString(), MessageResult.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest responce", error.toString());
                    }
                }
        );

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 150000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 150000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);
    }

    private boolean _searchClient(String phoneNumber, String password) throws JsonProcessingException, JSONException {
        final boolean[] result = {false};
        final ObjectMapper mapper = new ObjectMapper();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String addString = "/search_client?phoneNumber=" + phoneNumber.replace(' ', '+') +
                "&password=" + password;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Settings.URL.concat(addString),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            result[0] = mapper.readValue(response.toString(), boolean.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest responce", error.toString());
                        result[0] = false;
                    }
                }
        );

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 150000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 150000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);

        return result[0];
    }

    //endregion

    //region ТСП
    private void _addToken(TokenChangeModel model) throws JsonProcessingException, JSONException {
        MessageResult messageResult;

        final ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(model);
        JSONObject jsonObj = new JSONObject(jsonInString);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String addString = "/add_token_client";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Settings.URL.concat(addString),
                jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MessageResult message = mapper.readValue(response.toString(), MessageResult.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest responce", error.toString());
                    }
                }
        );

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 150000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 150000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);
    }

    private void _destToken(TokenChangeModel model) throws JsonProcessingException, JSONException {
        MessageResult messageResult;

        final ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(model);
        JSONObject jsonObj = new JSONObject(jsonInString);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String addString = "/dest_token";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Settings.URL.concat(addString),
                jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MessageResult message = mapper.readValue(response.toString(), MessageResult.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest responce", error.toString());
                    }
                }
        );

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 150000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 150000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);
    }

    //endregion
}
