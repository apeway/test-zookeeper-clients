package org.liws.zk.clients.curator.tools;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.utils.ZKPaths.PathAndNode;
import org.apache.zookeeper.ZooKeeper;
import org.liws.zk.clients.ZkProps;

/**
 * ZKPaths用于构建ZNode路径，递归创建和删除节点
 */
public class T1_ZKPaths {

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();

		String PATH = "/curator_zkpath_sample";

		System.out.println(ZKPaths.fixForNamespace("space",PATH)); // /space/curator_zkpath_sample
		System.out.println(ZKPaths.makePath(PATH, "sub")); // /curator_zkpath_sample/sub
		System.out.println(ZKPaths.getNodeFromPath("/curator_zkpath_sample/sub1")); // sub1

		PathAndNode pn = ZKPaths.getPathAndNode("/curator_zkpath_sample/sub1");
		System.out.println(pn.getPath()); // /curator_zkpath_sample
		System.out.println(pn.getNode()); // sub1


		ZooKeeper zookeeper = client.getZookeeperClient().getZooKeeper();
		String dir1 = PATH + "/child1";
		String dir2 = PATH + "/child2";
		// 递归创建
		ZKPaths.mkdirs(zookeeper, dir1); 
		ZKPaths.mkdirs(zookeeper, dir2);
		System.out.println(ZKPaths.getSortedChildren(zookeeper, PATH)); // [child1, child2]

		// 递归删除
		ZKPaths.deleteChildren(client.getZookeeperClient().getZooKeeper(), PATH, true); 
	}
}