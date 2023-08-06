package ru.yandex.tonychem.interpalsviewbooster.beans;

import ru.yandex.tonychem.interpalsviewbooster.engine.CacheManager;
import ru.yandex.tonychem.interpalsviewbooster.engine.CrawlEngine;
import ru.yandex.tonychem.interpalsviewbooster.engine.impl.BasicCrawlEngine;
import ru.yandex.tonychem.interpalsviewbooster.engine.model.Account;
import ru.yandex.tonychem.interpalsviewbooster.util.AppUtils;

import java.util.Map;
import java.util.Set;

public class BeansHolder {

    private static final Map<String, Set<Account>> visitedAccountsMap = AppUtils.loadVisitedAccounts();
    private static final CrawlEngine ENGINE = new BasicCrawlEngine();

    public static CacheManager sessionCacheManager = null;

    public static CrawlEngine engine() {
        return ENGINE;
    }

    public static Map<String, Set<Account>> cache() {
        return visitedAccountsMap;
    }
}
