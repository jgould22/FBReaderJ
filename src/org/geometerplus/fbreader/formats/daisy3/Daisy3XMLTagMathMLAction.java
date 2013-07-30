package org.geometerplus.fbreader.formats.daisy3;

import java.util.HashMap;
import android.util.Log;


import org.geometerplus.fbreader.bookmodel.BookReader;

import org.geometerplus.zlibrary.core.xml.ZLStringMap;

public class Daisy3XMLTagMathMLAction extends Daisy3XMLTagAction {

  
    
    private static Daisy3XMLTagMathMLAction instance = null;
    
    private static HashMap<String, String> mathMLMap = new HashMap<String,String>();
    
    private static boolean hasMath = false;
    
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
      
      
    }

    @Override
    protected void doAtEnd(Daisy3XMLReader reader) {
        // TODO Auto-generated method stub
       
        
        
    }
    
    /*
     * hasMathMLHashMap Method
     * @return boolean returns false if mathMLMap is null 
     */
    public static boolean hasMathMLHashMap (){
       
        return hasMath;
        
    }
    
    /*
     * Setter methods
     * @param HashMap<String, String> sets the Map containing the MathML with tag ids for keys
     */
    public static void setMathMLHashMap(HashMap<String, String> map){
        
        if(hasMath == false){
            mathMLMap = map;
        }
    }

}
