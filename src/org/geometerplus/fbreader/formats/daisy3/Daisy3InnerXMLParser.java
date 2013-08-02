package org.geometerplus.fbreader.formats.daisy3;

import java.util.zip.*;

import org.geometerplus.fbreader.formats.util.MiscUtil;

import  java.lang.StringBuilder;

import java.io.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Xml;

import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.xml.ZLStringMap;

import android.util.Log;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.transform.sax.SAXSource;

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
    private String tag;
    private ZLStringMap attributes;
  
    
    /*@constructor
     * Constructor takes ZLFile object and extracts the xml from the zip file and opens an iostream
     * @void
     * @param String xml file location 
     * @param String tag - tag to retrieve XML from 
     * 
     */
    public  Daisy3InnerXMLParser(ZLFile xmlFile, String Tag, ZLStringMap attributes){
        
            this.tag=Tag;
            this.attributes = attributes;
            try{
                parseXml(xmlFile); 
            }
            catch (Exception e){
                
                System.out.println(e.getMessage());
                
            }
            
    
        
    }
    
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
            if(tag.equals(qName)) { 
                System.out.println("START InnerXML " + qName); 
                
                InnerContentHandler innerContentHandler = new InnerContentHandler(xmlReader, this, qName, atts);
                
                xmlReader.setContentHandler(innerContentHandler); 
                
            } else { 
                System.out.println("START " + qName); 
            } 
        } 

        public void endElement(String uri, String localName, String qName) 
                throws SAXException { 
            System.out.println("END " + qName); 
        } 

        public void characters(char[] ch, int start, int length) 
                throws SAXException { 
            System.out.println(new String(ch, start, length)); 
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
    
     class InnerContentHandler implements ContentHandler { 

        private int depth = 1; 
        private XMLReader xmlReader; 
        private ContentHandler contentHandler;
        private StringBuilder stringBuilder = new StringBuilder();
        private String innerTagId;
        private boolean isFirstTag;
        private String startTag;
        private Attributes startAttributes;
       
        

        public InnerContentHandler(XMLReader xmlReader, ContentHandler contentHandler, String startTag, Attributes atts) { 
            this.contentHandler = contentHandler; 
            this.xmlReader = xmlReader; 
            this.startTag = startTag;
            this.startAttributes = atts;
            
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
            
            System.out.println("START InnerXML " + qName); 
            
            if(isFirstTag){
                
                innerTagId = atts.getValue("id");
                isFirstTag = false;
                stringBuilder.append("<" + startTag + " ");
                
                for(int i=0 ;i < startAttributes.getLength(); i++){
                    
                    stringBuilder.append(" " + startAttributes.getQName(i) + "=" + startAttributes.getValue(i));
                    
                }
                
            }
            
            //append the math tag
            stringBuilder.append("<" + qName + " ");
            
            //append attributes
            
            for(int i=0 ;i < atts.getLength(); i++){
                
                stringBuilder.append(" " + atts.getQName(i) + "=" + atts.getValue(i));
                
            }
            
            stringBuilder.append(">");
            
            depth++; 
        } 

        public void endElement(String uri, String localName, String qName) 
                throws SAXException { 
              
            System.out.println("END InnerXML " + qName); 
            
            stringBuilder.append("</" + qName +">");
            
            depth--; 
            if(0 == depth) { 
                
               xmlIdMap.put(innerTagId, stringBuilder.toString());
               
               Log.w("mathmloutput",stringBuilder.toString()); 
               
               xmlReader.setContentHandler(contentHandler); 
               
            } 
        } 

        public void characters(char[] ch, int start, int length) 
                throws SAXException { 
            
            System.out.println(new String(ch, start, length)); 
            
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
    
    
    private void parseXml(ZLFile xmlFile) throws IOException, XmlPullParserException{
        
        try{
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance(); 
     
        SAXParser saxParser = saxParserFactory.newSAXParser(); 
        
        XMLReader xmlReader = saxParser.getXMLReader(); 
        
        xmlReader.setFeature("http://xml.org/sax/features/namespaces", false);
        
        xmlReader.setContentHandler(new MyContentHandler(xmlReader)); 
        
        InputSource inputsource = new InputSource(getInputStream(xmlFile));
        
        xmlReader.parse(inputsource); 
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

    }      
    
    private InputStream getInputStream (ZLFile file) throws IOException {
        
        return file.getInputStream();
        
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
