package com.gooddies.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author sad
 */
public class XPathEngine {

    private Document doc;
    private XPath xpathObj;
    private Map<String, XPathExpression> expressions = new HashMap<String, XPathExpression>();

    public XPathEngine(String xml) {
        byte[] bytes;
        try {
            bytes = xml.getBytes("UTF-8");
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            init(stream);
            stream.close();
        } catch (IOException ex) {
            Logger.getLogger(XPathEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public XPathEngine(InputStream stream) {
        init(stream);
    }

    public XPathEngine(File file) {
        try {
            FileInputStream stream = new FileInputStream(file);
            init(new BufferedInputStream(stream));
            stream.close();
        } catch (IOException ex) {
            Logger.getLogger(XPathEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private XPathExpression getExpression(String xpath) throws XPathExpressionException {
        if (!expressions.containsKey(xpath)) {
            XPathExpression expression = xpathObj.compile(xpath);
            expressions.put(xpath, expression);
        }
        return expressions.get(xpath);
    }

    private void init(InputStream stream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = (Document) builder.parse(stream);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            xpathObj = (XPath) xPathfactory.newXPath();
        } catch (Exception ex) {
            Logger.getLogger(XPathEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getTextValueByXPath(String xpath, Object parent) {
        try {
            XPathExpression expression = getExpression(xpath);
            return (String) expression.evaluate(parent, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(XPathEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getTextValueByXPath(String xpath) {
        return getTextValueByXPath(xpath, doc);
    }

    public Element getNodeByXPath(String xpath, Object parent) {
        try {
            XPathExpression expression = getExpression(xpath);
            return (Element) (Node) expression.evaluate(parent, XPathConstants.NODE);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(XPathEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Element getNodeByXPath(String xpath) {
        return getNodeByXPath(xpath, doc);
    }

    public NodeList getNodeSetByXPath(String xpath, Object parent) {
        try {
            XPathExpression expression = getExpression(xpath);
            return (NodeList) expression.evaluate(parent, XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(XPathEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public NodeList getNodeSetByXPath(String xpath) {
        return getNodeSetByXPath(xpath, doc);
    }
}
