package org.geometerplus.fbreader.formats.daisy3;

import java.util.zip.*;

import org.geometerplus.fbreader.formats.util.MiscUtil;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import org.geometerplus.zlibrary.core.filesystem.ZLFile;

import android.util.Log;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

/*
 *  
 * Class for extraction of full MathM (as a string)L from a daisy3 XML file
 * Using XML pull parser it parses the XML and adds it to a hash map 
 * for later retrieval by the XMLTagMathMLAction
 * @author Jordan Gould jordangould@gmail.com
 * 
 */
public class Daisy3InnerXMLParser {

    private HashMap<String,String> xmlIdMap  = new HashMap<String,String>();  
    
    private String daisy3XMLFileName;
    private String tag;
  
    
    /*@constructor
     * Constructor takes ZLFile object and extracts the xml from the zip file and opens an iostream
     * @void
     * @param String xml file location 
     * @param String tag - tag to retrieve XML from 
     * 
     */
    public  Daisy3InnerXMLParser(ZLFile xmlFile, String Tag){
        
        this.tag=Tag;
        
        setXMLFilePath(xmlFile);
        
        try{
            
           parseXml(xmlFile); 
            
        }catch (IOException e) {
            
            Log.d("goread", "mathml ioexception");
            
        }catch (XmlPullParserException e) {
            
            Log.d("goread", "mathml XmlPullParserException");
            
        }
        
    }
    
    private void parseXml(ZLFile xmlFile) throws XmlPullParserException, IOException{
        
        InputStream input = getInputStreamFromZip(xmlFile);
        
        ZipFile daisy3zip = new ZipFile(daisy3XMLFileName);
     
        
            for (Enumeration<? extends ZipEntry> e = daisy3zip.entries();
                  e.hasMoreElements();) {
              ZipEntry ze = e.nextElement();
              String name = ze.getName();
              if (name.endsWith(".xml")) {
                  input = daisy3zip.getInputStream(ze);
                  break;
                
              }
            }
               
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        XmlPullParser previousTagParser = factory.newPullParser();

        parser.setInput(input,"utf-8" );
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
      
   
                if(parser.getName() == tag) {
                    
                    xmlIdMap.put(parser.getAttributeValue( null , "id"), getInnerXml(previousTagParser));
                    
                }
                
                parser = previousTagParser;
                eventType = parser.next();       
            
         }
        
        input.close();
        
        System.out.println(xmlIdMap.toString());
        
    }
    
    private String getInnerXml (XmlPullParser parser) 
            throws XmlPullParserException, IOException {
        StringBuilder sb = new StringBuilder();
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                if (depth > 0) {
                    sb.append("</" + parser.getName() + ">");
                }
                break;
            case XmlPullParser.START_TAG:
                depth++;
                StringBuilder attrs = new StringBuilder();
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    attrs.append(parser.getAttributeName(i) + "="
                            + parser.getAttributeValue(i) + "");
                }
                sb.append("<" + parser.getName() + " " + attrs.toString() + ">");
                sb.append(parser.getText());
                break;
            default:
                sb.append(parser.getText());
                break;
            }
        }
        String content = sb.toString();
       
        Log.d("InnerXML Gooten", content);
        
        return content;
    }
    
    private InputStream getInputStream (ZLFile file) throws IOException {
        
        return file.getInputStream();
        
    }
  
    private InputStream getInputStreamFromZip (ZLFile file) throws IOException {
      
     ZipFile daisy3zip = new ZipFile(daisy3XMLFileName);
     InputStream xmlInputStream = null;
  
         for (Enumeration<? extends ZipEntry> e = daisy3zip.entries(); e.hasMoreElements();) {
           
             ZipEntry ze = e.nextElement();
           
             String name = ze.getName();
           
             if (name.endsWith(".xml")) {
                 
               xmlInputStream = daisy3zip.getInputStream(ze);
               break;
             
           }
             
         }
       
         return xmlInputStream;
      
  }
    
    /*
     * Sets the XML file path to the xml file in the daisy3 zip
     * @param ZLFile Represents the XML file 
     */
   private void setXMLFilePath (ZLFile file){
        
        String myFilePrefix = MiscUtil.htmlDirectoryPrefix(file);
        
        final String extension = file.getExtension().intern();
        String name = file.getShortName();
        if(extension.equals("opf") && !name.startsWith("._")){
            ZLFile parentDirectory = file.getParent();
            List<ZLFile> children =  parentDirectory.children();
            for(ZLFile daisy3content : children){
                String str = daisy3content.getShortName();
               
                //Get the XML file name. This file contains the Daisy3 content
                if(daisy3content.getExtension().equals("xml")){
                    if(!str.startsWith("._"))
                    {
                          daisy3XMLFileName = myFilePrefix + daisy3content.getLongName();   
                         Log.d("Zip File Location",  myFilePrefix + daisy3content.getLongName());
                    }
                }
            }     
        }
    }
    
    
    /*
     * getInnerXML method
     * @param String of the Tag ID for the XML tag 
     * @return String contains the inner xml 
     */
    public String getInnerXML(String tagId){
        
        return xmlIdMap.get(tagId);
        
    }
    
    /*
     * getIdMap method
     * @return HashMap containg the Inner XML 
     */
    public HashMap<String, String> getIdMap (){
        
        return xmlIdMap;
        
    }
    
}
