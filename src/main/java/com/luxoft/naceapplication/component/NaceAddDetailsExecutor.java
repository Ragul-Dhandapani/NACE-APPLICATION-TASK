package com.luxoft.naceapplication.component;

import com.luxoft.naceapplication.dao.entities.NaceDetailsEntity;
import com.luxoft.naceapplication.repositories.NaceDetailsRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class NaceAddDetailsExecutor {

    public static final int N_THREADS = 10;
    private static final int PARTITION_SIZE = 500;

    /**
     * Inserting the Nace Application Information to the Database with the help of Executor Service which benefits for
     * multi threading
     *
     * @param naceDetails
     * @param naceDetailsRepository
     * @return
     * @throws InterruptedException
     */
    public List<NaceDetailsEntity> execute(List<NaceDetailsEntity> naceDetails , NaceDetailsRepository naceDetailsRepository)
            throws InterruptedException {

        List<NaceDetailsEntity> result;

        final AtomicInteger counter = new AtomicInteger(0);

        Collection<List<NaceDetailsEntity>> partitionedNaceDetails = naceDetails.parallelStream()
                .collect(Collectors.groupingBy(s -> counter.getAndIncrement() / PARTITION_SIZE)).values();

        ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);

        List<Callable<List<NaceDetailsEntity>>> callables = new ArrayList<>();
        partitionedNaceDetails.forEach(sublist -> {
            callables.add(new CreateNaceDetails(sublist , naceDetailsRepository));
        });

        Stream<List<NaceDetailsEntity>> map = executor.invokeAll(callables).stream().map(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                throw new IllegalStateException("Callable Statement Failed while execute in Executor Service" , e);
            }
        });

        result = map.flatMap(List::stream).collect(Collectors.toList());
        shutDownExecutorService(executor);
        return result;
    }

    /**
     * Shutdown the Executor
     *
     * @param executor
     */
    private void shutDownExecutorService(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1000 , TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
