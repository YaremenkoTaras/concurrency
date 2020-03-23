package com.yaremenko.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompletableFutureReview {

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        // supplyAsyncFuture -> supplier input
        CompletableFuture<String> supplyAsyncFuture = CompletableFuture.supplyAsync(() -> "Hello");

        //runAsync -> runnable input
        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Hello runnable");
        }, Executors.newSingleThreadExecutor());

        System.out.println(supplyAsyncFuture.get(1L, TimeUnit.SECONDS));
        runAsync.get(2L, TimeUnit.SECONDS);

        supplyAsyncFuture.thenAccept(result -> System.out.println(result + " world"));
        supplyAsyncFuture.get();

        supplyAsyncFuture.thenApply(result -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(result + " callback");
            return result;
        });

        supplyAsyncFuture.thenApply(result -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(result + " second callback");
            return result;
        });

        supplyAsyncFuture.get();

        ////////////////        ////////////////////////////

        CompletableFuture<String> asyncFuture = CompletableFuture.supplyAsync(() -> "async ");

        asyncFuture.thenApplyAsync(result -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(result + " callback");
            return result;
        });

        asyncFuture.thenApplyAsync(result -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(result + " second callback");
            return result;
        });

        asyncFuture.get();
        TimeUnit.SECONDS.sleep(5);

        /////////////////////        ////////////////////////////

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 10)
                .thenCompose(result ->
                        CompletableFuture.supplyAsync(() -> result * 2)
                ).thenCompose(result ->
                        CompletableFuture.supplyAsync(() -> result * 5)
                );

        System.out.println(future.get());

        ////////////////////        ////////////////////////////

        CompletableFuture<String> combination = asyncFuture.thenCombine(supplyAsyncFuture, (a, b) -> a + b);
        System.out.println(combination.get());

        ///////////////////        ////////////////////////////

        CompletableFuture<Integer> errorFuture = CompletableFuture
                .supplyAsync(() -> {
                    throw new RuntimeException("error in async running");
                }).handle((obj, err) -> {
                    System.out.print("Handle error " + err.getMessage());
                    return 10;
                });

        System.out.println(" result: " + errorFuture.get());


        CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("error in async running with exceptionally");
        })
                .exceptionally(err -> (5))
                .thenAccept(System.out::println);


        ///////////////////        ////////////////////////////


        CompletableFuture<String> what = CompletableFuture.supplyAsync(() -> "What");
        CompletableFuture<String> the = CompletableFuture.supplyAsync(() -> "the");
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "completableFuture");
        CompletableFuture<String> holds = CompletableFuture.supplyAsync(() -> "holds?");

        String collect = Stream.of(what, the, completableFuture, holds)
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" "));

        System.out.println(collect);

        System.exit(0);
    }

}
