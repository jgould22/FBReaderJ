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
          
          if(new File(externalPath.getAbsolutePath() +"/Android/data/org.geometerplus.android.fbreader/mathML/").exists()){
              //Cache maintenance check to see if the cache is greater than 5mb, if it is it clears everything except the current boo
              if(folderSize(new File(externalPath.getAbsolutePath() +"/Android/data/org.geometerplus.android.fbreader/mathML/"))>5242880){
                  
                  clearCache(new File(externalPath.getAbsolutePath() +"/Android/data/org.geometerplus.android.fbreader/mathML/" + xmlFile.getShortName()));
           
              }
          }
          
          
          try {
              
              mathMLHTML.getParentFile().mkdirs();
              mathMLHTML.createNewFile();
              FileOutputStream fOut = new FileOutputStream(mathMLHTML);
              OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
              myOutWriter.append(mathMLTemplate1 + mathML + mathMLTemplate2);
              myOutWriter.close();
              fOut.close();
              
              String link = externalPath.getAbsolutePath() +"/Android/data/org.geometerplus.android.fbreader/mathML_cache/" + xmlFile.getShortName() +"/"+ xmlattributes.getValue("id") + ".html";
              
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
               
        //Return text to REGULAR
        if (kind != FBTextKind.REGULAR) {
            reader.getModelReader().addControl(kind, false);
        }
        
    }
    
    /*
     * CheckStorageState Method
     * @return Boolean True if the storage is mounted false if not
     */
    private boolean checkStorageState(){
        
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Media can be written and read from 
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Media is read only 
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        
        return(mExternalStorageAvailable && mExternalStorageWriteable );
        
    }
    
    /*
     * SetXMLFile Method
     * @param ZLFile XML File for Daisy3 Book
     */
    public void setXMLFile(ZLFile file){
        
        this.xmlFile= file;
        
    }
    
    /*
     * FolderSize Method
     * @param File
     * @return Long The size of the file
     */
    private static long folderSize(File directory) {
        long length = 0;
        
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }
    
    /*
     * Clear Cache 
     * @param File The file that is to be excluded from the cache deletion 
     */
    private void clearCache(File excludedFile){
        
        File cacheDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/Android/data/org.geometerplus.android.fbreader/mathML/");
        
        if (cacheDir.isDirectory()) {
            String[] children = cacheDir.list();
            for (int i = 0; i < children.length; i++) {
                
                File checkForDeletion = new File(cacheDir, children[i]);
                
                if(checkForDeletion != excludedFile){
                    
                    deleteRecursive(checkForDeletion);
                    
                }
                
            }
        }
        
    }
    
   /*
    * DeleteRecursive Method
    * @param File Deletes the file and any children within the file
    */
    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
    

}
