package com.xiaomi.mone.grpc;

import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
public class CommonTest {

    @Test
    public void testMap() {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        IntStream.range(0,100000).parallel().forEach(i->{
            map.compute("name", (k, v) -> {
                System.out.println(v);
                if (null == v) {
                    return 1;
                }
                return v+1;
            });
        });
        System.out.println(map);
    }
}
