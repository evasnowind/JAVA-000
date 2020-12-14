package com.prayerlaputa.rpcfx.api;

import com.prayerlaputa.rpcfx.common.RpcfxException;

public class RpcfxResponse {

    private Object result;

    private boolean status;

    private RpcfxException exception;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public RpcfxException getRpcfxException() {
        return exception;
    }

    public void setRpcfxException(RpcfxException exception) {
        this.exception = exception;
    }
}
