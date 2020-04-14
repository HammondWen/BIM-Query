

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.jena.fuseki.main.*;
import be.ugent.IfcSpfReader;
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
        
    	
    	String ifcFile = "demo.ifc";  
    	String RDFFile = null;
		try {
			RDFFile = IFC2RDF(ifcFile,"");
		} catch (IOException e) {
			e.printStackTrace();
		}
    	RunFusekiServerDemo(RDFFile);
    	//FusekiServerQueryDemo();
                  
       }
    
    
    public static void RunFusekiServerDemo(String rdfFile) {
    	try {
    	 Dataset ds = DatasetFactory.createTxnMem() ;      
       FusekiServer server = FusekiServer.create()
                 .port(1234)
                 .add("/ds", ds)
                 .build();
       server.start();
       // ...      
 	  RDFConnectionRemoteBuilder builder = RDFConnectionRemote.create()
               .destination("http://localhost:1234/ds/")
               .queryEndpoint("query")
               .updateEndpoint("update")
               .gspEndpoint("data");
           
           Query query = QueryFactory.create("prefix demo: <http://www.semanticweb.org/demo#> \r\n" + 
           		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
           		"PREFIX : <http://www.semanticweb.org/panpan/bim-demo#>\r\n" + 
           		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
           		"PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + 
           		"PREFIX ifcowl:  <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\r\n" + 
           		"PREFIX express:  <https://w3id.org/express#> \r\n" + 
           		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n" + 
           		"		SELECT ?door  WHERE{\r\n" + 
           		"?door  rdf:type ifcowl:IfcDoor. \r\n" + 
           		"}  limit 20");

           
           try ( RDFConnection conn = builder.build()) { 
         	  //conn.load(outputFile);
         	  conn.loadDataset(rdfFile);
         	  conn.queryResultSet(query, ResultSetFormatter::out);
           }
       server.stop();}catch(Throwable any) {
    	   System.out.println("Error");
           any.printStackTrace();
       }
    	
    }
    
    public static void FusekiServerQueryDemo() {
    	
    	String outputFile="C:\\Users\\Panpan\\Desktop\\ifc\\wall-java-test.ttl";
    	  RDFConnectionRemoteBuilder builder = RDFConnectionRemote.create()
                  .destination("http://localhost:3030/demo/")
                  //.destination("http://localhost:3030/Example-CN-1/"
                  .queryEndpoint("query")
                  .updateEndpoint("update")
                  .gspEndpoint("data");
              
              Query query = QueryFactory.create("prefix demo: <http://www.semanticweb.org/demo#> \r\n" + 
              		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
              		"PREFIX : <http://www.semanticweb.org/panpan/bim-demo#>\r\n" + 
              		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
              		"PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + 
              		"PREFIX ifcowl:  <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\r\n" + 
              		"PREFIX express:  <https://w3id.org/express#> \r\n" + 
              		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n" + 
              		"		SELECT ?door  WHERE{\r\n" + 
              		"?door  rdf:type ifcowl:IfcDoor. \r\n" + 
              		"}  limit 20");

              
              try ( RDFConnection conn = builder.build()) { 
            	  //conn.load(outputFile);
            	  conn.loadDataset(outputFile);
            	  conn.queryResultSet(query, ResultSetFormatter::out);
              }
    }
    
     public static String IFC2RDF(String ifcFileName,String RDFFileName) throws IOException{
    	  
	 	  File file = new File(ifcFileName);
		  String fileName = file.getAbsolutePath();
		  String prefix=fileName.substring(0, fileName.lastIndexOf("."));
		  String suffix = fileName.substring(fileName.lastIndexOf("."));
		  String ifcFileNameCN=prefix+"-CN"+suffix;
		  
		  if(RDFFileName == null || RDFFileName.length() <= 0) {
			  RDFFileName=prefix+"-CN"+".ttl";
	    	 }
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
	      RandomAccessFile mm = new RandomAccessFile(ifcFileNameCN, "rw");
	      mm.write(sb.toString().getBytes());
	      mm.close();
	     
	       IfcSpfReader r = new IfcSpfReader();
	       try {
	           r.convert(ifcFileNameCN, RDFFileName, r.DEFAULT_PATH);
	       } catch (IOException e) {
	           e.printStackTrace();
	       }
	       
	       return RDFFileName;
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






 
 

