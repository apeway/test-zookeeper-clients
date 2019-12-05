package org.liws.zk.clients.zkclient;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;
import org.liws.zk.clients.ZkProps;

public class T_ZkClient {
	
	/**
	 * 1、使用ZkClient来创建一个ZooKeeper客户端
	 * @param IZkConnection [zkServers + sessionTimeout]
	 * @param connectionTimeout
	 * @param operationRetryTimeout
	 * @param zkSerializer
	 */
	private ZkClient zkClient = new ZkClient(ZkProps.CONNECT_STR, 5000);
	/*
	 * XXX ZkClient客户端可以通过注册相关的Listener事件监听来实现对zookeeper服务端事件的订阅。
	 * 并且和zookeeper原生的Watcher不同，ZkClient的Listener不是一次性的，只需注册一次就会一直生效。
	 */
	
	/**
	 * 使用ZkClient创建节点
	 */
	@Test
	public void test2_createNode() {
		/*
		 * -- 0、create:
		 * 		(String path, Object data, CreateMode mode)
		 * 		(String path, Object data, List<ACL> acl, CreateMode mode)
		 * --------------------------------------------------------------------------------------
		 * @param data : zkClient由于支持了自定义序列化，因此传入的节点数据可以是任意Object类型数据，这跟
		 * 		原生的zookeeper创建节点API只能传入byte[]类型数据不一样。
		 */
		
		/* 
		 * -- 1、createEphemeral: 创建临时节点
		 * 		(String path) 
		 * 		(String path, List<ACL> acl)
		 * 		(String path, Object data)
		 * 		(String path, Object data, List<ACL> acl)
		 */
		
		/*  
		 * -- 2、createEphemeralSequential： 创建持久顺序节点
		 * 		(String path, Object data)
		 * 		(String path, Object data, List<ACL> acl)
		 */
		
		/*  
		 * -- 3、createPersistent: 创建持久节点
		 * 		(String path) 
		 * 			(String path, boolean createParents)
		 * 				(String path, boolean createParents, List<ACL> acl)
		 * 		(String path, Object data)
		 * 		(String path, Object data, List<ACL> acl)
		 * --------------------------------------------------------------------------
		 * @param createParents : 控制是否需要在内部递归创建父节点。（原生的zookeeper创建
		 * 		节点API无法递归创建父节点）
		 */
		
		/*  
		 * -- 4、createPersistentSequential: 创建持久顺序节点
		 * 		(String path, Object data)
		 * 		(String path, Object data, List<ACL> acl)
		 */
		
        String path = "/zk-book/c1";
        zkClient.createPersistent(path, true);
	}
	
	/**
	 * ZkClient删除节点
	 */
	@Test
	public void test3_deleteNode() {
		/*
		 * delete(String path)
		 * delete(String path, int version)：强制指定版本删除
		 * deleteRecursive(String path)：递归删除非叶子节点（原生的zookeeper删除节点API只允许删除叶子节点）
		 */
		String path = "/zk-book";
        zkClient.deleteRecursive(path);
	}

	/**
	 * ZkClient获取子节点列表。
	 * @throws InterruptedException 
	 */
	@Test
	public void test4_getChildren() throws InterruptedException {
		
		String path = "/zk-book";
		
		/*
		 * 为指定节点注册IZkChildListener监听，当其发生以下4种事件时，就会监听到来自服务端的相应事件通知。
		 * 1、监听的节点被创建
		 * 2、监听的节点被删除
		 * 3、新增子节点
		 * 4、减少子节点
		 * --------------------------------------------------------------------------------
		 * @param parentPath 父节点路径
		 * @param currentChilds 最新的子节点相对路径列表，可能为null
		 */
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				System.out.println(parentPath + " 's child changed, currentChilds:" + currentChilds);
			}
        });
        
        zkClient.createPersistent(path);
        
        /*
		 * List<String> getChildren(String path)
		 * ---------------------------------------
		 * @return 返回的是子节点的相对路径列表
		 */
        System.out.println(zkClient.getChildren(path));
        
		Thread.sleep(500);
		zkClient.createPersistent(path + "/c1");

		Thread.sleep(500);
		zkClient.delete(path + "/c1");

		Thread.sleep(500);
		zkClient.delete(path);
		Thread.sleep(500);
	}
	
	/**
	 * ZkClient更新节点数据
	 * ZkClient读取节点数据
	 * @throws InterruptedException 
	 */
	@Test
	public void test5_readAndWriteData() throws InterruptedException {
		String path = "/zk-book";
        zkClient.createEphemeral(path, "123");
        
        /*
         * 为指定节点注册IZkDataListener监听，那么发生以下两种事件时，就会监听到来自服务端的相应事件通知：
         * 1、节点删除事件
         * 2、节点数据变更【内容变更或版本变更】事件通知
         * --------------------------------------------------------------------------------
         * @param dataPath 事件通知对应的节点路径
         * @param data 最新的节点数据
         */
		zkClient.subscribeDataChanges(path, new IZkDataListener() {
			@Override public void handleDataDeleted(String dataPath) throws Exception {
				System.out.println("Node " + dataPath + " deleted.");
			}
			@Override public void handleDataChange(String dataPath, Object data) throws Exception {
				System.out.println("Node " + dataPath + " changed, new data: " + data);
			}
		});
        
		/*
         * writeData(String path, Object data)
         * 	 writeData(String path, Object data, int expectedVersion)
         * 	   Stat writeDataReturnStat(String path, Object data, int expectedVersion)
         * --------------------------------------------------------------------------------
         * @param expectedVersion ：zookeeper的数据节点有数据版本的概念，可以使用这个数据版本
         * 		来实现类似CAS的原子操作。
         */
        zkClient.writeData(path,"456");
        
		/*
		 * <T extends Object> T readData(String path)
		 * 	 <T extends Object> T readData(String path, boolean returnNullIfPathNotExists)
		 * 	   <T extends Object> T readData(String path, Stat stat)
		 * -------------------------------------------------------------------------------------
		 * @param returnNullIfPathNotExists ：默认为false，表示删除不存在的节点会抛出异常，如果设置
		 * 		returnNullIfPathNotExists为true，删除不存在的节点就会直接返回null而不会抛出异常。
		 * @param stat : 
		 * @return : zkClient内部会负责将返回值反序列化为指定类型对象。
		 */
        System.out.println(zkClient.readData(path).toString());
        
        Thread.sleep(1000);
        zkClient.delete(path);
        Thread.sleep(1000);
	}

	@Test
	public void test6_exists() {
		/*
		 * exists(final String path)
		 */
		String path = "/zk-book";
        System.out.println("Node " + path + " exists: " + zkClient.exists(path));
	}

}