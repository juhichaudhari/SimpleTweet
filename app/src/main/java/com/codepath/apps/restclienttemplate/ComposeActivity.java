package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LIMIT = 140;
    EditText etCompose;
    Button btnTweet;
    TextView etValue;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        etValue = findViewById(R.id.etValue);


        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                int chars = s.toString().length();
                String temp = String.valueOf(chars);
                // Fires right after the text has changed
                etValue.setText(temp);

                if (chars > 280)
                {
                    etValue.setTextColor(Color.RED);
                }
                else{
                    etValue.setTextColor(Color.BLACK);
                }
            }
        });

        // set click listner on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LIMIT) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();
                // make an API call to twitter to public the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.e(TAG, "onSuccess to public tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.e(TAG, "Published tweet says: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));

                            //set result code and bundle data for response
                            setResult(RESULT_OK, intent);
                            // closes the activity, pass data to present
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to public tweet", throwable);
                    }
                });



            }


        });
    }
}