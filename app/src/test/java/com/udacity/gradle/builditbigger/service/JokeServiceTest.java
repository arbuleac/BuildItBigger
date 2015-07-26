package com.udacity.gradle.builditbigger.service;

import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @since 7/26/15.
 */
public class JokeServiceTest {
    JokeService service;
    private String joke;

    @Before
    public void setUp() throws Exception {
        service = new JokeService();
    }

    @After
    public void tearDown() throws Exception {
        service = null;
        assertNotNull("Joke should not be null", joke);
        assertNotEquals("Joke should not be empty", "", joke);
    }

    @Test
    public void testLoadJoke() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        service.loadJoke(new JokeService.JokeCallback() {
            @Override
            public void onNext(String joke) {
                JokeServiceTest.this.joke = joke;
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                fail("Error while getting joke");
            }
        });
        latch.await(20, TimeUnit.SECONDS);
    }
}