package com.geekbrains.stream;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Example {

    public static void main(String[] args) {

        Foo foo = new Foo() {
            // внутренний анонимный класс
            @Override
            public void foo() {
                System.out.println("Hello world");
            }
        };
        System.out.println(foo.getClass());
        foo.bar();

        Foo foo1 = () -> {
            System.out.println("Hello world");
        };
        foo1.foo();

        Foo2 foo2 = Integer::sum;

        Calculator calculator = new Calculator();

        int r1 = calculator.apply(1, 2, Integer::sum);
        int r2 = calculator.apply(3, 2, Example::multiply);
        int r3 = calculator.apply(5, 1, (a, b) -> a - b);

//        foo = (x, y) => {
//            any
//        }
//
//        bar = foo;
//        bar(1, 3);

        System.out.println(r1 + " " + r2 + " " + r3);
        InterfaceProcessor processor = new InterfaceProcessor();
        processor.doActionWith("Hello", str -> System.out.println(str + " world!"));
        System.out.println(processor.convert(2, String::valueOf).getClass());


        Consumer<String> printer = System.out::println;
        printer.accept("Hello");

        Predicate<Integer> isOdd = x -> x % 2 == 1;

        Function<String, Integer> mapper = String::length;

        Supplier<String> getter = () -> "Supplied object";
        Supplier<Map<String, Set<Integer>>> mapGetter = HashMap::new;

        Map<String, Set<Integer>> map =
                putInto("123", 2, HashMap::new, TreeSet::new);

        System.out.println(map);
    }

    static Map<String, Set<Integer>> putInto(String key, Integer val,
                        Supplier<Map<String, Set<Integer>>> mapSupplier,
                        Supplier<Set<Integer>> setSupplier) {
        Set<Integer> set = setSupplier.get();
        set.add(val);
        Map<String, Set<Integer>> map = mapSupplier.get();
        map.put(key, set);
        return map;
    }

    static int multiply(int a, int b) {
        return a * b;
    }
}
