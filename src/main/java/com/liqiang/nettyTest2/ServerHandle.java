package com.liqiang.nettyTest2;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.liqiang.SimpeEcode.Message;
import com.liqiang.SimpeEcode.MessageHead;
import com.liqiang.utils.*;
import com.liqiang.xml.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
//import lombok.Value;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.kafka.clients.producer.ProducerConfig;

@Component
public class ServerHandle extends ChannelInboundHandlerAdapter {

	private Server server;

	//随机序列
	private String randomSequence;

	//认证密钥
	private String authenticationKey = "12345";

	@Autowired
//	@Qualifier("testKafkaTemplate")
	private KafkaTemplate<String, String> kafkaTemplate;

//	@Value("${flink_streaming}")
//	private String topic1;

//	@Autowired
	ThreadPoolExecutor threadPoolExecutor;

	public ServerHandle(Server server) {
		// TODO Auto-generated constructor stub
		this.kafkaTemplate = new KafkaTemplate<>(producerFactory());
		this.server = server;
		this.randomSequence = MD5Util.getRandomSequence(16);
		this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
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
		if(message.getContentSN()==2) {
			System.out.println("server接收到客户端发送的心跳包");
			//表示心跳包 服务端响应心跳包  而不做相关业务处理

			//解密心跳包
			String xmlStr = AESUtil.decrypt(message.getContent());
			System.out.println(xmlStr);
			//xml转bean
			Notify notify = XmlUtil.XMLToJavaBean(xmlStr, Notify.class);

            //发送心跳应答
			List<String> typeList = new ArrayList<>();
//			typeList.add("heart_result");
			typeList.add("time");
			HeartBeat heartBeat = new HeartBeat("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//			HeartResultValidate id_validate = new HeartResultValidate("heart_result", "0000");
			TimeValidate id_validate = new TimeValidate("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
			HeartResult heartResult = new HeartResult(new Common(notify.getCommon().getBuilding_id(), notify.getCommon().getGateway_id(), typeList), heartBeat);
			//转换成xml字符串
			xmlStr = XmlUtil.beanToXml(heartResult, "UTF-8");
			//AES加密
			byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
			//计算内容长度
			int contentLength = 4 + aesXmlBytes.length;
			//计算CBC校验
			int contentSN = 2;
			short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
			//心跳消息构建
			Message heartResultMessage = new Message(contentLength, contentSN, aesXmlBytes, cbc_check);
//			HeartResult heartResult = new HeartResult()
			ctx.writeAndFlush(heartResultMessage);

		}else if(message.getContentSN()==0){
			System.out.println("server接收到客户端发送的身份验证请求");

			//解密身份验证请求包
			String xmlStr = AESUtil.decrypt(message.getContent());
			System.out.println(xmlStr);
			//xml转bean
			Request request = XmlUtil.XMLToJavaBean(xmlStr, Request.class);


			System.out.println("server向客户端发送一串随机序列");
			randomSequence = MD5Util.getRandomSequence(16);
			List<String> typeList = new ArrayList<>();
			typeList.add("sequence");
			SequenceValidate id_validate = new SequenceValidate("sequence", randomSequence);
			Sequence sequence = new Sequence(new Common(request.getCommon().getBuilding_id(), request.getCommon().getGateway_id(), typeList), id_validate);
			//转换成xml字符串
			xmlStr = XmlUtil.beanToXml(sequence, "UTF-8");
			//AES加密
			byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
			//计算内容长度
			int contentLength = 4 + aesXmlBytes.length;
			//计算CBC校验
			int contentSN = 0;
			short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
			//随机序列消息构建
			Message sequenceMessage = new Message(contentLength, contentSN, aesXmlBytes, cbc_check);
//			HeartResult heartResult = new HeartResult()
			ctx.writeAndFlush(sequenceMessage);

		}else if(message.getContentSN()==1){
			System.out.println("server接收到客户端发送的MD5值");

			String xmlStr = AESUtil.decrypt(message.getContent());
			// xml转bean
//			System.out.println("==================================xml转bean============================");
			MD5 md5 = XmlUtil.XMLToJavaBean(xmlStr, MD5.class);
			String md5Str = MD5Util.getMD5(randomSequence+authenticationKey);
            String result = "fail";
			if(md5Str.equals(md5.getId_validate().getMd5())){
				result = "pass";
			}
			List<String> typeList = new ArrayList<>();
			typeList.add("result");
			typeList.add("time");
			ResultValidate id_validate = new ResultValidate("result", result, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
			ResultTime resultTime = new ResultTime(new Common(md5.getCommon().getBuilding_id(), md5.getCommon().getGateway_id(), typeList), id_validate);
			//转换成xml字符串
			xmlStr = XmlUtil.beanToXml(resultTime, "UTF-8");
			//AES加密
			byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
			//计算内容长度
			int contentLength = 4 + aesXmlBytes.length;
			//计算CBC校验
			int contentSN = 1;
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
			this.dealWithMessage(message);
		}else {
			System.out.println("server接收到客户端发送信息:" + msg.toString());
			String xmlStr = AESUtil.decrypt(message.getContent());
			System.out.println(xmlStr);
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
		server.removeClient(ctx); // 移除客户端
	    System.out.println("发生异常与客户端失去连接");

	   
	    cause.printStackTrace();
		// ctx.fireExceptionCaught(cause);pipeline可以注册多个handle 这里可以理解为是否通知下一个Handle继续处理
	}


	private void writeToKafka(String key, String content) {
//		if (key.equals(topic1)) {
			kafkaTemplate.send(key, content);
//		}
	}

	/**
	 * 处理消息
	 * @throws Exception
	 */
	private void dealWithMessage(Message message) throws Exception {

		threadPoolExecutor.submit(()->{

			try {
				String xmlStr = AESUtil.decrypt(message.getContent());
				// xml转bean
//			System.out.println("==================================xml转bean============================");
				Report report = XmlUtil.XMLToJavaBean(xmlStr, Report.class);

				String[] excludeProperties = {"typeList"};

				PropertyPreFilters filters = new PropertyPreFilters();
				PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
				excludefilter.addExcludes(excludeProperties);

				String content = JSONObject.toJSONString(report, excludefilter);
//			System.out.println("收到验证结果: " + resultTime.getId_validate().getResult());
				System.out.println("flink_streaming");

				//多线程写入kafka
				String key = "flink_streaming";
//				String content = "{\"user_id\":\""+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+"\",\"name\":\"zhangsan\",\"sex\":\"nv\",\"money\":\"499\"}";

//				System.out.println("key={},content={}", key, content);

				this.writeToKafka(key, content);
			}
			catch (Exception e){

			}

			//写入成功
//			ByteBufAllocator alloc = ctx.alloc();
//			ByteBuf buffer = alloc.buffer();
//			buffer.writeBytes("STORED\r\n".getBytes());
//			ctx.writeAndFlush(buffer);

		});
	}

	private ProducerFactory<String, String> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	private Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.LINGER_MS_CONFIG,1);
//		props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG,16384);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG,33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return props;
	}

}
