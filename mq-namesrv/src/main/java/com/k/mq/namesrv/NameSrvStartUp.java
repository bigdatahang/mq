package com.k.mq.namesrv;

import com.k.mq.namesrv.core.NameSrvStarter;

public class NameSrvStartUp {
    private static NameSrvStarter nameSrvStarter;
    public static void main(String[] args) {
        nameSrvStarter = new NameSrvStarter(9090);
        nameSrvStarter.startServer();
    }
}
