package com.liqiang.nettyTest2;

import com.liqiang.SimpeEcode.Message;
import com.liqiang.SimpeEcode.MessageHead;
import com.liqiang.utils.AESUtil;
import com.liqiang.utils.ByteUtil;
import com.liqiang.utils.CBCUtil;
import com.liqiang.utils.XmlUtil;
import com.liqiang.xml.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerHandle extends ChannelInboundHandlerAdapter {

	private Server server;

	public ServerHandle(Server server) {
		// TODO Auto-generated constructor stub
		this.server = server;
	}
	/**
	 * 读写超时事事件
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent event=(IdleStateEvent)evt;
			//如果读超时
			if(event.state()==IdleState.READER_IDLE) {
			        System.out.println("有客户端超时了");
			        ctx.channel().close();//关闭连接
			}
		}else {
			super.userEventTriggered(ctx, evt);
		}
		
	}

	// 建立连接时回调
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("有客户端建立连接了");
		server.addClient(ctx);
		// ctx.fireChannelActive();//pipeline可以注册多个handle 这里可以理解为是否通知下一个Handle继续处理
	}

	// 接收到客户端发送消息时回调
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Message message=(Message)msg;
		if(message.getContentSN()==0) {
			System.out.println("server接收到客户端发送的心跳包");
			//表示心跳包 服务端响应心跳包  而不做相关业务处理
//			MessageHead head=new MessageHead();
//			byte[] content="".getBytes();
//			head.setCreateDate(new Date());
//			head.setType("ping");
//			head.setLength(content.length);
//			Message pingMessage=new Message(head,content);
//			head.setToken(pingMessage.buidToken());
// 			ctx.writeAndFlush(pingMessage);
			List<String> typeList = new ArrayList<>();
			typeList.add("heart_result");
			HeartResultValidate id_validate = new HeartResultValidate("heart_result", "0000");
			HeartResult heartResult = new HeartResult(new Common("330400A001", "01", typeList), id_validate);
			//转换成xml字符串
			String xmlStr = XmlUtil.beanToXml(heartResult, "UTF-8");
			//AES加密
			byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
			//计算内容长度
			int contentLength = 4 + aesXmlBytes.length;
			//计算CBC校验
			int contentSN = 0;
			short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
			//心跳消息构建
			Message heartResultMessage = new Message(contentLength, 0, aesXmlBytes, cbc_check);
//			HeartResult heartResult = new HeartResult()
			ctx.writeAndFlush(heartResultMessage);

		}else if(message.getContentSN()==1){
			System.out.println("server接收到客户端发送的身份验证请求");
			System.out.println("server向客户端发送一串随机序列");
			List<String> typeList = new ArrayList<>();
			typeList.add("sequence");
			SequenceValidate id_validate = new SequenceValidate("sequence", "1234abcd");
			Sequence sequence = new Sequence(new Common("330400A001", "01", typeList), id_validate);
			//转换成xml字符串
			String xmlStr = XmlUtil.beanToXml(sequence, "UTF-8");
			//AES加密
			byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
			//计算内容长度
			int contentLength = 4 + aesXmlBytes.length;
			//计算CBC校验
			int contentSN = 1;
			short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
			//心跳消息构建
			Message sequenceMessage = new Message(contentLength, contentSN, aesXmlBytes, cbc_check);
//			HeartResult heartResult = new HeartResult()
			ctx.writeAndFlush(sequenceMessage);

		}else if(message.getContentSN()==2){
			System.out.println("server接收到客户端发送的MD5值");
			List<String> typeList = new ArrayList<>();
			typeList.add("result");
			typeList.add("time");
			ResultValidate id_validate = new ResultValidate("result", "pass", "20230509152030");
			ResultTime resultTime = new ResultTime(new Common("330400A001", "01", typeList), id_validate);
			//转换成xml字符串
			String xmlStr = XmlUtil.beanToXml(resultTime, "UTF-8");
			//AES加密
			byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
			//计算内容长度
			int contentLength = 4 + aesXmlBytes.length;
			//计算CBC校验
			int contentSN = 2;
			short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
			//心跳消息构建
			Message resultTimeMessage = new Message(contentLength, contentSN, aesXmlBytes, cbc_check);
//			HeartResult heartResult = new HeartResult()
			ctx.writeAndFlush(resultTimeMessage);


		}else if(message.getContentSN()==3){
			String xmlStr = AESUtil.decrypt(message.getContent());
			// xml转bean
//			System.out.println("==================================xml转bean============================");
			Report report = XmlUtil.XMLToJavaBean(xmlStr, Report.class);
//			System.out.println("收到验证结果: " + resultTime.getId_validate().getResult());
			System.out.println("server接收到客户端发送的监测数据:" + xmlStr);
		}else {
			System.out.println("server接收到客户端发送信息:" + msg.toString());
		}
		// TODO Auto-generated method stub
		
		// ctx.fireChannelRead(msg);pipeline可以注册多个handle 这里可以理解为是否通知下一个Handle继续处理
	}

	// 通信过程中发生异常回调
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		// super.exceptionCaught(ctx, cause);
		ctx.close();// 发生异常关闭通信通道
	    System.out.println("发生异常与客户端失去连接");
	   
	    cause.printStackTrace();
		// ctx.fireExceptionCaught(cause);pipeline可以注册多个handle 这里可以理解为是否通知下一个Handle继续处理
	}
}
