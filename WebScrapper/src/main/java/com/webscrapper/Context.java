package com.webscrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Dmitry
 */
public class Context {
    
    private final Stack<ContextDocument> documents = new Stack<>();
    private final List<Object> resultList = new ArrayList<>();
    private final List<Object>resultsObjectList=new ArrayList<>();
    private final ThreadLocal currentObject=new ThreadLocal();

    public synchronized void pushCurrentObject(Object currentObject) {
        this.currentObject.set(currentObject);
        resultsObjectList.add(currentObject);
    }

    public synchronized Object getCurrentObject() {
        return currentObject.get();
    }

    public synchronized List<Object> getResultsObjectList() {
        return resultsObjectList;
    }

    public synchronized List<Object> getArbitraryResultList() {
        return resultList;
    }

    public synchronized Context addArbitraryResult(Object value) {
        resultList.add(value);
        return this;
    }

    public synchronized Document getDocument() {
        return documents
                .peek()
                .getDocument();
    }

    public synchronized List<Element> getElementsOnStackTop() {
        if (getElements() == null) {
            List<Element> elements = new ArrayList<>();
            elements.add(getDocument());
            return elements;
        } else {
            return getElements();
        }
    }

    public synchronized List<Element> getElements() {
        try {
            return documents
                    .peek().elementsStack
                    .peek();
        } catch (Exception ex) {
            return null;
        }
    }

    public synchronized void pushElements(List<Element> elements) {
        this.documents
                .peek().elementsStack
                .push(elements);
    }

    public synchronized void popElements() {
        try {
            this.documents
                    .peek()
                    .getElementsStack()
                    .pop();
        } catch (Exception ex) {
        }
    }

    public synchronized void pushDocument(Document document) {
        documents.push(new ContextDocument(document));
    }

    public synchronized Document popDocument() {
        return documents
                .pop()
                .getDocument();
    }

    private static class ContextDocument {

        private Stack<List<Element>> elementsStack = new Stack<>();
        private final Document document;

        public ContextDocument(Document document) {
            this.document = document;
        }

        public void setElementsStack(Stack<List<Element>> elementsStack) {
            this.elementsStack = elementsStack;
        }

        public Document getDocument() {
            return document;
        }

        public Stack<List<Element>> getElementsStack() {
            return elementsStack;
        }

    }

}
