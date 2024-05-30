package flo.no.kanji.util;

import java.util.concurrent.Callable;

public class FunctionUtils {

    public static void executeWithRetry(Runnable task, final int maxRetries) {
        Callable<Void> callable = () -> {
            task.run();
            return null;
        };
        executeWithRetry(callable, maxRetries);
    }

    public static <T> T executeWithRetry(final Callable<T> task, final int maxRetries) {
        int attempt = 0;
        while (true) {
            try {
                return task.call();
            } catch (Exception e) {
                attempt++;
                if (attempt > maxRetries) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
