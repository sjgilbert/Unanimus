package com.sjgilbert.unanimus.parsecache;

import com.izzette.queuemap.QueueMap;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Map;
import java.util.Queue;

/**
 * 8/31/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public /* static */ final class ParseCache<K extends CharSequence, V extends ParseObject> {
    public static ParseCache<String, ParseObject> parseCache = new ParseCache<>();
    private final QueueMap<String, ParseQuery<V>> queueMap = new QueueMap<>();
    private final Queue<Map.Entry<String, ParseQuery<V>>> entryQueue = queueMap.asQueue;
    private final Map<String, ParseQuery<V>> queryMap = queueMap.asMap;

    private ParseCache(Object... ignore) {
        // Singleton
    }

    public static void init(Object... objects) {
        parseCache = new ParseCache<>(objects);
    }

    public static void free() {
        parseCache = null;
    }

    public static boolean isInit() {
        return (parseCache != null);
    }

    public ParseQuery<V> get(K key) {
        ParseQuery<V> parseQuery = queryMap.get(key.toString());
        remove(key);
        put(key, parseQuery);
        return parseQuery;
    }

    public ParseQuery<V> remove(K key) {
        return queryMap.remove(key.toString());
    }

    public ParseQuery<V> put(K key, ParseQuery<V> query) {
        return queryMap.put(key.toString(), query);
    }

    public boolean trim() {
        return trim(1);
    }

    public boolean trim(int number) {
        boolean ret = false;
        for (int i = 0; number > i; ++i) if (null != entryQueue.poll()) ret = true;
        return ret;
    }

    public void clear() {
        entryQueue.clear();
    }

    public boolean clearCache() {
        return clearCache(1);
    }

    public boolean clearCache(int number) {
        boolean ret = false;

        int i = 0;
        for (Map.Entry<String, ParseQuery<V>> entry : entryQueue) {
            if (!(number > i)) break;

            ParseQuery<V> parseQuery = entry.getValue();
            if (parseQuery.hasCachedResult()) {
                ++i;
                ret = true;
                parseQuery.clearCachedResult();
            }
        }

        return ret;
    }

    public boolean clearAllCache() {
        return clearCache(size());
    }

    public boolean isEmpty() {
        return entryQueue.isEmpty();
    }

    public int size() {
        return entryQueue.size();
    }
}
