package org.geometerplus.fbreader.formats.daisy3;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.HashMap;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;



import org.apache.http.client.utils.URIUtils;
import org.geometerplus.fbreader.bookmodel.BookReader;
import org.geometerplus.fbreader.bookmodel.FBTextKind;

import org.geometerplus.zlibrary.core.xml.ZLStringMap;

public class Daisy3XMLTagMathMLAction extends Daisy3XMLTagAction {
    
    final static String mathMLTemplate1 = "<!DOCTYPE html> <html> <head> <title> Math Eqn</title> <script type=\"text/javascript\" src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\"></script></head><body>";
            
    final static String mathMLTemplate2 = "</body></html>";
            
    private static Daisy3XMLTagMathMLAction instance = null;
    
    private  HashMap<String, String> mathMLMap = new HashMap<String,String>();
    
    private boolean hasMap = false;
    
    /**
     * Default constructor.
     */
    public static Daisy3XMLTagMathMLAction getInstance() {
        //if the instance is null, create a new one 
        if (instance == null) {
            instance = new Daisy3XMLTagMathMLAction();
        }
        
        return instance;
    }
    
    
  
    
    
    @Override
    protected void doAtStart(Daisy3XMLReader reader, ZLStringMap xmlattributes) {
        
        final BookReader modelReader = reader.getModelReader();
        
        String mathMLTagID = xmlattributes.getValue("id");
      
        Log.w("mathmloutput","I have found the Math ML " + mathMLMap.get(mathMLTagID) + " "+ mathMLMap.get(xmlattributes.getValue("id")));
        
      if(checkStorageState()){
          
          
          File externalPath = Environment.getExternalStorageDirectory();
          File mathMLHTML = new File(externalPath.getAbsolutePath() +"/Android/data/" + "org.geometerplus.android.fbreader" + "/mathML/" + xmlattributes.getValue("id") + ".html") ;
          
          try {
              mathMLHTML.getParentFile().mkdirs();
              mathMLHTML.createNewFile();
              FileOutputStream fOut = new FileOutputStream(mathMLHTML);
              OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
              myOutWriter.append(mathMLTemplate1 + mathMLMap.get(xmlattributes.getValue("id")) + mathMLTemplate2);
              myOutWriter.close();
              fOut.close();
              
              String link = mathMLHTML.getAbsolutePath();
              
              byte linkType = 37;
              
           modelReader.addHyperlinkControl(linkType, link);
           
           modelReader.addHyperlinkLabel("MathML Link");
          
          } catch (Exception e) {
            e.printStackTrace();
          }
          
      }
      
    }

    @Override
    protected void doAtEnd(Daisy3XMLReader reader) {
        // TODO Auto-generated method stub
       
        reader.getModelReader().addControl(FBTextKind.REGULAR, false);
        
    }
    
    private boolean checkStorageState(){
        
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        
        return(mExternalStorageAvailable && mExternalStorageWriteable );
        
    }
    
    private static long getDirSize(File dir) {

        long size = 0;
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            }
        }

        return size;
    }
    
    /*
     * hasMathMLHashMap Method
     * @return boolean returns false if mathMLMap is null 
     */
    public boolean hasMathMLHashMap (){
       
        return hasMap;
        
    }
    
    /*
     * Setter methods
     * @param HashMap<String, String> sets the Map containing the MathML with tag ids for keys
     */
    public void setMathMLHashMap(HashMap<String, String> map){
        
        if(!hasMap){
            mathMLMap = map;
            hasMap = true;
        }
    }
    
   

    

}
