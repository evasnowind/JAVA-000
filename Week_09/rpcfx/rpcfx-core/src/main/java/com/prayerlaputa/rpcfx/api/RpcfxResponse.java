package com.prayerlaputa.rpcfx.api;

import com.prayerlaputa.rpcfx.common.RpcfxException;
import lombok.Data;
import lombok.ToString;

@Data
public class RpcfxResponse {

    private Long requestId;

    private Object result;

    private boolean status;

    private RpcfxException exception;
}
