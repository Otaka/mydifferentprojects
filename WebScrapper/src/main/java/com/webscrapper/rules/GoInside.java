package com.webscrapper.rules;

import com.webscrapper.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Dmitry
 */
public class GoInside extends AbstractRule {

    public GoInside(AbstractRule... actions) {
        addActions(actions);
    }

    @Override
    public void process(Context context) {
        for (Element element : context.getElementsOnStackTop()) {

            String tag = element.tagName();
            if (tag.equalsIgnoreCase("a")) {
                processLink(element, context);
            } else if (tag.equalsIgnoreCase("iframe")) {
                processIframe(element, context);
            } else {
                throw new IllegalStateException("Cannot go inside element [" + element.tagName() + ", " + element.attributes() + "]");
            }
        }
    }

    private void processLink(Element linkElement, Context context) {
        String link = linkElement.attr("href");
        if (link.startsWith("#")) {
            return;
        }

        link = linkElement.absUrl("href");
        System.out.println("Go inside link [" + link + "]");
        processPage(link, context);
    }

    private void processIframe(Element iframeElement, Context context) {
        String link = iframeElement.attr("src");
        if (link.startsWith("#")) {
            return;
        }

        link = iframeElement.absUrl("src");
        System.out.println("Go inside iframe [" + link + "]");
        processPage(link, context);
    }

    private String getUrl(String url, String referer) throws MalformedURLException, ProtocolException, IOException {
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.114 Safari/537.36 Viv/1.9.818.50");
            if (referer != null) {
                connection.setRequestProperty("Referer", referer);
            }
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode <= 299) {
                return readInputStream(connection.getInputStream());
            } else {
                throw new RuntimeException("Response code " + responseCode + " for url [" + url + "]\n" + readInputStream(connection.getErrorStream()));
            }
        } finally {
            connection.disconnect();
        }
    }

    private String readInputStream(InputStream inputStream) throws UnsupportedEncodingException, IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "UTF-8");
        for (;;) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0) {
                break;
            }

            out.append(buffer, 0, rsz);
        }

        return out.toString();
    }

    private void processPage(String url, Context context) {
        Document doc;
        try {
            Document oldDoc = context.getDocument();
            String referer = null;
            if (oldDoc != null) {
                referer = oldDoc.baseUri();
            }

            String htmlData = getUrl(url, referer);
            doc = Jsoup.parse(htmlData, url);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error while loading page [" + url + "] " + ex.getMessage());
        }

        context.pushDocument(doc);
        try {
            processInnerUrlRules(context, getActions());
        } finally {
            context.popDocument();
        }
    }

    private void processInnerUrlRules(Context context, List<AbstractRule> rules) {
        for (AbstractRule rule : rules) {
            rule.process(context);
        }
    }
}
