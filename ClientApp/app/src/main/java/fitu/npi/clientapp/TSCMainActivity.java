package fitu.npi.clientapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

import java.io.IOException;

public class TSCMainActivity extends AppCompatActivity {
    private String URL = "http://192.168.94.2:8080";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
                URL.concat(addString),
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
                URL.concat(addString),
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
                URL.concat(addString),
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
                URL.concat(addString),
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
                URL.concat(addString),
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
