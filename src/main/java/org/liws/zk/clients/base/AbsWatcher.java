package org.liws.zk.clients.base;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.Watcher;

/**
 * 内部维护了一个CountDownLatch(1)
 */
public abstract class AbsWatcher implements Watcher {
	
	/** 信号量：用于等待会话连接成功	*/
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
	 * 如果会话连接成功，则调用此方法来解除其它线程在await()调用上的阻塞。
	 */
	protected void countDown() {
		connectedSemaphore.countDown();
	}
	
	
	
}
