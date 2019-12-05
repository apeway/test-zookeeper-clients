package org.liws.zk.clients.curator.tools;

import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.TestingZooKeeperServer;

/**
 * TestingCluster用于模拟zookeeper集群环境
 */
public class T4_TestingCluster {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		TestingCluster cluster = new TestingCluster(3);
		cluster.start();
		Thread.sleep(2000);

		TestingZooKeeperServer leader = null;
		for (TestingZooKeeperServer zs : cluster.getServers()) {
			System.out.print(zs.getInstanceSpec().getServerId() + "-");
			System.out.print(zs.getQuorumPeer().getServerState() + "-");
			System.out.println(zs.getInstanceSpec().getDataDirectory().getAbsolutePath());
			if (zs.getQuorumPeer().getServerState().equals("leading")) {
				leader = zs;
			}
		}
		
		leader.kill();
		System.out.println("--After leader kill:");
		for (TestingZooKeeperServer zs : cluster.getServers()) {
			System.out.print(zs.getInstanceSpec().getServerId() + "-");
			System.out.print(zs.getQuorumPeer().getServerState() + "-");
			System.out.println(zs.getInstanceSpec().getDataDirectory().getAbsolutePath());
		}
		cluster.stop();
	}
}