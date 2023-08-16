package org.example.chapter7;

import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;


public class PlayGround {
    public static void main(String...args) throws Exception {

        System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "12");

        LongFunction<Long> sequentialSum = (n) -> Stream.iterate(1L, i -> i + 1)
                .limit(n)
                .reduce(0L, Long::sum);

        LongFunction<Long> parallelSum = n -> Stream.iterate(1L, i -> i + 1)
                .limit(n)
                .parallel()
                .reduce(0L, Long::sum);


//        System.out.println("Iterative Sum done in: " + measurePerf( sequentialSum::apply, 10_000_000L) + " msecs");
//        System.out.println("Parallel forkJoinSum done in: " + measurePerf(parallelSum::apply, 10_000_000L) + " msecs" );
        System.out.println("ForkJoin sum done in: " + measurePerf(ForkJoinSumCalculator::forkJoinSum, 10_000_000L) + " msecs");
//        System.out.println("for sum done in: " + measurePerf(ForkJoinSumCalculator::iterate, 10_000_000L) + " msecs");

        Function<String, Integer> countWordIteratively = (s) -> {
            int count = 0;
            boolean lastSpace = true;
            for(var c: s.toCharArray()){
                if(Character.isWhitespace(c)){
                    lastSpace = true;
                }else{
                    if(lastSpace) count++;
                    lastSpace = false;
                }
            }
            return count;
        };

        final var SETENCE =
                "Nel	  mezzo del cammin di nostra vita " +
                        "mi ritrovai in una  selva oscura" +
                        "ch la dritta via era  smarrita ";

        IntStream.range(0, SETENCE.length())
                .mapToObj(SETENCE::charAt)
                .reduce(new WordCounter(0,true),
                        WordCounter::accumulate,
                        WordCounter::combine);


    }

    public static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();
    public static <T, R> long measurePerf(Function<T, R> f, T input) {
        long fastest = Long.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            R result = f.apply(input);
            long duration = (System.nanoTime() - start) / 1_000_000;
            System.out.println("Result: " + result);
            if (duration < fastest) {
                fastest = duration;
            }
        }
        return fastest;
    }
}

class ForkJoinSumCalculator extends RecursiveTask<Long> {
    private final long[] numbers;
    private final int start;
    private final int end;

    public static final long THRESHOLD = 10_000;

    public ForkJoinSumCalculator(long[] numbers){
        this(numbers, 0, numbers.length);
    }

    private ForkJoinSumCalculator(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        String format = "thread id: %d start: %d end %d\n";
        System.out.printf(format, Thread.currentThread().getId(), start, end);
        int length = end -start;
        if(length <= THRESHOLD){
            return computeSequentially();
        }
        var leftTask = new ForkJoinSumCalculator(numbers,start, start + length / 2);
        leftTask.fork();
        var rightTask = new ForkJoinSumCalculator(numbers, start + length/2, end);
        var rightResult = rightTask.compute();
        var leftResult = leftTask.join();
        return leftResult + rightResult;
    }

    public static long forkJoinSum(long n){
        var numbers = LongStream.rangeClosed(1, n).toArray();
        var task = new ForkJoinSumCalculator(numbers);
        return new ForkJoinPool().invoke(task);
    }

    private long computeSequentially() {
        long sum = 0;
        for(int i = start; i < end; i++){
            sum += numbers[i];
        }
        return sum;
    }
    public static long iterate(long n) {
        long sum = 0;
        for(int i = 0; i < n; i++){
            sum += i;
        }
        return sum;
    }
}

class WordCounter{
    private final int counter;
    private final boolean lastSpace;

    public WordCounter(int counter, boolean lastSpace) {
        this.counter = counter;
        this.lastSpace = lastSpace;
    }

    public WordCounter accumulate(Character c){
        if(Character.isWhitespace(c)) {
            return lastSpace ? this : new WordCounter(counter, true);
        }
        return lastSpace ? new WordCounter(counter + 1, false) : this;
    }

    public WordCounter combine(WordCounter wordCounter){
        return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
    }
    public int getCounter(){
        return counter;
    }
}

class WordCounterSpliterator implements Spliterator<Character> {
    private final String string;
    private int currentChar = 0;

    public WordCounterSpliterator(String string){
        this.string = string;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
        action.accept(string.charAt(currentChar++)); // 현재 문자를 소비
        return currentChar < string.length(); // 소비할 문자가 남아있으면 true를 반환
    }

    @Override
    public Spliterator<Character> trySplit() {
        int currentSize = string.length() - currentChar;

        if(currentSize < 10){
            return null;
        }
        for(int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++){
            if(Character.isWhitespace(string.charAt(splitPos))) {
                var spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));
                currentChar = splitPos;
                return spliterator;
            }
        }
        return null;
    }

    @Override
    public long estimateSize() {
        return string.length() - currentChar;
    }

    @Override
    public int characteristics() {
        return ORDERED + SIZED + NONNULL + IMMUTABLE;
    }
}