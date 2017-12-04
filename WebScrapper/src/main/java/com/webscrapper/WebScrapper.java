package com.webscrapper;

import com.webscrapper.rules.AbstractRule;
import com.webscrapper.rules.UrlRule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Dmitry
 */
public class WebScrapper {

    private final List<UrlRule> urlRules = new ArrayList<>();

    public WebScrapper() {
    }

    public WebScrapper(UrlRule... urlRules) {
        for (UrlRule rule : urlRules) {
            addUrlRule(rule);
        }
    }

    public final WebScrapper addUrlRule(UrlRule urlRule) {
        urlRules.add(urlRule);
        return this;
    }

    public List<UrlRule> getUrlRules() {
        return urlRules;
    }

    public Context process() throws IOException {
        Context context = new Context();
        for (UrlRule urlRule : urlRules) {
            try {
                processUrl(urlRule, context);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return context;
    }

    private void processUrl(UrlRule urlRule, Context context) throws IOException {
        System.out.println("Begin to process url " + urlRule.getUrl());
        Document doc = Jsoup.connect(urlRule.getUrl()).get();
        context.pushDocument(doc);
        try {
            processInnerUrlRules(context, urlRule.getInnerRules());
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
