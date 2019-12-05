package org.liws.zk.clients.curator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import org.liws.zk.clients.ZkProps;

/**
 * 使用Curator的异步接口BackgroundCallback
 */
public class T3_Async {
    
    @Test
    public void testCreateNode() throws Exception {
    	CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
    	client.start();
    	
		CountDownLatch semaphore = new CountDownLatch(2);
		ExecutorService pool = Executors.newFixedThreadPool(2);
		String PATH = "/zk-book1";

    	System.out.println("Main thread: " + Thread.currentThread().getName());
        BackgroundCallback callback = new BackgroundCallback() {
        	/*
        	 * processResult()会在操作完成后被异步调用 
        	 * ---------------------------------------------------------------------------
        	 * @param client 当前客户端实例
        	 * @param event 服务端事件【需关注属性：type事件类型、resultCode响应码】
        	 */
            @Override
            public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
				System.out.println(Thread.currentThread().getName() + " => event[code: " + event.getResultCode()
						+ ", type: " + event.getType() + "]"); 
				semaphore.countDown();
			}
        };
       
        // 两次创建的节点名相同，第一次响应码为0，第二次响应码为-110
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
			.inBackground(callback, pool) // 此处传入了自定义的Executor来负责异步事件处理逻辑
			.forPath(PATH, "init".getBytes());
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
			.inBackground(callback) // 此处没有传入自定义的Executor，异步事件处理逻辑会交由zookeeper默认的EventThread负责
			.forPath(PATH, "init".getBytes());

        semaphore.await();
        pool.shutdown();
    }
}