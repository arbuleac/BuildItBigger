package com.arbuleac.jokeprovider;

public class JokeProvider {

    private static JokeProvider sInstance;

    public static JokeProvider get() {
        if (sInstance == null) {
            synchronized (JokeProvider.class) {
                if (sInstance == null) {
                    sInstance = new JokeProvider();
                }
            }
        }
        return sInstance;
    }

    public String next() {
        //TODO Replace with a fetch from somewhere.
        return "Q: how many programmers does it take to change a light bulb?" +
                "\n" +
                "A: none, that's a hardware problem";
    }

}
