package org.geometerplus.android.fbreader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import android.graphics.*;
import javax.security.auth.callback.CallbackHandler;


import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MathMLWebViewActivity extends Activity {

    private WebView webview;
    private TextToSpeechWrapper mTextToSpeech;
    private Intent intent;
    
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       webview = new WebView(this);
       
       setContentView(webview);

       WebSettings webSettings = webview.getSettings();
       webSettings.setJavaScriptEnabled(true);
       
       intent = getIntent();
       
         
       //Load MathJax to render MathML, this is done separately due to bug in android 3.0/4.0 involving url parsing with "?" 
       webview.setWebViewClient(new WebViewClient() {  
           @Override
           public void onPageStarted(WebView view, String url, Bitmap favicon) {
              
               
             /*  webview.loadUrl("javascript:<script type=\"text//javascript\" " +
                       "src=\"https:////c328740.ssl.cf1.rackcdn.com/mathjax/latest/MathJax.js?" +
                       "config=TeX-AMS-MML_HTMLorMML<//script>");  
               */   
               
               
           }
           @Override  
           public void onPageFinished(WebView view, String url)  
           {  
               
               injectTTSapis();
      
               
           }  
       });  
       
       webview.loadUrl("file://" + intent.getExtras().getString("fileLocation"));

    }
    
    
    private void injectTTSapis(){
        
        mTextToSpeech = new TextToSpeechWrapper(webview.getContext());
        webview.addJavascriptInterface(mTextToSpeech, "accessibility");
        
        //CallbackHandler mCallback = new CallbackHandler("accessibilityTraversal");
        //webview.addJavascriptInterface(mCallback, "accessibilityTraversal");
        
    }
    
    /**
     * Used to protect the TextToSpeech class, only exposing the methods we want to expose.
     */
    private static class TextToSpeechWrapper {
        private TextToSpeech mTextToSpeech;

        public TextToSpeechWrapper(Context context) {
            
            final String pkgName = context.getPackageName();
            
            try{
            Class c = Class.forName("android.speech.tts.TextToSpeech");
            Constructor constructor = c.getConstructor(new Class[]{Context.class, OnInitListener.class, String.class,String.class, boolean.class});
          
            mTextToSpeech = (TextToSpeech) constructor.newInstance(context, null, null, pkgName + ".**webview**", true);
            
            }catch(Exception e) {
                
                System.out.println("exception");
                e.printStackTrace();
                       
            }
            
        }

        @JavascriptInterface
        @SuppressWarnings("unused")
        public boolean isSpeaking() {
            return mTextToSpeech.isSpeaking();
        }

        @JavascriptInterface
        @SuppressWarnings("unused")
        public int speak(String text, int queueMode, HashMap<String, String> params) {
            return mTextToSpeech.speak(text, queueMode, params);
        }

        @JavascriptInterface
        @SuppressWarnings("unused")
        public int stop() {
            return mTextToSpeech.stop();
        }

        @SuppressWarnings("unused")
        protected void shutdown() {
            mTextToSpeech.shutdown();
        }
        
        
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mTextToSpeech.shutdown();     
        
    }

   
    
}
