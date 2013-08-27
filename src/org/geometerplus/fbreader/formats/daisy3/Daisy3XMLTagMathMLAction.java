package org.geometerplus.fbreader.formats.daisy3;

import java.io.File;
import java.io.FileOutputStream;

import java.io.OutputStreamWriter;
import android.os.Environment;

import org.geometerplus.fbreader.bookmodel.BookReader;
import org.geometerplus.fbreader.bookmodel.FBTextKind;

import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.xml.ZLStringMap;

public class Daisy3XMLTagMathMLAction extends Daisy3XMLTagAction {
    
    final static String mathMLTemplate1 = "<!DOCTYPE html> <html> <head> <title> Math Eqn</title> <script type=\"text/javascript\" src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\"></script></head><body>";
            
    final static String mathMLTemplate2 = "</body></html>";
            
    private static Daisy3XMLTagMathMLAction instance = null;
     
    private static byte kind; 
    
    private ZLFile xmlFile;
    
    
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
        
        Daisy3InnerXMLParser innerXMLParser = new Daisy3InnerXMLParser(xmlFile,xmlattributes);
        
        String mathML = innerXMLParser.getXMLString();
        
      if(checkStorageState()){
          
          
          File externalPath = Environment.getExternalStorageDirectory();
          File mathMLHTML = new File(externalPath.getAbsolutePath() +"/Android/data/org.geometerplus.android.fbreader/mathML/" + xmlFile.getShortName() +"/"+ xmlattributes.getValue("id") + ".html") ;
          
          try {
              mathMLHTML.getParentFile().mkdirs();
              mathMLHTML.createNewFile();
              FileOutputStream fOut = new FileOutputStream(mathMLHTML);
              OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
              myOutWriter.append(mathMLTemplate1 + mathML + mathMLTemplate2);
              myOutWriter.close();
              fOut.close();
              
              String link = mathMLHTML.getAbsolutePath();
              
              final byte hyperlinkType;
             
              hyperlinkType = FBTextKind.EXTERNAL_HYPERLINK;
              
              kind = hyperlinkType;
              
              modelReader.addHyperlinkControl(hyperlinkType, link);
              
              modelReader.addHyperlinkLabel(reader.myReferencePrefix + "MathML Link");
          
          } catch (Exception e) {
            e.printStackTrace();
          }
          
      }
      
    }

    @Override
    protected void doAtEnd(Daisy3XMLReader reader) {
        // TODO Auto-generated method stub
       
        if (kind != FBTextKind.REGULAR) {
            reader.getModelReader().addControl(kind, false);
        }
        
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
    
    
    public void setXMLFile(ZLFile file){
        
        this.xmlFile= file;
        
    }
    
    
    

   

    

}
