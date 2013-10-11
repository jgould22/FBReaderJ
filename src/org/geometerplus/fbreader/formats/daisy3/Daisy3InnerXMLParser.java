/**
 * Daisy3InnerXMLParser
 * Uses SAX parser to extract the content between two xml tags and store it as a string for retrieval 
 * @author Jordan Gould jordangould@gmail.com 
 * 
 */

package org.geometerplus.fbreader.formats.daisy3;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.xml.ZLStringMap;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import android.util.Log;

public class Daisy3InnerXMLParser {
    
    String xmlTag;
    String tagId;
    String innerXML;
    
    /**
     * @constructor
     * @param xmlFile
     * @param attributes
     */
    public  Daisy3InnerXMLParser(ZLFile xmlFile, ZLStringMap attributes){
        
        this.tagId = attributes.getValue("id");
        
        try{
            //Create SAX parser and open xml file as input source
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance(); 
         
            SAXParser saxParser = saxParserFactory.newSAXParser(); 
            
            XMLReader xmlReader = saxParser.getXMLReader(); 
            
            xmlReader.setFeature("http://xml.org/sax/features/namespaces", false);
            
            xmlReader.setContentHandler(new MyContentHandler(xmlReader)); 
            
            InputSource inputsource = new InputSource(xmlFile.getInputStream());
            
            //parse the xml file
            xmlReader.parse(inputsource); 
                    
            }
            catch(Exception e){
                
                e.printStackTrace();
                
            }
   
    }
     
    /*
     * Inner Class MyContentHandler
     * Parses xml file until it encounters a tag with the specified ID the invokes InnerConetent Handler
     */
    private class MyContentHandler implements ContentHandler { 

        private XMLReader xmlReader; 

        private MyContentHandler(XMLReader xmlReader) { 
            this.xmlReader = xmlReader; 
        } 

        public void setDocumentLocator(Locator locator) { 
        } 

        public void startDocument() throws SAXException { 
        } 

        public void endDocument() throws SAXException { 
        } 

        public void startPrefixMapping(String prefix, String uri) 
                throws SAXException { 
        } 

        public void endPrefixMapping(String prefix) throws SAXException { 
        } 

        public void startElement(String uri, String localName, String qName, 
                Attributes atts) throws SAXException { 
            if(tagId.equals(atts.getValue("id"))) { 
                
                StringBuilder startTag = new StringBuilder();
                
                if(qName.indexOf(":") != -1  ){
                    
                    qName = qName.substring(qName.indexOf(":")+1, qName.length());
                    
                }
                
                startTag.append("<" + qName + " ");
                
                for(int i=0 ;i < atts.getLength(); i++){
                    
                    startTag.append(" " + atts.getQName(i) + "=\""+ atts.getValue(i) + "\"");
                    
                }
                
                startTag.append(">");
                
                InnerContentHandler innerContentHandler = new InnerContentHandler(xmlReader, this, startTag.toString(),atts);
                
                xmlReader.setContentHandler(innerContentHandler); 
                
            } 
        } 

        public void endElement(String uri, String localName, String qName) 
                throws SAXException { 
        } 

        public void characters(char[] ch, int start, int length) 
                throws SAXException { 
        } 

        public void ignorableWhitespace(char[] ch, int start, int length) 
                throws SAXException { 
        } 

        public void processingInstruction(String target, String data) 
                throws SAXException { 
        } 

        public void skippedEntity(String name) throws SAXException { 
        } 

    } 
    
    
    /*
     * InnerClass InnerContentHandler
     * Takes xml within the xml file and stores it in a string for later retrieval 
     */
     private class InnerContentHandler implements ContentHandler { 

        private int depth = 1; 
        private XMLReader xmlReader; 
        private ContentHandler contentHandler;
        private StringBuilder stringBuilder = new StringBuilder();
        private boolean isFirstTag = true;
        private String startTagId;
        private String startTag;

        public InnerContentHandler(XMLReader xmlReader, ContentHandler contentHandler, String startTag, Attributes atts) { 
           
            this.contentHandler = contentHandler; 
            this.xmlReader = xmlReader; 
            this.startTag = startTag;
            this.startTagId = atts.getValue("id");
            
                 
        } 

        public void setDocumentLocator(Locator locator) { 
        } 

        public void startDocument() throws SAXException { 
        } 

        public void endDocument() throws SAXException { 
        } 

        public void startPrefixMapping(String prefix, String uri) 
                throws SAXException { 
        } 

        public void endPrefixMapping(String prefix) throws SAXException { 
        } 

        public void startElement(String uri, String localName, String qName, 
                Attributes atts) throws SAXException { 
        
            //if it is the first tag append it to the stringbuilder
            if(isFirstTag){
                
                stringBuilder.append(startTag);
 
                isFirstTag = false;
           
            }
            
            if(qName.indexOf(":") != -1  ){
                
                qName = qName.substring(qName.indexOf(":")+1, qName.length());
                
            }
            
            //append the tag
            stringBuilder.append("<" + qName);
            
            //append attributes
            for(int i=0 ;i < atts.getLength(); i++){
                
                stringBuilder.append(" " + atts.getQName(i) + "=\"" + atts.getValue(i) + "\"");
                
            }
            //close tag
            stringBuilder.append(">");
     
            depth++; 
            
        } 

        public void endElement(String uri, String localName, String qName) 
                throws SAXException { 
            //append close tag
            if(qName.indexOf(":") != -1  ){
                
                qName = qName.substring(qName.indexOf(":")+1, qName.length());
                
            }
            
            stringBuilder.append("</" + qName +">");
            
            depth--; 
            //if its the end of the innerXML tag then set the inner xml to innerXML and release control of the content handler 
            if(0 == depth) { 
               
               innerXML = stringBuilder.toString();
               
               xmlReader.setContentHandler(contentHandler); 
               
            } 
        } 

        public void characters(char[] ch, int start, int length) 
                throws SAXException { 
            
            stringBuilder.append(new String(ch, start, length));
            
        } 

        public void ignorableWhitespace(char[] ch, int start, int length) 
                throws SAXException { 
        } 

        public void processingInstruction(String target, String data) 
                throws SAXException { 
        } 

        public void skippedEntity(String name) throws SAXException { 
        } 
        
    } 
     
     
     /**
      * getXMLString
      * @return innerXML that has been parsed as a string
      */
     public String getXMLString(){
         
         return innerXML;
         
     }
    
    

}