package org.xcolab.commons.html;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlUtilTest {


    @Test
    public void testLinkifyUrls__simpleUrl() {
        String input = "www.google.com";
        String expected = "<a rel='nofollow' href='http://www.google.com'>www.google.com</a>";
        String message = "Simple URL linkification failed.";
        assertEquals(message, expected, HtmlUtil.linkifyUrlsInHtml(input));
    }

    @Test
    public void testLinkifyUrls__umlauts() {
        String input = "https://en.wikipedia.org/wiki/Paavo_Järvi";
        String expected = "<a rel='nofollow' href='https://en.wikipedia.org/wiki/Paavo_Järvi'>https://en.wikipedia.org/wiki/Paavo_Järvi</a>";
        String message = "Umlauts are not handled correctly in linkification.";
        assertEquals(message, expected, HtmlUtil.linkifyUrlsInHtml(input));
    }

    @Test
    public void testLinkifyUrls__parentheses() {
        String input = "Hi, please check my website! (www.google.com)";
        String expected = "Hi, please check my website! (<a rel='nofollow' href='http://www.google.com'>www.google.com</a>)";
        String message = "Parentheses are not handled correctly in linkification.";
        assertEquals(message, expected, HtmlUtil.linkifyUrlsInHtml(input));
    }

    @Test
    public void testLinkifyUrls__anchorTag() {
        String input = "<a href=\"http://www.google.com\">www.google.com</a>";
        String expected = "<a href=\"http://www.google.com\">www.google.com</a>";
        String message = "An URL inside an anchor tag should not be linkified.";
        assertEquals(message, expected, HtmlUtil.linkifyUrlsInHtml(input));
    }

    @Test
    public void testLinkifyUrls__comma() {
        String input = "I have this great website here www.google.com, where you can search for stuff!";
        String expected = "I have this great website here <a rel='nofollow' href='http://www.google.com'>www.google.com</a>, where you can search for stuff!";
        String message = "A comma after the URL is not part of the URL.";
        assertEquals(message, expected, HtmlUtil.linkifyUrlsInHtml(input));
    }

    @Test
    public void testLinkifyUrls__imgTag() {
        String input = "Here's a very nice image: <img src=\"https://climatecolab.org/images/climateColab-logo.png\" />";
        String expected = "Here's a very nice image: <img src=\"https://climatecolab.org/images/climateColab-logo.png\" />";
        String message = "An URL inside an img tag should not be linkified.";
        assertEquals(message, expected, HtmlUtil.linkifyUrlsInHtml(input));
    }

    @Test
    public void testLinkifyUrls__multipleUrls() {
        String input = "Woow, multiple mixed URLs!!! www.google.com, <a href=\"http://www.reddit.com\">www.reddit.com</a>, http://facebook.com, \n https://climatecolab.org";
        String expected = "Woow, multiple mixed URLs!!! <a rel='nofollow' href='http://www.google.com'>www.google.com</a>, <a href=\"http://www.reddit.com\">www.reddit.com</a>, <a rel='nofollow' href='http://facebook.com'>http://facebook.com</a>, \n <a rel='nofollow' href='https://climatecolab.org'>https://climatecolab.org</a>";
        String message = "Multiple URLs are not linkifed correctly.";
        assertEquals(message, expected, HtmlUtil.linkifyUrlsInHtml(input));
    }

    @Test
    public void testLinkifyUrls__nestedTag() {
        String input = "<a href=\"www.google.com\"><b>www.google.com</b></a>";
        String expected = "<a href=\"www.google.com\"><b>www.google.com</b></a>";
        String message = "Nested tags break the linkification exception.";
        assertEquals(message, expected, HtmlUtil.linkifyUrlsInHtml(input));
    }

    @Test
    public void testLinkifyUrls__query() {
        String input = "Here's an interesting URL with query strings: http://picklemorty.com?key=value&key2=value2";
        String expected = "Here's an interesting URL with query strings: <a rel='nofollow' href='http://picklemorty.com?key=value&key2=value2'>http://picklemorty.com?key=value&key2=value2</a>";
        String message = "Linkification does not work correctly with query strings in the URL.";
        assertEquals(message, expected, HtmlUtil.linkifyUrlsInHtml(input));
    }
}
