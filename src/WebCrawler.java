import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;


public class WebCrawler {

    private static final int MAX_DEPTH = 2;

    // create set and nested list for storing links and articles
    private HashSet<String> urlLinks;
    private List<List<String>> articles;
    // initialize set and list
    public  WebCrawler() {
        urlLinks = new HashSet<>();
        articles = new ArrayList<>();
    }
    //get all URLs that start with "https://www.javatpoint.com/" and add them to the set
    public void getPageLinks(String URL, int depth) {

        //we use the conditional statement to check whether we have already crawled the URL or not.
        // we also check whether the depth reaches to MAX_DEPTH or not
        if (urlLinks.size() != 50 && !urlLinks.contains(URL) && (depth < MAX_DEPTH) && (URL.startsWith("http://www.ilan.gov.tr") || URL.startsWith("https://www.ilan.gov.tr"))){
            System.out.println(">> Depth: " + depth + " [" + URL + "]");

            // use try catch block for recursive process
            try {
                // if the URL is not present in the set, we add it to the set
                urlLinks.add(URL);

                // fetch the HTML code of the given URL by using the connect() and get() method and store the result in Document
                Document doc = Jsoup.connect(URL).get();

                String doc2 =Jsoup.connect(URL).get().html();

                System.out.println(doc2);

                // we use the select() method to parse the HTML code for extracting links of other URLs and store them into Elements
                Elements availableLinksOnPage = doc.select("a[href]");

                // increase depth
                depth++;

                // for each extracted URL, we repeat above process
                for (Element ele : availableLinksOnPage) {
                    if(ele.attr("abs:href").startsWith("http://www.javatpoint.com\"") || ele.attr("abs:href").startsWith("https://www.javatpoint.com\"")) {
                        // call getPageLinks() method and pass the extracted URL to it as an argument
                        getPageLinks(ele.attr("abs:href"), depth);
                    }
                }
            }
            // handle exception
            catch (IOException e) {
                // print exception messages
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }
    public void getArticles() {
        Iterator<String> i = urlLinks.iterator();
        while (i.hasNext()) {


            // create variable doc that store document data
            Document doc;

            // we put the recursive code in a try-catch block
            try {

                doc = Jsoup.connect(i.next()).get();
                Elements availableArticleLinks = doc.select("a[href]");


                for (Element ele : availableArticleLinks) {

                    //we get only those article's  title which contain java 8
                    // use matches() and regx method to check whether text contains Java 8 or not
                    if (ele.text().contains("python")) {
                        System.out.println(ele.text());
                        // create temp list that stores articles
                        ArrayList<String> temp = new ArrayList<>();
                        temp.add(ele.text()); //get title of the article
                        temp.add(ele.attr("abs:href")); //get the URL of the article
                        // add article list in the nested article list
                        articles.add(temp);
                    }
                }
            }
            // handle exception
            catch (IOException e) {
                // show error message
                System.err.println(e.getMessage());
            }
        }
    }

    // create writeToFile() method to write data into file
    public void writeToFile(String fName) {
        // declare variable of type FileWriter
        FileWriter wr;

        //use try-catch block to write data into file
        try {
            // initialize FileWriter for fName
            wr = new FileWriter(fName);

            for(int i = 0; i<articles.size(); i++) {

                try {
                    String article = "- Title: " + articles.get(i).get(0) + " (link: " + articles.get(i).get(1) + ")\n";

                    // show the article and save it to the specified file
                    System.out.println(article);
                    wr.write(article);

                }catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
            // close FileWriter class object
            wr.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    // main() method start
    public static void main(String[] args) {
        // create instance of the ExtractArticlesExample class
        WebCrawler obj = new WebCrawler();

        // call getPageLinks() method to get all the page links of the specified URL
        obj.getPageLinks("http://www.javatpoint.com\"", 0);

        // call getArticles() method to find all the articles
        obj.getArticles();

        // call writeToFile() method to write all the articles in the specified file
        obj.writeToFile("Web Crawler Example");
    }

}