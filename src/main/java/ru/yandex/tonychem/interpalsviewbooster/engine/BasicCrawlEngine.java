package ru.yandex.tonychem.interpalsviewbooster.engine;

import ru.yandex.tonychem.interpalsviewbooster.engine.exceptions.IncorrectCredentialsException;
import ru.yandex.tonychem.interpalsviewbooster.util.ResourceLocators;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicCrawlEngine implements CrawlEngine {

    private final HttpClient client;
    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36";
    private final String badCredentialsFlag = "If you have an account, please sign in below";

    private String cookies;

    public BasicCrawlEngine() {
        this.client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
    }

    @Override
    public void authorize(String username, String password) throws IncorrectCredentialsException, IOException {
        String csrfToken;
        String authorizationRequestBody;

        HttpRequest csrfTokenRequest = HttpRequest.newBuilder()
                .uri(ResourceLocators.MAIN.uri())
                .GET()
                .build();


        HttpResponse<String> csrfTokenResponse = null;

        try {
            csrfTokenResponse = client.send(csrfTokenRequest, HttpResponse.BodyHandlers.ofString());

            cookies = prepareCookie(csrfTokenResponse);
            csrfToken = prepareCSRFToken(csrfTokenResponse);

            authorizationRequestBody =
                    String.format("username=%s&password=%s&csrf_token=%s", username, password, csrfToken);

            HttpRequest loginRequest = HttpRequest.newBuilder()
                    .uri(ResourceLocators.AUTH.uri())
                    .header("user-agent", userAgent)
                    .header("referer", "https://www.interpals.net")
                    .header("cookie", cookies)
                    .header("content-type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(authorizationRequestBody))
                    .build();

            HttpResponse<String> authorizationRequest = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());

            if (authorizationRequest.body().contains(badCredentialsFlag)) {
                throw new IncorrectCredentialsException();
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Account> gatherAccounts(UserSearchQuery userSearchQuery) {
        return null;
    }

    private String prepareCookie(HttpResponse<String> initialResponse) {
        String interpalsSessid = null;
        String __ubic1 = null;
        String csrfCookieV2 = null;

        for (String cookie : initialResponse.headers().allValues("set-cookie")) {
            String[] splittedCookie = cookie.split(";");

            for (String keyValueString : splittedCookie) {
                if (keyValueString.contains("interpals_sessid")) {
                    interpalsSessid = keyValueString.substring("interpals_sessid".length() + 1);
                    break;
                } else if (keyValueString.contains("__ubic1")) {
                    __ubic1 = keyValueString.substring("__ubic1".length() + 1);
                    break;
                } else if (keyValueString.contains("csrf_cookieV2")) {
                    csrfCookieV2 = keyValueString.substring("csrf_cookieV2".length() + 1);
                    break;
                }
            }
        }

        return String.format("interpals_sessid=%s; __ubic1=%s; csrf_cookieV2=%s;",
                interpalsSessid, __ubic1, csrfCookieV2);
    }

    private String prepareCSRFToken(HttpResponse<String> initialResponse) {
        final String csrfPattern = "<meta name=\"csrf_token\" content=\"(.*?)\"";

        Pattern pattern = Pattern.compile(csrfPattern);
        Matcher matcher = pattern.matcher(initialResponse.body());

        String csrfToken = null;

        while (matcher.find()) {
            csrfToken = matcher.group(1);
            break;
        }

        return csrfToken;
    }
}
