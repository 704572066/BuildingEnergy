package com.liqiang.nettyTest2;

import com.liqiang.SimpeEcode.Message;
import com.liqiang.SimpeEcode.MessageHead;
import com.liqiang.task.ReportDataTask;
import com.liqiang.utils.*;
import com.liqiang.xml.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClientHandle extends ChannelInboundHandlerAdapter {

	Timer timer = new HashedWheelTimer();

	private static final long INTERVAL_SECONDS = 5; // 定时任务触发间隔

	private String authenticationKey = "12345";

	Client client;

	public  ClientHandle(Client client) {
		// TODO Auto-generated constructor stub
       this.client=client;
    }
    /**
	 * 读写超时事事件
     * @throws Exception 
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent idleStateEvent=((IdleStateEvent) evt);
			/**
			 * 如果没有收到服务端的写 则表示服务器超时 判断是否断开连接
			 */
	        if(idleStateEvent.state()==IdleState.READER_IDLE) {
	        	System.out.println("服务器无响应");
	        	if(!ctx.channel().isOpen()) {
	        		System.out.println("正在重连");
	        		client.connection();
	        		System.out.println("重连成功");
	        	}
	        }else if(idleStateEvent.state()==IdleState.WRITER_IDLE) {
	        	//如果没有触发写事件则向服务器发送一次心跳包
	        	System.out.println("正在向服务端发送心跳包");

//	        	MessageHead head=new MessageHead();
//	        	byte[]content="".getBytes();
//				head.setCreateDate(new Date());
//				head.setType("ping");
//				head.setLength(content.length);
//				Message pingMessage=new Message(head,content);
//				head.setToken(pingMessage.buidToken());
				//心跳指令内容构建
				List<String> typeList = new ArrayList<>();
				typeList.add("notify");
				Notify notify = new Notify(new Common("330400A001", "01", typeList));
				//转换成xml字符串
				String xmlStr = XmlUtil.beanToXml(notify, "UTF-8");
				//AES加密
				byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
				//计算内容长度
				int contentLength = 4 + aesXmlBytes.length;
				//计算CBC校验
				int contentSN = 2;
				short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
                //心跳消息构建
				Message heartBeatMessage = new Message(contentLength, 2, aesXmlBytes, cbc_check);
	 			ctx.writeAndFlush(heartBeatMessage);
	        }
		}else {
			super.userEventTriggered(ctx, evt);
		}
	}
	//建立连接时回调
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("与服务器建立连接成功");
		client.setServerChannel(ctx);
		client.setConnection(true);

		//请求身份验证
		//身份验证指令内容构建
		List<String> typeList = new ArrayList<>();
		typeList.add("request");
		Request request = new Request(new Common("330400A001", "01", typeList), new Validate("request"));
		//转换成xml字符串
		String xmlStr = XmlUtil.beanToXml(request, "UTF-8");
		//AES加密
		byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
		//计算内容长度
		int contentLength = 4 + aesXmlBytes.length;
		//计算CBC校验
		int contentSN = 1;
		short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
		//心跳消息构建
		Message requestMessage = new Message(contentLength, 1, aesXmlBytes, cbc_check);
		ctx.writeAndFlush(requestMessage);

		//ctx.fireChannelActive();//如果注册多个handle 下一个handel的事件需要触发需要调用这个方法
		
	}
	//读取服务器发送信息时回调
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Message message=(Message) msg;
		if(message.getContentSN()==2) {
			//表示是心跳包 不做任何业务处理
			System.out.println("服务器有响应");
		}else if(message.getContentSN()==1) {
			System.out.println("收到随机串");
			String xmlStr = AESUtil.decrypt(message.getContent());
			// xml转bean
			System.out.println("==================================xml转bean============================");
			Sequence sequence = XmlUtil.XMLToJavaBean(xmlStr,Sequence.class);
//			System.out.println(sequence);
			//计算MD5值发送给服务端
//            String authenticationKey = "12345";
            String md5Str = MD5Util.getMD5(authenticationKey + sequence.getId_validate().getSequence());
			List<String> typeList = new ArrayList<>();
			typeList.add("md5");
			MD5 md5 = new MD5(new Common("330400A001", "01", typeList), new MD5Validate("md5", md5Str));
			//转换成xml字符串
			xmlStr = XmlUtil.beanToXml(md5, "UTF-8");
			//AES加密
			byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
			//计算内容长度
			int contentLength = 4 + aesXmlBytes.length;
			//计算CBC校验
			int contentSN = 2;
			short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
			//心跳消息构建
			Message md5Message = new Message(contentLength, contentSN, aesXmlBytes, cbc_check);
			ctx.writeAndFlush(md5Message);
		}else if(message.getContentSN()==2) {
			String xmlStr = AESUtil.decrypt(message.getContent());
			// xml转bean
//			System.out.println("==================================xml转bean============================");
			ResultTime resultTime = XmlUtil.XMLToJavaBean(xmlStr,ResultTime.class);
			System.out.println("收到验证结果: " + resultTime.getId_validate().getResult());
			// 定时任务，每隔一段时间发送消息
			timer.newTimeout(new ReportDataTask(timer, client.getServerChannel().channel()), INTERVAL_SECONDS, TimeUnit.SECONDS);


		}else {
			// TODO Auto-generated method stub
			System.out.println(msg.toString());
		}
	
	}

	//发生异常时回调
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
	    System.out.println("发生异常 与服务器断开连接");
	    cause.printStackTrace();
		ctx.close();//关闭连接
	}
}
