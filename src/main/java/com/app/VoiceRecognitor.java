package com.app;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

import java.io.IOException;
import java.io.InputStream;

public class VoiceRecognitor {

    public VoiceRecognitor(){

    }

    public String getWordFromVoice(String filepath)  {

         /*
        TODO: Bug
        Heroku, upon loading this application on their web services, stores the libraries in a location
        unavaible to this application explicitly.
        Yet, the sphinx voice recognition app requires the exact filepath to various file in order to work properly.

        possible fix: use classLoader.getressourceAsStream(), write the stream on a file, use that file as filepath.
        (memory intensive).
         */
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(filepath);
        Configuration configuration = new Configuration();

        configuration.setSampleRate(8000);
        configuration.setAcousticModelPath(classLoader.getResource("Voice/enus").getPath());
        configuration.setDictionaryPath(classLoader.getResource("Voice/cmudict-en-us.dict").getPath());
        configuration.setLanguageModelPath(classLoader.getResource("Voice/enus.lm.bin").getPath());

        StreamSpeechRecognizer recognizer = null;

        try {
            recognizer = new StreamSpeechRecognizer(configuration);
        } catch (IOException e) {
            return "error";
        }

        recognizer.startRecognition(stream);
        SpeechResult result  = recognizer.getResult();
        recognizer.stopRecognition();

        return result.getHypothesis();
    }
}
