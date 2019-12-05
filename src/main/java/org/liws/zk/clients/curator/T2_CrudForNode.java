package org.liws.zk.clients.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.liws.zk.clients.ZkProps;

public class T2_CrudForNode {

	/**
	 * 使用Curator删除节点
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();

		String PATH = "/zk-book/c1";
		//client.delete().deletingChildrenIfNeeded().forPath(PATH);
		
		/*
		 * 1、创建节点，可自由组合一系列Fluent风格接口来创建各种类型节点
		 */
		CreateBuilder create = client.create();
		create.creatingParentsIfNeeded()	// 递归创建
			.withMode(CreateMode.EPHEMERAL) // 临时节点
			// forPath可在创建节点时指定初始节点数据: 
			// 	 和ZkClient不同的是，data的类型是byte[]而不是Object（与zookeeper原生API一致）
			.forPath(PATH, "123".getBytes());	
		
		/*
		 * 2、获取节点数据 
		 */
		Stat oldStat = new Stat();
		GetDataBuilder getDataBuilder = client.getData();
		byte[] data = getDataBuilder
				// 获取节点数据的同时获取该节点的stat（传入的Stat实例用于存储服务端返回的最新节点状态信息）
				.storingStatIn(oldStat) 
				.forPath(PATH);
		System.out.println("节点数据: " + new String(data) + "，数据版本：" + oldStat.getVersion());
		/*
		 * 3、更新节点数据
		 */
		SetDataBuilder setDataBuilder = client.setData();
		Stat newStat = setDataBuilder
				.withVersion(oldStat.getVersion())	// 强制指定版本进行更新（用于实现CAS）
				.forPath(PATH, "456".getBytes());
		System.out.println("为节点setData成功, 新数据版本: " + newStat.getVersion());
		// 使用过期的stat再次进行更新
		try {
			setDataBuilder.withVersion(oldStat.getVersion()).forPath(PATH); 
		} catch (Exception e) {
			System.out.println("为节点setData失败，因为: " + e.getMessage());
		}
		// 更新后再获取数据
		data = client.getData().storingStatIn(newStat).forPath(PATH);
		System.out.println("节点数据: " + new String(data) + "，数据版本：" + newStat.getVersion());
		
		/*
		 * 4、删除节点
		 */
		DeleteBuilder delete = client.delete();
		delete.guaranteed()					// 强制保证删除
			.deletingChildrenIfNeeded()		// 递归删除
			.withVersion(newStat.getVersion()) // 强制指定版本进行删除
			.forPath(PATH);
		
		/*
		 * 5、其它操作
		 * 	client.checkExists()
		 *  client.getChildren()
		 * 	client.sync()
		 *  client.getACL()
		 */
		
		delete.guaranteed().forPath("/zk-book"); // 删不了？
		Thread.sleep(1000);
	}
	
}