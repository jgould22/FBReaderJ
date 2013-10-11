package org.geometerplus.android.fbreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MathMLWebViewActivity extends Activity {

    
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       final WebView webview = new WebView(this);
       setContentView(webview);

       WebSettings webSettings = webview.getSettings();
       webSettings.setJavaScriptEnabled(true);
       
       Intent intent = getIntent();
        
       
       //Load MathJax to render MathML, this is done separately due to bug in android 3.0/4.0 involving url parsing with "?" 
       webview.setWebViewClient(new WebViewClient() {  
           @Override  
           public void onPageFinished(WebView view, String url)  
           {  
               webview.loadUrl("javascript:<script type=\"text//javascript\" src=\"https:////c328740.ssl.cf1.rackcdn.com/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML<//script>");  
           }  
       });  
       
      webview.loadUrl("file://" + intent.getExtras().getString("fileLocation"));

    }
 
    
}
