package com.liqiang.SimpeEcode;

import com.liqiang.utils.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessageDecode extends ByteToMessageDecoder{

	private final int BASE_LENGTH=4+4+4+2+4;//协议头 类型 int+length 4个字节+消息类型加令牌和 令牌生成时间50个字节
	private int headData=0X76;//协议开始标志

    private final byte[] HEADER = { 0x68, 0x68, 0x16, 0x16 };

	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
		// 刻度长度必须大于基本长度
		if(buffer.readableBytes()>=BASE_LENGTH) {
			/**
			 * 粘包 发送频繁 可能多次发送黏在一起 需要考虑  不过一个客户端发送太频繁也可以推断是否是攻击
			 */
			//防止soket流攻击。客户端传过来的数据太大不合理
			if(buffer.readableBytes()>2048) {
				//buffer.skipBytes(buffer.readableBytes());
				
			}
		}
		int beginIndex;//记录包开始位置
		while(true) {
			  // 获取包头开始的index  
			beginIndex = buffer.readerIndex();  
			//如果读到开始标记位置 结束读取避免拆包和粘包
            int head = buffer.readInt();
//            int little_head = ByteUtil.bytesToIntLittleEndian(HEADER);
            int big_head  = ByteUtil.bytesToIntBigEndian(HEADER);
			if(head == big_head) {
				break;
			}
			 
			//初始化读的index为0
            buffer.resetReaderIndex();  
            // 当略过，一个字节之后，  
            //如果当前buffer数据小于基础数据 返回等待下一次读取
            if (buffer.readableBytes() < BASE_LENGTH) {  
                return;  
            }  
		}
//        buffer.resetReaderIndex();
        int readableLength  = buffer.readableBytes();
//        byte[] re  = new byte[readableLength];
//        buffer.readBytes(re);
//        System.out.println(buffer.toString());
        //读取消息长度
        byte[] lengthBytes = new byte[4];
        buffer.readBytes(lengthBytes);
//        int length = buffer.readInt();
        int length = ByteUtil.bytesToIntLittleEndian(lengthBytes);
        int re  = buffer.readableBytes();
        // 判断请求数据包数据是否到齐   -150是消息头的长度。
        if ((buffer.readableBytes()-6) < length) {
            //没有到齐 返回读的指针 等待下一次数据到期再读
            buffer.readerIndex(beginIndex);  
            return;  
        }  
//        //读取消息类型
//        byte[] typeByte=new byte[50];
//        buffer.readBytes(typeByte);
//        //读取令牌
//        byte[] tokenByte=new byte[50];
//        buffer.readBytes(tokenByte);
//
//        //读取令牌生成时间
//        byte[]createDateByte=new byte[50];
//        buffer.readBytes(createDateByte);
        //读取指令序号contentSN
//        int contentSN = buffer.readInt();
        byte[] contentSNBytes = new byte[4];
        buffer.readBytes(contentSNBytes);
        int contentSN = ByteUtil.bytesToIntLittleEndian(contentSNBytes);
        //读取content
        byte[] content = new byte[length-4];
        buffer.readBytes(content);
        //读取CBC校验
        byte[] cbc_CheckBytes = new byte[2];
        buffer.readBytes(cbc_CheckBytes);
        short cbc_check = ByteUtil.bytesToShortLittleEndian(cbc_CheckBytes);
        //读取footer
        buffer.readInt();
//        MessageHead head=new MessageHead();
//        head.setHeadData(headData);
//        head.setToken(new String(tokenByte).trim());
//        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        head.setCreateDate(  simpleDateFormat.parse(new String(createDateByte).trim()));
//        head.setLength(length);
//        head.setType(new String(typeByte).trim());
//        Message message=new Message(head, data);
//        //认证不通过
//        if(!message.authorization(message.buidToken())) {
//        	ctx.close();
//
//        	return;
//        }
        Message message = new Message(length, contentSN, content, cbc_check);
        out.add(message);
        buffer.discardReadBytes();//回收已读字节
	}
	

}
