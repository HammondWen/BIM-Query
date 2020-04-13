

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//
import org.apache.jena.system.JenaSystem;
import org.apache.jena.fuseki.main.*;
import be.ugent.IfcSpfReader;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
 
public class BimQuery {
    
    public static void main(String[] args) {
        
//        String inputFile="C:\\Users\\Panpan\\Documents\\wall-java-test.ifc";
//        String outputFile="C:\\Users\\Panpan\\Desktop\\ifc\\wall-java-test.ttl";  
    	System.out.println(System.getProperty("java.class.path"));  
       
    	RunFusekiServerDemo();
                  
       }
    
    
    public static void RunFusekiServerDemo() {
    	try {
    	 Dataset ds = DatasetFactory.createTxnMem() ;      
       FusekiServer server = FusekiServer.create()
                 .port(1234)
                 .add("/ds", ds)
                 .build();
       server.start();
       // ...       
       server.stop();}catch(Throwable any) {
    	   System.out.println("Error");
           any.printStackTrace();
       }
    	
    }
    
    public static void FusekiServerQueryDemo() {
    	
    	  RDFConnectionRemoteBuilder builder = RDFConnectionRemote.create()
                  .destination("http://localhost:3030/Example-CN-1/")
                  // Query only.
                  .queryEndpoint("query")
                  .updateEndpoint(null)
                  .gspEndpoint(null);
              
              Query query = QueryFactory.create("SELECT * { ?x ?y ?z } limit 20");

              
              try ( RDFConnection conn = builder.build()) { 
            	  conn.queryResultSet(query, ResultSetFormatter::out);
              }
    }
    
     public static void IFC2RDF(String ifcFileName,String RDFFileName) throws IOException{
              BufferedReader reader = new BufferedReader(new FileReader(ifcFileName));
              String line = null;
              // 一行一行的读,减少内存占用
              StringBuilder sb = new StringBuilder();
              while ((line = reader.readLine()) != null) {
                  sb.append(Unicode2Chinese(line));
                  sb.append("\r\n");
              }
              reader.close();
          
                 //写回去
              RandomAccessFile mm = new RandomAccessFile(ifcFileName, "rw");
              mm.write(sb.toString().getBytes());
              mm.close();
             
               IfcSpfReader r = new IfcSpfReader();
               try {
                   r.convert(ifcFileName, RDFFileName, r.DEFAULT_PATH);
               } catch (IOException e) {
                   e.printStackTrace();
               }
             }
     
     public static String Unicode2Chinese(String str) {
             int length=str.length();
             int count=0;
             StringBuilder res=new StringBuilder();
            String regEx = "\\\\X2\\\\[0-9a-zA-Z]*\\\\X0\\\\";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(str);
            while (matcher.find()) {
                String matchedUnicodeStr = matcher.group();
                StringBuilder convertedChineseStr=new StringBuilder();
                String matchedUnicodeStrAfterTrim = matchedUnicodeStr.substring(4, matchedUnicodeStr.length()-4);
                for(int i=0;i<matchedUnicodeStrAfterTrim.length();i=i+4) {
                     String singleUnicode = matchedUnicodeStrAfterTrim.substring(i,i+4);
                     convertedChineseStr.append((char)Integer.parseInt(singleUnicode,16));        
                }
                int index=matcher.start();
                res.append(str.substring(count, index));//添加前面不是unicode的字符
                res.append(convertedChineseStr);//添加转换后的字符
                count = index+matchedUnicodeStr.length();//统计下标移动的位置
    
            }
            res.append(str.substring(count, length));
            return res.toString();
     }
}






 
 

