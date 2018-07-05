package com.hikari;

import static org.junit.Assert.assertTrue;

import com.alibaba.fastjson.JSON;
import com.hikari.bean.Book;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void testJSoup() {
        try {
            // 从 URL 加载 HTML
            Document document1 = Jsoup.connect("http://www.bing.com").get();
            // 从 文件 加载 HTML
            //Document document2 = Jsoup.parse(new File("D:/temp/index.html"), "utf-8");
            // 从 String 加载 HTML
            String html = "<html><head><title>First parse</title></head>" + "<body><p>Parsed HTML into a doc.</p></body></html";
            Document document3 = Jsoup.parse(html);
            System.out.println(document1.title());
            System.out.println(document3.title());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取 HTML 页面的 Fav 图标
     */
    @Test
    public void testFavIcon() {
        String favImage = "Not Found";

        try {
            Document document = Jsoup.connect("http://www.bing.com").get();
            Element element = document.head().select("link[href~=.*\\.(ico|png)]").first();

            if (element == null) {
                element = document.head().selectFirst("meta[itemprop=image]").firstElementSibling();

                if (element != null) {
                    favImage = element.attr("content");
                }
            } else {
                favImage = element.attr("href");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(favImage);
    }

    /***
     * 获取 HTML 页面中的素有链接
     */
    @Test
    public void testLink() {
        try {
            Document document = Jsoup.connect("http://www.bing.com").get();
            Elements links = document.select("a[href]");

            for (Element link : links) {
                System.out.println("link: " + link.attr("href"));
                System.out.println("text: " + link.text());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取 HTML 页面中的所有图像
     */
    @Test
    public void testImage() {
        try {
            Document document = Jsoup.connect("http://www.microsoft.com").get();
            Elements images = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]");

            for (Element image :
                    images) {
                System.out.println("src: " + image.attr("src"));
                System.out.println("height: " + image.attr("height"));
                System.out.println("width: " + image.attr("width"));
                System.out.println("alt: " + image.attr("alt"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取 URL 的元信息
     */
    @Test
    public void testMeta() {
        try {
            Document document = Jsoup.connect("http://www.bing.com").get();

            String description = document.select("meta[name=description]").get(0).attr("content");
            System.out.println("Meta description: " + description);
            String keywords = document.select("meta[name=keywords]").first().attr("content");
            System.out.println("Meta keyword: " + keywords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 在 HTML 页面中获取表单属性
     */
    @Test
    public void testInput() {
        try {
            String url = "https://book.douban.com/top250";

            ArrayList<String> list = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            String cookie = "douban";

            Document document;
            Elements elements;

            for (int i = 0; i <= 225; i = i + 25) {
                document = Jsoup.connect(url + "?start=" + i).get();
                elements = document.select("a[title]");

                String id;
                String href;
                for (Element element :
                        elements) {
                    href = element.attr("href");
                    id = href.substring(32, href.length() - 1);

                    list.add(id);
                }
                Thread.sleep(20000);
            }

            String api = "https://api.douban.com/v2/book/";

            for (String s :
                    list) {

                String doc = httpPost(api + s, map, cookie);
                Thread.sleep(20000);
                Book book = JSON.parseObject(doc, Book.class);
                System.out.println(book.toString());
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String httpPost(String url, Map<String, String> map, String cookie) throws IOException{
        //获取请求连接
        Connection con = Jsoup.connect(url);
        //遍历生成参数
        if(map!=null){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                //添加参数
                con.data(entry.getKey(), entry.getValue());
            }
        }
        //插入cookie（头文件形式）
        con.header("Cookie", cookie);
        return con.ignoreContentType(true).post().selectFirst("body").text();
    }

}
