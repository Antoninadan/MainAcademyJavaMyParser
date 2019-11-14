package com.mainacad;

import com.mainacad.model.Item;
import com.mainacad.service.PromNavigationParserService;
import com.mainacad.service.PromProductParserService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class ApplicationRunner {
    private static final Logger LOG = Logger.getLogger(ApplicationRunner.class.getName());
    private static final String BASE_URL = "https://prom.ua";

    public static void main(String[] args) {
        if (args.length == 0) {
            LOG.warning("You didnot input any keyword!");
            return;
        }

        List<Thread> threads = new ArrayList<>();
        List<Item> items = Collections.synchronizedList(new ArrayList<>());

        LOG.info("parser started!");
        try {
            String keyword = URLEncoder.encode(args[0], "UTF-8");
            String searchUrl = BASE_URL + "/search?search_term=" + keyword;
            PromNavigationParserService promNavigationParserService =
                    new PromNavigationParserService(items, searchUrl, threads);
            threads.add(promNavigationParserService);
            promNavigationParserService.start();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        boolean threadsFinished;
        do {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadsFinished = checkThreads(threads);
        } while (threadsFinished);

        LOG.info("parser finished! " + items.size() + "were extracted");
    }

    private static boolean checkThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            if (thread.isAlive() || thread.getState().equals(Thread.State.NEW)) {
                return true;
            }
        }
        return false;
    }
}