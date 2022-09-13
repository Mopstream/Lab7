package com.mopstream.server.utility;

import com.mopstream.common.utility.Outputer;


public class ShutdownHandling {

    public static void addCollectionSavingHook() {
        Thread savingHook = new Thread(() ->
                Outputer.println("\nСервер принял спок.")
        );
        Runtime.getRuntime().addShutdownHook(savingHook);
    }
}
