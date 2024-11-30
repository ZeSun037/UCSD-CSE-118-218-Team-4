package com.example.wearos_gui;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.*;

public class RedisTest {

    private Jedis jedis;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        jedis = new Jedis("83.149.103.151", 6379);
    }

    @After
    public void tearDown() {
        if (jedis != null) {
            jedis.flushAll();
            jedis.close();
        }
    }

    @Test
    public void testSetAndGet() {
        // Set a key-value pair
        String setResult = jedis.set("key1", "value1");
        assertEquals("OK", setResult);

        // Get the value
        String value = jedis.get("key1");
        assertEquals("value1", value);
    }

    @Test
    public void testGetNonExistentKey() {
        // Attempt to get a value for a non-existent key
        String value = jedis.get("nonExistentKey");
        assertNull(value);
    }
}
