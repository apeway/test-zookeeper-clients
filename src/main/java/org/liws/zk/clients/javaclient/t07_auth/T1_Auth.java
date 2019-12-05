package org.liws.zk.clients.javaclient.t07_auth;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.liws.zk.clients.ZkProps;

public class T1_Auth {

	
	private ZooKeeper getZookeeperWithNoneAuth() throws Exception {
		ZooKeeper zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, null);
		return zookeeper;
	}
	private ZooKeeper getZookeeperWithAuth1() throws Exception {
		ZooKeeper zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, null);
		// 对zookeeper会话添加权限信息
		zookeeper.addAuthInfo("digest", "foo:true".getBytes()); 
		return zookeeper;
	}
	private ZooKeeper getZookeeperWithAuth2() throws Exception {
		ZooKeeper zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, null);
		// 对zookeeper会话添加权限信息
		zookeeper.addAuthInfo("digest", "foo:false".getBytes()); 
		return zookeeper;
	}
	
	
	@Test
	public void test_schema_digest() throws Exception {
		final String PATH = "/zktest_auth";
		
		// 1、使用含auth1权限的zookeeper会话创建数据节点，创建的数据节点会包含auth1权限
		getZookeeperWithAuth1().create(PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
		
		// 2.1、使用含auth1权限的ZooKeeper会话，可成功访问含auth1权限的数据节点
		System.out.println("成功获取节点信息：" + getZookeeperWithAuth1().getData(PATH, false, null));

		// 2.2、使用不含权限的ZooKeeper会话，不能成功访问含权限的数据节点
		try {
			getZookeeperWithNoneAuth().getData(PATH, false, null);
		} catch (KeeperException.NoAuthException e) {
			System.out.println("获取节点信息失败：" + e.getMessage());
		}

		// 2.3、使用含auth2权限的ZooKeeper会话，不能成功访问含auth1权限的数据节点
		try {
			getZookeeperWithAuth2().getData(PATH, false, null);
		} catch (KeeperException.NoAuthException e) {
			System.out.println("获取节点信息失败：" + e.getMessage());
		}
	}

	/**
	 * Perms.CREATE和Perms.DELETE两种权限，都是针对子节点的权限控制
	 */
	@Test
	public void test_schema_digest_createAndDelete() throws Exception {
		final String PATH1 = "/zktest_auth_delete";
		final String PATH2 = "/zktest_auth_delete/child";
		  
		/*
		 * 由于PATH1节点是由含auth1权限的zookeeper会话创建的，
		 * 所以只有含auth1权限的zookeeper会话，才能为PATH1节点增加或删除子节点；
		 * 但对PATH1节点本身，任意zookeeper会话都能将其删除。
		 */
		getZookeeperWithAuth1().create(PATH1, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
		getZookeeperWithAuth1().create(PATH2, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);

		try {
			getZookeeperWithNoneAuth().delete(PATH2, -1);
		} catch (Exception e) {
			System.out.println("删除节点失败: " + e.getMessage());
			getZookeeperWithAuth1().delete(PATH2, -1);
			System.out.println("成功删除节点：" + PATH2);
		}

        getZookeeperWithNoneAuth().delete( PATH1, -1 );
        System.out.println( "成功删除节点：" + PATH1);
	}
	
	/**
	 * world权限模式测试 ——Ids.OPEN_ACL_UNSAFE
	 */
	@Test
	public void test_schema_world() throws Exception {
		final String PATH = "/zktest_auth_worldSchema";
		
		ZooKeeper zookeeperWithNoneAuth = getZookeeperWithNoneAuth();
		zookeeperWithNoneAuth.create(PATH, "init".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
 
		ZooKeeper zookeeperWithAuth1 = getZookeeperWithAuth1();
		System.out.println("获取节点信息成功：" + zookeeperWithAuth1.getData(PATH, false, null));
	}
	
	/**
	 * super权限模式测试
	 */
	@Test
	public void test_schema_super() throws Exception { 
		final String PATH = "/zktest_auth_superSchema";
		
		// 1、使用含auth1权限的zookeeper会话来创建数据节点，创建的数据节点会包含auth1权限
		getZookeeperWithAuth1().create(PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);

		// 2、使用含Super权限的zookeeper会话，可以操作含任意权限的数据节点。
		System.out.println("成功获取节点信息：" + getZookeeperWithSuperAuth().getData(PATH, false, null));
	}
	private ZooKeeper getZookeeperWithSuperAuth() throws Exception {
		ZooKeeper zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, null);
		// XXX 假设在zookeeper服务器上开启了super模式，且配置的权限标识为经过编码后的"foo:zk-book"
		String superAuth = "foo:zk-book";
		// 对zookeeper会话添加超管权限！
		zookeeper.addAuthInfo("digest", superAuth.getBytes()); 
		return zookeeper;
	}

}