package com.simplepl.grammar.comments;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class CommentRemoverTest {

    @Test
    public void testSingleLineComment() {
        CommentRemover commentRemover=new CommentRemover("123//3\n469");
        Assert.assertEquals("123   \n469", commentRemover.process());
        Assert.assertEquals("//3", commentRemover.getFoundComments().get(0).getData());
        Assert.assertEquals(false, commentRemover.getFoundComments().get(0).isMultiLine());
        Assert.assertEquals("123   \r\n123", new CommentRemover("123//3\r\n123").process());
    }

    @Test
    public void testMultiLineComment() {
        Assert.assertEquals("123       \n123", new CommentRemover("123/*345*/\n123").process());
        Assert.assertEquals("1    \n\n    6\n45", new CommentRemover("1/*23\n\n45*/6\n45").process());
        Assert.assertEquals("1    \r\n\n   \n   ", new CommentRemover("1/*23\r\n\n456\n45*").process());
        
        CommentRemover commentRemover=new CommentRemover("1/*23\n78 *78 \n*/456\r\n45*");
        Assert.assertEquals("1    \n       \n  456\r\n45*", commentRemover.process());
        Assert.assertEquals("/*23\n78 *78 \n*/", commentRemover.getFoundComments().get(0).getData());
        Assert.assertEquals(true, commentRemover.getFoundComments().get(0).isMultiLine());
    }
    
    @Test
    public void testSingleLineCommentStartsInString() {
        CommentRemover commentRemover=new CommentRemover("1\"2//3\" \nNext line");
        Assert.assertEquals("1\"2//3\" \nNext line", commentRemover.process());
    }
    
    @Test
    public void testMultilineCommentStartsInString() {
        Assert.assertEquals("Hello \"my string/*should not be r*/emoved\" \nNext line", new CommentRemover("Hello \"my string/*should not be r*/emoved\" \nNext line").process());
    }
}
