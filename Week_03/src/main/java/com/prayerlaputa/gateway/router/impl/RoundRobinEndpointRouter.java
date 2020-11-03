package com.prayerlaputa.gateway.router.impl;

import com.prayerlaputa.gateway.router.HttpEndpointRouter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenglong.yu
 * created on 2020/11/2
 */
public class RoundRobinEndpointRouter implements HttpEndpointRouter {

    private ConcurrentHashMap<Integer, Integer> rrIndexMap = new ConcurrentHashMap<>();

    @Override
    public String route(List<String> endpoints) {
        if (null == endpoints || endpoints.size() == 0) {
            return null;
        }
        int hash = endpoints.hashCode();
        int idx = rrIndexMap.getOrDefault(hash, 0);
        rrIndexMap.put(hash, (idx+1) % endpoints.size());
        return endpoints.get(idx);
    }
}
