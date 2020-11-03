package com.prayerlaputa.gateway.router.impl;

import com.prayerlaputa.gateway.router.HttpEndpointRouter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author chenglong.yu
 * created on 2020/11/1
 */
public class HashHttpEndpointRouter implements HttpEndpointRouter {
    @Override
    public String route(List<String> endpoints) {
        if (null == endpoints || endpoints.size() == 0) {
            return null;
        }
        int idx = ThreadLocalRandom.current().nextInt(endpoints.size());
        return endpoints.get(idx);
    }
}
