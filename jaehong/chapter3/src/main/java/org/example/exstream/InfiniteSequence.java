package org.example.exstream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.math.BigInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InfiniteSequence {

    public static void main(String...args) throws IOException {
        var br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(Stream.iterate(new int[]{0,1},
                        arr -> new int[]{arr[1], arr[0] + arr[1]})
                .parallel()
                .map((var arr) -> arr[0])
                .limit(Integer.parseInt(br.readLine()) + 1)
                .skip(10)
                .collect(Collectors.toList()));
    }

}
