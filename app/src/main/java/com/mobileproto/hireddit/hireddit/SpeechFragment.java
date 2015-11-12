package com.mobileproto.hireddit.hireddit;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Speech Fragment: Holds functionality for requesting and recieving voice input
 */
public class SpeechFragment extends Fragment
{
    String DEBUG_TAG = "SpeechFragment Debug";
    private View view;
    private SpeechRecognizer sr;
    private SpeechListener listener;
    private Intent recognizerIntent;
    private ArrayList voiceInput;
    private boolean isListening = false;
    @Bind(R.id.speech)
    Button speechButton;
    @Bind(R.id.text)
    TextView textView;

    public SpeechFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.fragment_speech, container, false);
        ButterKnife.bind(this, view);

        listener = new SpeechListener(new SpeechCallback()
        {
            @Override
            public void callback(ArrayList voiceResult)
            {
                voiceInput = voiceResult;
                textView.setText(voiceInput.get(0).toString());
                isListening = false;
            }

            @Override
            public void errorCallback(int errorCode)
            {
                if (errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
                    //TODO: change this to saying out loud, "please try again"
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Error: Speech was not recognized.", Toast.LENGTH_SHORT).show();
                }
                if (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Error: Please say something.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Error occurred! Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void partialCallback(ArrayList partialResult)
            {
                textView.setText(partialResult.get(0).toString());
            }
        });

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                1000); // TODO: TEST THIS
        sr = SpeechRecognizer.createSpeechRecognizer(getActivity().getApplicationContext());
        sr.setRecognitionListener(listener);

        speechButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!isListening) {
                    doListen();
                } else {
                    //what you want to happen if you press the button an you're already listening for voice
                    dontListen(); //note that you don't need to press the button again to stop listening - it'll automatically stop
                }
            }
        });
        return view;
    }

    public void doListen()
    {
        Log.d(DEBUG_TAG, "Start listening.");
        isListening = true;
        sr.startListening(recognizerIntent);
    }

    public void dontListen()
    {
        Log.d(DEBUG_TAG, "Stop listening.");
        sr.stopListening();
        isListening = false;
    }
}