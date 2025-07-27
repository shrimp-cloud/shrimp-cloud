package com.wkclz.camunda.util;

import com.wkclz.camunda.exception.CamundaException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author shrimp
 */
public class XmlUtil {

    public static Document getDocument(String xml) {
        return getDocument(xml, false);
    }

    public static Document getDocument(String xml, boolean namespace) {
        if (StringUtils.isBlank(xml)) {
            throw CamundaException.error("xml 不能为空");
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(namespace);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            return builder.parse(is);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }


    public static NodeList getNodes(Document document, XPathExpression expression) {
        if (document == null) {
            throw CamundaException.error("doc 不能为空");
        }
        if (expression == null) {
            throw CamundaException.error("expression 不能为空");
        }
        // 计算XPath, 并匹配所有的值
        try {
            return  (NodeList) expression.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void replaceValue(NodeList nodes, String newValue) {
        if (nodes == null || nodes.getLength() == 0) {
            return;
        }
        // 遍历所有匹配的属性，替换值
        for (int i = 0; i < nodes.getLength(); i++) {
            Attr attr = (Attr) nodes.item(i);
            attr.setValue(newValue);
        }
    }

    public static Node getNode(Document document, String expression) {
        XPathExpression xPathExpression = getXPathExpression(expression);
        try {
            return (Node)xPathExpression.evaluate(document, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
    public static Node getNode(Document document, XPathExpression expression) {
        try {
            return (Node)expression.evaluate(document, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public static XPathExpression getXPathExpression(String expression) {
        if (StringUtils.isBlank(expression)) {
            throw CamundaException.error("expression 不能为空");
        }
        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            xpath.setNamespaceContext(new BpmnNamespaceContext());
            return xpath.compile(expression);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }



    public static String writeDocument2String(Document document) {
        if (document == null) {
            throw CamundaException.error("document 不能为空");
        }
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            return writer.toString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }








    static class BpmnNamespaceContext implements NamespaceContext {
        private final Map<String, String> prefixToUriMap = new HashMap<>();
        public BpmnNamespaceContext() {
            // 添加默认的 BPMN 命名空间
            prefixToUriMap.put("bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL");
            prefixToUriMap.put("bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
            prefixToUriMap.put("dc", "http://www.omg.org/spec/DD/20100524/DC");
            prefixToUriMap.put("camunda", "http://camunda.org/schema/1.0/bpmn");
            prefixToUriMap.put("di", "http://www.omg.org/spec/DD/20100524/DI");
            prefixToUriMap.put("modeler", "http://camunda.org/schema/modeler/1.0");
            // 如果有其他需要使用的命名空间，可以在这里添加
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return prefixToUriMap.getOrDefault(prefix, null);
        }

        @Override
        public String getPrefix(String namespaceURI) {
            for (Map.Entry<String, String> entry : prefixToUriMap.entrySet()) {
                if (namespaceURI.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
            return null;
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            return prefixToUriMap.keySet().iterator();
        }
    }
}
