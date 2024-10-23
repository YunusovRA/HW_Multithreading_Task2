package ru.netology;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Integer>> futures = new ArrayList<>();
        for (String text : texts) {
            String localText = text;
            Callable<Integer> task = () -> {
                int maxSize = 0;
                for (int i = 0; i < localText.length(); i++) {
                    for (int j = 0; j < localText.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (localText.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(localText.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };
            Future<Integer> future = executor.submit(task);
            futures.add(future);
        }

        int maxInterval = 0;
        for (Future<Integer> future : futures) {
            int interval = future.get();
            if (interval > maxInterval) {
                maxInterval = interval;
            }
        }

        executor.shutdown();

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Максимальный интервал: " + maxInterval);
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}