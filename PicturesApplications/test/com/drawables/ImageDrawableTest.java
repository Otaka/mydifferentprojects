/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drawables;

import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class ImageDrawableTest {

    public ImageDrawableTest() {
    }

    @Test
    public void testLoadImage() {
        new ImageDrawable().loadImage("http://img1.joyreactor.cc/pics/post/кот-гифки-почувствуй-разницу-я-вас-всех-запомнил-2880839.gif");
    }
}
