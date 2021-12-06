package com.geekbrains.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamApi {

    public static void main(String[] args) {
        // 1 2 3 4 5 6
        // | | | | | |

        // действия со стримами
        // делятся на промежуточные (на элеметном конвеера),
        // то есть стрим продолжает существовать
        // и на терминальные, которые возвращают
        // значение отличное от стрима
        // дествия: фильтрация, преобразование, аггрегация
        // filter, map, reduce, forEach

        // forEach
        Stream.of(1, 2, 3, 4, 5)
                .forEach(x -> System.out.print(x + " "));
        System.out.println();

        // filter
        Stream.of(1, 2, 3, 4, 5, 6, 7)
                .filter(x -> x % 2 == 0)
                .forEach(x -> System.out.print(x + " "));
        System.out.println();

        // map - пробразование каждого элемента стрима
        // результат - стрм нового типа
        Stream.of(1, 2, 3, 4, 5)
                .map(x -> x * 4)
                .filter(x -> x > 9)
                .forEach(x -> System.out.print(x + " "));
        System.out.println();

        // reduce - аггрегация всей цепочки данных
        Optional<Integer> reduce = Stream.of(1, 2, 3, 4, 5)
                .reduce((x, y) -> x * y);
        reduce.ifPresent(System.out::println);

        Integer res = Stream.of(1, 2, 3, 4, 5)
                .reduce(0, Integer::sum);
        System.out.println(res);

        ArrayList<Integer> objects = Stream.of(1, 2, 3, 4, 5)
                .reduce(
                        new ArrayList<>(),
                        (list, item) -> {
                            list.add(item);
                            return list;
                        },
                        (l, r) -> l
                );

        System.out.println(objects);

        // collectors
        List<Integer> list = Stream.of(1, 2, 3, 4, 5)
                .collect(Collectors.toList());
        System.out.println(list);

        Map<String, Integer> map = Stream.of("a", "a", "aaaa", "b", "b", "b", "C")
                .map(String::toLowerCase)
                .collect(Collectors.toMap(
                        Function.identity(),
                        x -> 1,
                        Integer::sum
                ));
        System.out.println(map);
    }
}
