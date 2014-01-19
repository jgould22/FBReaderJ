/**
 * Daisy3XMLTagMathMLAction
 * Generates html file with MathML in it and links to the local file 
 * @author Jordan Gould jordangould@gmail.com 
 * 
 */

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
    
    final static String mathMLTemplate1 = "<!DOCTYPE html> <html> <head> <title> Math Render</title>" +
    		"<script type=\"text/javascript\" src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\"></script>" +
    		"<script type=\"text/javascript\" src=\"https://ssl.gstatic.com/accessibility/javascript/android/chromeandroidvox.js\"></script>" +
    		"<script type=\"text/javascript\">accessibility=function(){return 1;} accessibility.speak=function(a){alert(a);}" +
    		    "accessibility.isSpeaking=function(){return 0;}accessibility.stop=function(){return 1;} " +
    			"function testS() {cvox.ChromeVox.syncToNode(document.getElementById('mathform'), true);}" +
    		"</script>" +
    		"</head><body>";
    
    final static String mathMLTemplate2 = "</body></html>";
            
    private static Daisy3XMLTagMathMLAction instance = null;
     
    private static byte kind; 
    
    private ZLFile xmlFile;
    
    
    /**
     * Default constructor.
     * @constructor
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

              //Cache maintenance check to see if the cache is greater than 5mb, if it is it clears everything except the current book Folder
              if(folderSize(new File(externalPath.getAbsolutePath() +"/Android/data/org.geometerplus.android.fbreader/mathML/"))>5242880){
                  
                  clearCache(new File(externalPath.getAbsolutePath() +"/Android/data/org.geometerplus.android.fbreader/mathML/" + xmlFile.getShortName()));
           
              }
          }
          
          
          try {
              
              //Create HTML file with mathML and MathJax
              mathMLHTML.getParentFile().mkdirs();
              mathMLHTML.createNewFile();
              FileOutputStream fOut = new FileOutputStream(mathMLHTML);
              OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
              myOutWriter.append(mathMLTemplate1 + mathML + mathMLTemplate2);
              myOutWriter.close();
              fOut.close();
              
              String link = externalPath.getAbsolutePath() +"/Android/data/org.geometerplus.android.fbreader/mathML/" + xmlFile.getShortName() +"/"+ xmlattributes.getValue("id") + ".html";
              
              final byte hyperlinkType;
             
              //Insert link to mathML .html FIle
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
    
    /**
     * StorageState Check
     * Checks the status of the external storage 
     * @return boolean  true if the external storage is available and writable, false otherwise
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
    
    /**
     * SetXMLFile Method
     * sets the xml file that is to be parsed for mathML
     * @param ZLFile XML File for Daisy3 Book
     */
    public void setXMLFile(ZLFile file){
        
        this.xmlFile= file;
        
    }
    
    /**
     * FolderSize Method
     * @param File or Directory
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
    
    /**
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
    
   /**
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
