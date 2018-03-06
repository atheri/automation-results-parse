import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class AutomationResultsParse {

    public static void main(String[] args) throws IOException {
        int passed, failed, skipped, total;
        passed = failed = skipped = total = 0;

        File input = new File("overview.html");
        Document doc = Jsoup.parse(input, "UTF-8");

        Elements suites = doc.getElementsByClass("suite");
        for (Element suite : suites) {
            Elements suiteChildren = suite.getAllElements();
            for (Element child : suiteChildren) {
                if (child.hasClass("passed number")) {
                    passed += Integer.valueOf(child.text());
                } else if (child.hasClass("skipped number")) {
                    skipped += Integer.valueOf(child.text());
                } else if (child.hasClass("failed number")) {
                    failed += Integer.valueOf(child.text());
                }

            }
        }
        total = passed + skipped + failed;

        System.out.format("Passed: %d | Skipped: %d | Failed: %d || Total: %d", passed, skipped, failed, total);
    }

}
