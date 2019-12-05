package org.liws.zk.clients.javaclient.t01_constructor;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.liws.zk.clients.ZkProps;
import org.liws.zk.clients.javaclient.impl.watcher.AbsWatcherWithCDL;
import org.liws.zk.clients.javaclient.impl.watcher.SimpWatcher1;


public class T_CreateSession {

	/**
	 * 创建一个最基本的ZooKeeper会话实例
	 */
	@Test
	public void test_createSession() throws Exception {
		AbsWatcherWithCDL watcher = new SimpWatcher1();
		// watcher: 这里的watcher用于监听客户端与服务端连接状态变化事件（在其它场合它还能用于监听节点事件）
		ZooKeeper zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher);
		System.out.println("Zookeeper构造方法执行完后，zookeeper.getState : " + zookeeper.getState());
		watcher.await();
		System.out.println("会话创建成功后，zookeeper.getState : " + zookeeper.getState());
		
		Thread.sleep(2000);
	}
	
	/**
	 * 创建一个最基本的ZooKeeper会话实例，复用sessionId和sessionPassWd（为啥？）
	 */
	@Test
	public void test_createSession2() throws Exception {
		
		AbsWatcherWithCDL watcher = new SimpWatcher1();
		ZooKeeper zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher);
		watcher.await();
		// 第一次会话创建成功后，记录下SessionId和SessionPasswd以用于复用
		long sessionId = zookeeper.getSessionId();
		byte[] passwd = zookeeper.getSessionPasswd();

		
		// 用正确的 sessionId 和 sessionPassWd，连接成功
		watcher = new SimpWatcher1();
		zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher, sessionId, passwd);
		watcher.await();
		
		// 用错误的 sessionId 和 sessionPassWd，接收到服务端的Expired事件通知
		watcher = new SimpWatcher1();
		zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher, 1l, "test".getBytes());
		watcher.await();  // XXX 程序无法终止
	}
}