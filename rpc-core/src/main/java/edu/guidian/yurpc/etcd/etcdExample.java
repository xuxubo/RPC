package edu.guidian.yurpc.etcd;


import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class etcdExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Client client = Client.builder().endpoints("http://localhost:2379").build();
        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("key".getBytes());
        ByteSequence value = ByteSequence.from("value".getBytes());
        kvClient.put(key, value).get();
        CompletableFuture<GetResponse> getFuture = kvClient.get(key);
        GetResponse getResponse = getFuture.get();
        System.out.println(getResponse.toString());
        kvClient.delete(key).get();
    }
}
