package org.liws.zk.clients.curator.tools;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

/**
 * TestingServer用于模拟一个标准的zookeeper服务器，进行单元测试
 */
public class T3_TestingServer {

	public static void main(String[] args) throws Exception {
		TestingServer server = new TestingServer(2181);

		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(server.getConnectString())
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		
		String PATH = "/zookeeper";
		System.out.println(client.getChildren().forPath(PATH));
		
		server.close();
	}
}