package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.language.v1.CloudNaturalLanguage;
import com.google.api.services.language.v1.CloudNaturalLanguageRequestInitializer;
import com.google.api.services.language.v1.model.AnnotateTextRequest;
import com.google.api.services.language.v1.model.AnnotateTextResponse;
import com.google.api.services.language.v1.model.Document;
import com.google.api.services.language.v1.model.Features;
import com.google.api.services.language.v1.model.Sentiment;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private String API_KEY = "XXXXXXX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CloudNaturalLanguage naturalLanguageService = new CloudNaturalLanguage.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                null
        ).setCloudNaturalLanguageRequestInitializer(
                new CloudNaturalLanguageRequestInitializer(API_KEY)
        ).build();

        String transcript = "Hello, World!";
        Document document = new Document();
        document.setType("PLAIN_TEXT");
        document.setLanguage("en-US");
        document.setContent(transcript);

        Features features = new Features();
        features.setExtractDocumentSentiment(true);

        final AnnotateTextRequest request = new AnnotateTextRequest();
        request.setDocument(document);
        request.setFeatures(features);

        Callable<AnnotateTextResponse> callLanguageApi = new Callable<AnnotateTextResponse>() {
            @Override
            public AnnotateTextResponse call() throws Exception {
                AnnotateTextResponse response = naturalLanguageService.documents().annotateText(request).execute();
                return response;
            }
        };
        
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        AnnotateTextResponse response = null;
        try {
            response = executorService.submit(callLanguageApi).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Sentiment sent = response.getDocumentSentiment();
        System.out.println("Score : " + sent.getScore() + " Magnitude : " + sent.getMagnitude());
    }
}