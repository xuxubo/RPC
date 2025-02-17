package edu.guidian.yurpc.fault.tolerant;

import edu.guidian.yurpc.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {

    RpcResponse doTolerant(Map<String, Object> context, Exception e);



}
