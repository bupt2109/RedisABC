package basic;

import redis.clients.jedis.Jedis;

/**
 * Haha
 *
 * @author yu
 * @version v1.0
 * @Description
 * @Date 2020/4/1
 */
public class Haha {
    public static void main(String[] args) {

        Jedis jedis = new Jedis("localhost");
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        System.out.println(value);
//        System.out.println("Hello world!");
    }
}
