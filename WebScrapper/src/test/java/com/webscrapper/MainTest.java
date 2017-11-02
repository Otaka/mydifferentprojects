/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webscrapper;

import com.webscrapper.rules.*;

import java.io.File;
import java.io.PrintStream;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class MainTest {

    @Test
    public void testMain() throws Exception {
        System.out.println("Started");
        String sourceUrl = "https://www.emuparadise.me/Nintendo_Entertainment_System_ROMs/List-All-Titles/13";
        //String sourceUrl = "https://www.emuparadise.me/Nintendo_Entertainment_System_ROMs/Games-Starting-With-Z/13";

        WebScrapper scrapper = new WebScrapper(
                new UrlRule(
                        sourceUrl,
                        new GetElementsByTag(
                                "a",
                                new GetElementsByAttributeValue("class", "index gamelist",
                                        new GoInside(
                                                new PushResultObject(MyClass.class),
                                                new GetElementsByTag("h1", new PushInnerTextToResultObject("title")),
                                                new GetElementsByAttributeValue("class", "download-link",
                                                        new GetElementsByTag("a",
                                                                new GetElementsMatchingOwnText(
                                                                        ".*Download.*",
                                                                        new PushAttributeToResultObject("href", "downloadUrl", true)
                                                                )
                                                        )
                                                ),
                                                new GetElementsByTag("img",
                                                        new GetElementsContainsAttribute("data-original",
                                                                new GetElementsByAttributeValue("class", "lazy",
                                                                        new PushAttributeToResultObject("data-original", "image", true)
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        Context context = scrapper.process();
        List listOfObjects = context.getResultsObjectList();
        for (int i = 0; i < listOfObjects.size(); i++) {
            try (PrintStream stream = new PrintStream(new File("F:/nesgame_" + i + ".html"))) {
                stream.println("<html><body>");
                int max = i + 200;
                max = Math.min(max, listOfObjects.size());
                for (int j = i; j < max; j++, i++) {
                    MyClass parsedObject = (MyClass) listOfObjects.get(j);
                    String downloadLink = StringEscapeUtils.escapeXml(parsedObject.getDownloadUrl());
                    String title = StringEscapeUtils.escapeXml(parsedObject.getTitle());
                    stream.println("<div>");
                    stream.println("<div><a href='" + downloadLink + "'>" + title + "</a></div>");
                    for (String imageUrl : parsedObject.getImageList()) {
                        stream.println("    <img src='" + StringEscapeUtils.escapeXml(imageUrl) + "' alt='" + title + "'/>");
                    }
                    stream.println("</div>");
                }

                stream.println("</body><html>");
            }
        }
    }

    public static class MyClass {

        private List<String> image = new ArrayList<>();
        private String downloadUrl;
        private String title;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public String getImage() {
            return image.isEmpty() ? null : image.get(0);
        }

        public void setImage(String image) {
            this.image.add(image);
        }

        public List<String> getImageList() {
            return image;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

    }

}
