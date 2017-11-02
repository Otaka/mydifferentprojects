package com.webscrapper;

import com.webscrapper.rules.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author Dmitry
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String url = "http://masterchef.stb.ua/ru/issue/sezon-5-vypusk-2/";
        AbstractRule insideIframe = new GoInside(
                new GetElementsByTag("iframe",
                        new GoInside(
                                new GetElementsByTag("video",
                                        new GetElementsByTag("source",
                                                new FilterNotProcessedAttributeValues("src",
                                                        new PushResultObject(HashMap.class),
                                                        new PushAttributeToResultObject("src", "link")
                                                )
                                        )
                                )
                        )
                )
        );

        WebScrapper webscraper = new WebScrapper(
                new UrlRule(url,
                        new GetElementsByTag("a",
                                new FilterElementsByRegex("href", ".*-chast-.*",
                                        new FilterNotProcessedAttributeValues("href",
                                                insideIframe
                                        )
                                )
                        )
                )
        );
        Context context = webscraper.process();
        List<Object> result = context.getResultsObjectList();
        for (Object obj : result) {
            System.out.println("Obj=" + obj);
        }
    }
}
