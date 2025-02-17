package edu.guidian.yurpc.fault.retry;

import edu.guidian.yurpc.model.RpcResponse;

import java.util.concurrent.Callable;

public interface RetryStrategy {
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;


}
