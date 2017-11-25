package com.app;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

import java.io.IOException;
import java.io.InputStream;

public class VoiceRecognitor {

    public VoiceRecognitor(){

    }

    public String getWordFromVoice()  {

        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream stream = classLoader.getResourceAsStream("rawData/Voice/Amos.wav");
        Configuration configuration = new Configuration();

        //configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath(String.valueOf(classLoader.getResource("Voice/cmudict-en-us.dict")));
        configuration.setLanguageModelPath(String.valueOf(classLoader.getResource("Voice/enus.lm.bin")));

        StreamSpeechRecognizer recognizer = null;
        try {
            recognizer = new StreamSpeechRecognizer(configuration);
        } catch (IOException e) {
           return (e.getMessage());
        }

        recognizer.startRecognition(stream);
        SpeechResult result  = recognizer.getResult();

        recognizer.stopRecognition();

        return result.getHypothesis();
    }
}
