package search;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import responses.SingleResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class YandexSearch extends AbstractSearch {
    private static final String ENGINE_NAME = "Yandex";
    private static final String QUERY_BASE = "https://www.yandex.ru/search/?text=";
    private String query;

    public YandexSearch(String query) {
        this.query = query;
    }

    @Override
    public List<SingleResponse> search() throws IOException {
        Elements links = Jsoup.connect(getUrlForQuery(query))
                .userAgent(USER_AGENT)
                .get()
                .select("div h2");
        return extractLinks(links);
    }

    private String getUrlForQuery(String query) throws UnsupportedEncodingException {
        return QUERY_BASE + URLEncoder.encode(query, "UTF-8") + String.format("&numdoc=%d", RESULTS_NUM);
    }

    public String getEngineName() {
        return ENGINE_NAME;
    }
}
