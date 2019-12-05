package org.liws.zk.jute;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.apache.jute.BinaryInputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.apache.zookeeper.server.ByteBufferInputStream;
import org.liws.zk.jute.model.MockReqHeader;

/**
 * 使用Jute进行序列化
 */
public class T_Jute {

	public static void main(String[] args) throws Exception {
		MockReqHeader srcHeader1 = new MockReqHeader(0x00000001, "type1");
		MockReqHeader srcHeader2 = new MockReqHeader(0x00000002, "type2");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
		// 通过OutputArchive进行序列化，将对象序列化到指定tag中去
		srcHeader1.serialize(boa, "srcHeader1");
		srcHeader2.serialize(boa, "srcHeader2");

		// 这里通常是TCP网络传输对象
		ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());

		ByteBufferInputStream bbis = new ByteBufferInputStream(bb);
		BinaryInputArchive bbia = BinaryInputArchive.getArchive(bbis);
		// 通过InputArchive进行反序列化，从指定tag中反序列化出对象
		MockReqHeader destHeader1 = new MockReqHeader();
		destHeader1.deserialize(bbia, "srcHeader1");
		MockReqHeader destHeader2 = new MockReqHeader();
		destHeader2.deserialize(bbia, "srcHeader2");
		
		System.out.println(srcHeader1.equals(destHeader1));
		System.out.println(srcHeader2.equals(destHeader2));
	}
}
