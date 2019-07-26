/* 
 * Copyright or © or Copr. Arnold Fertin 2019
 *
 * This software is a computer program whose purpose is to perform image processing.
 *
 * This software is governed by the CeCILL-C license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL-C license as circulated
 * by CEA, CNRS and INRIA at the following URL "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license,
 * users are provided only with a limited warranty and the software's author, the holder of the economic rights, and the
 * successive licensors have only limited liability.
 *
 * In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or
 * developing or reproducing the software by the user in light of its specific status of free software, that may mean
 * that it is complicated to manipulate, and that also therefore means that it is reserved for developers and
 * experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling the security of their systems and/or data
 * to be ensured and, more generally, to use and operate it in the same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL-C license and that you
 * accept its terms.
 */
package util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author Arnold Fertin
 */
public final class ConcurrencyUtils
{

    private ConcurrencyUtils()
    {
    }

    private static ExecutorService threadPool = Executors.newCachedThreadPool(new CustomThreadFactory(new CustomExceptionHandler()));

    private static class CustomExceptionHandler implements Thread.UncaughtExceptionHandler
    {

        @Override
        public void uncaughtException(final Thread t,
                                      final Throwable e)
        {
            e.printStackTrace();
        }
    }

    private static class CustomThreadFactory implements ThreadFactory
    {

        private static final ThreadFactory DEFAULT_FACTORY = Executors.defaultThreadFactory();

        private final Thread.UncaughtExceptionHandler handler;

        CustomThreadFactory(final Thread.UncaughtExceptionHandler handler)
        {
            this.handler = handler;
        }

        @Override
        public Thread newThread(final Runnable r)
        {
            final Thread t = DEFAULT_FACTORY.newThread(r);
            t.setUncaughtExceptionHandler(handler);
            t.setDaemon(true);
            return t;
        }
    };

    private static int nTnreads = getNumberOfProcessors();

    /**
     * Returns the number of available processors.
     * <p>
     * @return number of available processors
     */
    public static int getNumberOfProcessors()
    {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Returns the current number of threads.
     * <p>
     * @return the current number of threads.
     */
    public static int getNumberOfThreads()
    {
        return nTnreads;
    }

    /**
     * Sets the number of threads.
     * <p>
     * @param n the number of threads.
     */
    public static void setNumberOfThreads(final int n)
    {
        if (n < 1)
        {
            throw new IllegalArgumentException("n must be greater or equal 1");
        }
        nTnreads = n;
    }

    /**
     * Submits a Runnable task for execution and returns a Future representing that task.
     * <p>
     * @param task task for execution
     * <p>
     * @return a handle to the task submitted for execution
     */
    public static Future<?> submit(final Runnable task)
    {
        if (threadPool.isShutdown() || threadPool.isTerminated())
        {
            threadPool = Executors.newCachedThreadPool(new CustomThreadFactory(new CustomExceptionHandler()));
        }
        return threadPool.submit(task);
    }

    /**
     * Waits for all threads to complete computation.
     * <p>
     * @param futures handles to running threads
     */
    public static void waitForCompletion(final Future<?>[] futures)
    {
        final int size = futures.length;
        try
        {
            for (int j = 0; j < size; j++)
            {
                futures[j].get();
            }
        }
        catch (ExecutionException ex)
        {
            ex.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
