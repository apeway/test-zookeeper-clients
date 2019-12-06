package org.liws.zk.clients.base.impl.watcher;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.Watcher;

public abstract class AbsWatcherWithCDL implements Watcher {
	
	/** 反映会话是否连接成功的信号量	 */
	private CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	/**
	 * 一直等待直到会话连接成功
	 */
	public void await() {
		try {
			connectedSemaphore.await(); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 如果会话连接成功，调用此方法解除在await()调用上的阻塞。
	 */
	protected void countDown() {
		connectedSemaphore.countDown();
	}
	
	
	
}
