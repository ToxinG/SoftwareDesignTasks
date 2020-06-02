package search;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import responses.SingleResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSearch {
    protected static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36";
    protected static final int RESULTS_NUM = 5;

    public abstract List<SingleResponse> search() throws IOException;

    public abstract String getEngineName();

    protected List<SingleResponse> extractLinks(Elements links) throws IOException {
        List<SingleResponse> results = new ArrayList<>();
        for (Element link : links) {
            String url = link.absUrl("href");
            if (url.startsWith("http://www.google.ru/url?q=")) {
                url = url.substring(url.indexOf('=') + 1);
            }
            url = URLDecoder.decode(url, "UTF-8");
            if (url.startsWith("http") || url.startsWith("https")) {
                results.add(new SingleResponse(url, link.text()));
            }
            if (results.size() == RESULTS_NUM) {
                break;
            }
        }
        return results;
    }
}
