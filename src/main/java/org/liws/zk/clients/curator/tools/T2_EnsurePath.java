package org.liws.zk.clients.curator.tools;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;
import org.liws.zk.clients.ZkProps;

/**
 * EnsurePath【过时的API】提供了一种能够确保数据节点存在的机制
 */
@Deprecated
public class T2_EnsurePath {

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		
		String path = "/curator_ensure_path/c1";
		
		client.usingNamespace( "curator_ensure_path" );
		
		EnsurePath ensurePath = new EnsurePath(path);
		// 试图创建指定节点，如果节点存在就不进行任何操作，也不对外抛出异常，否则正常创建节点
		ensurePath.ensure(client.getZookeeperClient());
		ensurePath.ensure(client.getZookeeperClient());   
		
		EnsurePath ensurePath2 = client.newNamespaceAwareEnsurePath("/c1");
		ensurePath2.ensure(client.getZookeeperClient());
	}
}