
public class Utils {
	public static String queryCommonPrefix ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + 
			"PREFIX ifcowl:  <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\r\n" + 
			"PREFIX express:  <https://w3id.org/express#> \r\n" + 
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \r\n";
	
	public static String queryCustomizeProfix = "PREFIX : <http://www.semanticweb.org/panpan/bim-demo#>\r\n";

	//目前只测试过window 和 door
	public static  String queryWallConnectedSpecifiedItem(String type, String name) {
		type = type.substring(0, 1).toUpperCase() + type.substring(1); 
		
		String selectStatement="SELECT ?item ?ContainerWall ?ContainerWallName  WHERE{\r\n" + 
				"?item  rdf:type ifcowl:Ifc"+ type+" .\r\n" + 
				"?item ifcowl:name_IfcRoot ?label .\r\n" + 
				"?label  rdf:type ifcowl:IfcLabel .\r\n" + 
				"?label  express:hasString " +"\""+name+"\""+ ". \r\n" + 
				"?IfcRelFillsElement ifcowl:relatedBuildingElement_IfcRelFillsElement ?item .\r\n" + 
				"?IfcRelFillsElement  ifcowl:relatingOpeningElement_IfcRelFillsElement ?IfcOpeningElement .\r\n" + 
				"?IfcRelVoidsElement ifcowl:relatedOpeningElement_IfcRelVoidsElement ?IfcOpeningElement .\r\n" + 
				"?IfcRelVoidsElement ifcowl:relatingBuildingElement_IfcRelVoidsElement ?ContainerWall .\r\n" + 
				"?ContainerWall ifcowl:name_IfcRoot ?ContainerWallLabel .\r\n" + 
				"?ContainerWallLabel express:hasString ?ContainerWallName . }";
		String st=queryCommonPrefix+queryCustomizeProfix+selectStatement;
		return st;
	}

}
