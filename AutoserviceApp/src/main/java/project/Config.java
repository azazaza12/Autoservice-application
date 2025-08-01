package project;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Config {
    public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static  String DB_URL;
    public static  String USER;
    public static  String PASS;
    public static void initializeConfig(){
        try {
            // Создать новый документ XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;

            // Прочитать XML-файл и создать документ XML
            File inputFile = new File("config.xml");
            document = builder.parse(inputFile);
            document.getDocumentElement().normalize();

            // Извлечь параметры из документа XML
            NodeList nodeList = document.getElementsByTagName("parameter");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String name = element.getAttribute("name");
                    String value = element.getTextContent();

                    if (name.equals("url")) {
                        DB_URL= value;
                    } else if (name.equals("username")) {
                        USER = value;
                    } else if (name.equals("password")) {
                        PASS = value;
                    }
                }
            }

            System.out.println("Параметры успешно извлечены из файла конфигурации XML.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
