package com.mobileproto.hireddit.hireddit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import io.indico.Indico;
import io.indico.enums.TextTag;
import io.indico.results.IndicoResult;
import io.indico.api.Api;
import io.indico.network.IndicoCallback;
import io.indico.clients.TextApi;
import io.indico.utils.IndicoException;;

/**
 * Created by lwilcox on 11/5/2015.
 */
public class GetWords{
    private String importantWords;
    private Context context;
    public ArrayList<String> wordList = new ArrayList<>();
    public ArrayList<String> allComments;
    public GetWords(Context context){
        this.context = context;
    }
    String indicoApiKey = "7a8f16edc7a58c8a7773ba95c6d2241b";
    Indico indico = Indico.init(context, indicoApiKey, null);

    protected ArrayList<String> getImportantWords(String spokenString){
        try {
            indico.keywords.predict("indico is so easy to use!", new IndicoCallback<IndicoResult>() {
                public void handle(IndicoResult result) throws IndicoException {
                    Log.i("Indico Sentiment", "sentiment of: " + result.getKeywords());
                    if (result.getKeywords() != null) {
                        wordList.add(result.getKeywords().toString());
                    }
                }
            });
            return wordList;
        } catch (IOException | IndicoException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getRelatedComments(String spokenString) {
        wordList = getImportantWords(spokenString);
        importantWords = wordList.toString();
        GetComment getComment = new GetComment(context);
        getComment.commentSearch(importantWords, new CommentCallback() {
            public void callback(ArrayList<String> commentList) {
                allComments = commentList;
            }
        });
        return allComments;
    }
}
