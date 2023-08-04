package com.liqiang.SimpeEcode;

import com.liqiang.utils.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.text.SimpleDateFormat;

public class MessageEncoder extends MessageToByteEncoder<Message> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
//		// TODO Auto-generated method stub
//		// 写入开头的标志
//		out.writeInt(msg.getHead().getHeadData());
//		// 写入包的的长度
//		out.writeInt(msg.getContent().length);
//		byte[] typeByte = new byte[50];
//		/**
//		 * type定长50个字节
//		 *  第一个参数 原数组
//		 *  第二个参数 原数组位置
//		 *  第三个参数 目标数组
//		 *  第四个参数 目标数组位置
//		 *  第五个参数 copy多少个长度
//		 */
//		byte[] indexByte=msg.getHead().getType().getBytes();
//		try {
//			System.arraycopy(indexByte, 0, typeByte, 0,indexByte.length>typeByte.length?typeByte.length:indexByte.length);
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		//写入消息类型
//		out.writeBytes(typeByte);
//		byte[] tokenByte = new byte[50];
//		/**
//		 * token定长50个字节
//		 *  第一个参数 原数组
//		 *  第二个参数 原数组位置
//		 *  第三个参数 目标数组
//		 *  第四个参数 目标数组位置
//		 *  第五个参数 copy多少个长度
//		 */
//		 indexByte=msg.getHead().getToken().getBytes();
//		try {
//			System.arraycopy(indexByte, 0, tokenByte, 0,indexByte.length>tokenByte.length?tokenByte.length:indexByte.length);
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//
//		//写入令牌
//		out.writeBytes(tokenByte);
//		byte[] createTimeByte = new byte[50];
//		SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String time = format0.format(msg.getHead().getCreateDate());
//		indexByte=time.getBytes();
//		System.arraycopy(indexByte, 0, createTimeByte, 0,indexByte.length>createTimeByte.length?createTimeByte.length:indexByte.length);
//		//写入令牌生成时间
//		out.writeBytes(createTimeByte);
//
//		// 写入消息主体
//		out.writeBytes(msg.getContent());
		out.writeBytes(msg.getHEADER());
		out.writeBytes(ByteUtil.intToBytesLittleEndian(msg.getContentLength()));
		System.out.println("encode Length: "+ByteUtil.bytesToHex(ByteUtil.intToBytesLittleEndian(msg.getContentLength())));
		out.writeBytes(ByteUtil.intToBytesLittleEndian(msg.getContentSN()));
		System.out.println("encode SN: "+ByteUtil.bytesToHex(ByteUtil.intToBytesLittleEndian(msg.getContentSN())));
//		out.writeBytes(ByteUtil.intTobytes());
		out.writeBytes(msg.getContent());
		System.out.println("encode Content: "+ByteUtil.bytesToHex(msg.getContent()));
		out.writeBytes(ByteUtil.shortToBytesLittleEndian(msg.getCbc_check()));
		System.out.println("encode CBC: "+ByteUtil.bytesToHex(ByteUtil.shortToBytesLittleEndian(msg.getCbc_check())));
		out.writeBytes(msg.getFOOTER());

	}

}
