package org.liws.zk.clients.base;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.junit.Test;
import org.liws.zk.clients.ZkProps;

public class T1_CreateSession {

	/**
	 * 创建一个最基本的ZooKeeper会话实例
	 */
	@Test
	public void test_createSession() throws Exception {
		// XXX Watcher事件处理器：用于处理来自zk服务端的watcher事件通知
		AbsWatcher watcher = new AbsWatcher() {
			@Override public void process(WatchedEvent event) {
				System.out.print("接收到WatchedEvent事件通知：【" + event + "】 ->> ");
				// 根据不同通知状态、事件状态做不同的处理
				if (KeeperState.SyncConnected == event.getState()) {
					// 如果客户端与服务端成功建立会话，则调用countDown()
					if(EventType.None == event.getType()) {
						countDown(); 
						System.out.println("这说明zookeeper会话已经异步创建成功了！");
					}
				} 
			}
		};
		
		// watcher: 这里的watcher用于监听客户端与服务端连接状态变化事件（在其它场合它还能用于监听节点事件）
		ZooKeeper zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher);
		System.out.println("Zookeeper构造方法执行完后，zookeeper.getState : " + zookeeper.getState());
		watcher.await();
		System.out.println("会话创建成功后，zookeeper.getState : " + zookeeper.getState());
		
		Thread.sleep(2000);
	}
	
	/**
	 * 复用sessionId和sessionPasswd来创建一个ZooKeeper会话实例，（为啥？）
	 */
	@Test
	public void test_createSession2() throws Exception {
		AbsWatcher watcher = new AbsWatcher() {
			@Override public void process(WatchedEvent event) {
				System.out.print("接收到WatchedEvent事件通知：【" + event + "】 ->> ");
				if (KeeperState.SyncConnected == event.getState()) {
					if(EventType.None == event.getType()) {
						countDown(); 
						System.out.println("这说明zookeeper会话已经异步创建成功了！");
					}
				} 
			}
		};
		ZooKeeper zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher);
		watcher.await();
		// 第一次会话创建成功后，记录下SessionId和SessionPasswd以用于复用
		long sessionId = zookeeper.getSessionId();
		byte[] sessionPasswd = zookeeper.getSessionPasswd();

		
		// 用正确的 sessionId 和 sessionPasswd，连接成功
		zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher, sessionId, sessionPasswd);
		watcher.await();
		
		// 用错误的 sessionId 和 sessionPasswd，接收到服务端的Expired事件通知
		zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher, 1l, "test".getBytes());
		watcher.await();  // XXX 程序无法终止
	}
}