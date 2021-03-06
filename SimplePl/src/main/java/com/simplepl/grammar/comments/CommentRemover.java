package com.simplepl.grammar.comments;

import com.simplepl.utils.ParserInputStream;
import java.util.ArrayList;
import java.util.List;

public class CommentRemover {

    private final List<Comment> foundComments = new ArrayList<>();
    private final ParserInputStream inputStream;

    public CommentRemover(String data) {
        foundComments.clear();
        inputStream = new ParserInputStream(data);
    }

    public String process() {
        StringBuilder sb = new StringBuilder(inputStream.size());
        while (!inputStream.eof()) {
            char c = inputStream.next();
            switch (c) {
                case '\"':
                    sb.append("\"");
                    if (inputStream.peek(0) == '\"' && inputStream.peek(1) == '\"') {
                        sb.append("\"\"");
                        inputStream.skip(2);
                        skipRawString(sb);
                    } else {
                        skipGenericString(sb);
                    }
                    break;
                case '/':
                    char nextChar = inputStream.peek(0);
                    if (nextChar == '/') {
                        inputStream.next();
                        //skip until the end of line
                        appendCharToStringBuilder(sb, ' ', skipUntilEndOfLineReturnCount());
                    } else if (nextChar == '*') {
                        inputStream.next();
                        //skip until the '*/'
                        skipUntilEndCommentReturnCount(sb);
                    }else{
                        sb.append(c);
                    }
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }

        return sb.toString();
    }

    private void skipRawString(StringBuilder sb) {
        while (!inputStream.eof()) {
            char c = inputStream.next();
            if (c == '\"' && inputStream.peek(0) == '\"' && inputStream.peek(1) == '\"') {
                sb.append("\"\"\"");
                inputStream.skip(2);
                break;
            } else {
                sb.append(c);
            }
        }
    }

    private void skipGenericString(StringBuilder sb) {
        while (!inputStream.eof()) {
            char c = inputStream.next();
            sb.append(c);
            if (c == '\"' && inputStream.peek(-1) != '\\') {
                break;
            }
        }
    }

    private void appendCharToStringBuilder(StringBuilder sb, char c, int count) {
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
    }

    private int skipUntilEndOfLineReturnCount() {
        StringBuilder commentText = new StringBuilder("//");
        int toSkip = "//".length();
        int startIndex = inputStream.getIndex() - toSkip;
        while (!inputStream.eof()) {
            char c = inputStream.next();
            if (c == '\r' || c == '\n') {
                inputStream.rewind();
                Comment comment = new Comment(commentText.toString(), startIndex, false);
                foundComments.add(comment);
                break;
            } else {
                commentText.append(c);
            }

            toSkip++;
        }

        return toSkip;
    }

    private void skipUntilEndCommentReturnCount(StringBuilder sb) {
        StringBuilder commentText = new StringBuilder("/*");
        int startIndex = inputStream.getIndex() - 2;
        sb.append("  ");
        while (!inputStream.eof()) {
            char c = inputStream.next();
            if (c == '*' && inputStream.peek(0) == '/') {
                inputStream.next();
                sb.append("  ");
                commentText.append("*/");
                Comment comment = new Comment(commentText.toString(), startIndex, true);
                foundComments.add(comment);
                break;
            } else if (c == '\r' || c == '\n') {
                sb.append(c);
                commentText.append(c);
            } else {
                sb.append(' ');
                commentText.append(c);
            }
        }
    }

    public List<Comment> getFoundComments() {
        return foundComments;
    }

}
