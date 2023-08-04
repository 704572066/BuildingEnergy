package com.liqiang.nettyTest2;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.liqiang.SimpeEcode.Message;
import com.liqiang.SimpeEcode.MessageHead;
import com.liqiang.utils.*;
import com.liqiang.xml.*;
import com.liqiang.xml.yaquan.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
//import lombok.Value;
import org.apache.kafka.common.protocol.types.Field;
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

import javax.xml.bind.JAXBException;
import java.util.Random;

@Component
public class ServerHandle extends ChannelInboundHandlerAdapter {

	private Server server;

	//随机序列
	private String randomSequence;

	//认证密钥
	private String authenticationKey = "12345";

	private String authenticationKey_yaquan = "IJKLMNOPQRSTUVWX";//雅全MD5认证密钥

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
		//解密xml包
		String xmlStr = AESUtil.decrypt(message.getContent());
//		System.out.println("解密消息中的xml包："+xmlStr);
//		System.out.println("消息的指令序列号："+message.getContentSN());
		//xml转bean
		Root root = XmlUtil.XMLToJavaBean(xmlStr, Root.class);
		String type = root.getCommon().getTypeList().get(0);
		System.out.println("xml包类型："+type);
		if(message.getContentSN()==2) {
			System.out.println("server接收到客户端发送的心跳包");
			//表示心跳包 服务端响应心跳包  而不做相关业务处理

			//解密心跳包
//			xmlStr = AESUtil.decrypt(message.getContent());
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

		}else if((message.getContentSN()==0||message.getContentSN()==256)&&type.equalsIgnoreCase("request")){
			System.out.println("server接收到客户端发送的身份验证请求: "+message.getContentSN());

			//解密身份验证请求包
//			xmlStr = AESUtil.decrypt(message.getContent());
			System.out.println(xmlStr);
			System.out.println(message.getContentSN());
			//xml转bean

			Request request = XmlUtil.XMLToJavaBean(xmlStr, Request.class);



			System.out.println("server向客户端发送一串随机序列");
//			randomSequence = MD5Util.getRandomSequence(16);
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
			if(message.getContentSN()==256) { //雅全设备采用CRC校验
				contentSN = 256;
				System.out.println("randomSequence: "+randomSequence);
//				String xml = AESUtil.decrypt(ByteUtil.hexToByteArray("8d80db2e2085cadb5fa68fdd664de4338908c707d9b808d23040df70ac3d0cde0a7e3c468f9ad43f3be4e26429452e88dc9a93a1dec06689972755356b5c33042d92d5a4bebb9786e2f62865373c3b1c09cdf416ffbe1097b4a76bc36423c40e7564c3c90c3ef2e323d429f959d8a55f49cb4e275edcda066e9cb630412877d30a5f310897f349c696a567c264cc25577be230c8bb1041f773418bef5077f2b6c7bad1af3e18e84bd615fac441f93a71989f6b92b54ed7dfc3987624547abc4d1bad2a920b93f242581b353ef67367d466bb4e68d1164aa5fd2ffcdc3e128a9ed8e3b15e3c9ea0d4c93166ee2b960b6b9694f7ddb7d9499cfc4a8cc865acd5b3a0c3c468731fc89b712058d80ff3880e884f40f3b833375dedc579ee9b6f8e80360b59e09e8da0c5b33223e46b010866"));
//				System.out.println("wenzhou "+xml);
//				String cbc_check = CRCUtil.GetCRC_XMODEM("00010000bcfd4378a5dfb425c45e3f9069fa0ae328752273149d7b897a769be1753dfd6c76f88a7852648e62a626b706d12b8d6255c70dc37de6465926d00e6d71522757a49d38a7df247767084106ee27bcaed63e625d7cd181f0f0cf39f06b56b868561eb51cddcd89ce93ae9fed43d61de3991140a7a33071041a07ff19097dac88219594fdec80e1a312fc720fcae5097527174ae4f8eefacbf3170b61df5cc5867b49f78b923c504f779f064faf23aaac7a0dab2c331b500ccaf05f48dd113c05e13f7e5d9035d08f49001b3ba278c2cd91c9889b213303b04bfe40edffff5c365b21398df36fedbcfbe111882585343d5e");
//				System.out.println("wenzhou cbc"+cbc_check);
				short crc_check = CRCUtil.GetCRC_XMODEM(ByteUtil.byteMerger(ByteUtil.intToBytesLittleEndian(contentSN), aesXmlBytes));
				Message sequenceMessage = new Message(contentLength, contentSN, aesXmlBytes, crc_check);
				ctx.writeAndFlush(sequenceMessage);

			}
			else {

				short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
//			short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesLittleEndian(contentSN), aesXmlBytes)));
//			short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intTobytes(), aesXmlBytes)));
				//随机序列消息构建
				Message sequenceMessage = new Message(contentLength, contentSN, aesXmlBytes, cbc_check);
//			HeartResult heartResult = new HeartResult()
				ctx.writeAndFlush(sequenceMessage);
			}


		}else if(message.getContentSN()==256&&type.equalsIgnoreCase("notify")) {
			System.out.println("server接收到雅全客户端发送的心跳包: ");
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
			int contentSN = 256;
//			short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
			short crc_check = CRCUtil.GetCRC_XMODEM(ByteUtil.byteMerger(ByteUtil.intToBytesLittleEndian(contentSN), aesXmlBytes));

			//心跳消息构建
			Message heartResultMessage = new Message(contentLength, contentSN, aesXmlBytes, crc_check);
//			HeartResult heartResult = new HeartResult()
			ctx.writeAndFlush(heartResultMessage);



		}else if(message.getContentSN()==1||message.getContentSN()==304){
			System.out.println("server接收到客户端发送的MD5值");
			xmlStr = AESUtil.decrypt(message.getContent());
			System.out.println("MD5: "+xmlStr);
			// xml转bean
//			System.out.println("==================================xml转bean============================");
			MD5 md5 = XmlUtil.XMLToJavaBean(xmlStr, MD5.class);
			String md5Str = null;
//			if(message.getContentSN()==304) {
//				md5Str = MD5Util.getMD5(randomSequence + authenticationKey_yaquan);
//			}
//			else{
//				md5Str = MD5Util.getMD5(randomSequence + authenticationKey);
//			}
			md5Str = MD5Util.getMD5(randomSequence + authenticationKey);
            String result = "fail";
			if(md5Str.equalsIgnoreCase(md5.getId_validate().getMd5())){
				result = "pass";
			}
			List<String> typeList = new ArrayList<>();
			typeList.add("result");
			ResultValidate id_validate = null;
			YaquanResultValidate yaquanResultValidate = null;
			if(message.getContentSN()==1) {
				typeList.add("time");
//				id_validate = new YaquanResultValidate("result", result);
				id_validate = new ResultValidate("result", result, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
				ResultTime resultTime = new ResultTime(new Common(md5.getCommon().getBuilding_id(), md5.getCommon().getGateway_id(), typeList), id_validate);
				//转换成xml字符串
				xmlStr = XmlUtil.beanToXml(resultTime, "UTF-8");

			}
			else {
				yaquanResultValidate = new YaquanResultValidate("result", result);
				YaquanResult yaquanResult = new YaquanResult(new Common(md5.getCommon().getBuilding_id(), md5.getCommon().getGateway_id(), typeList), yaquanResultValidate);
				//转换成xml字符串
				xmlStr = XmlUtil.beanToXml(yaquanResult, "UTF-8");
			}
//			ResultValidate id_validate = new ResultValidate("result", result, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//			ResultTime resultTime = new ResultTime(new Common(md5.getCommon().getBuilding_id(), md5.getCommon().getGateway_id(), typeList), id_validate);
//			//转换成xml字符串
//			xmlStr = XmlUtil.beanToXml(resultTime, "UTF-8");
			//AES加密
			byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
			//计算内容长度
			int contentLength = 4 + aesXmlBytes.length;
			//计算CBC校验
			int contentSN = 1;
			if(message.getContentSN()==304) { //雅全设备采用CRC校验
				contentSN = 304;
//				String xml = AESUtil.decrypt(ByteUtil.hexToByteArray("8d80db2e2085cadb5fa68fdd664de4338908c707d9b808d23040df70ac3d0cde0a7e3c468f9ad43f3be4e26429452e88dc9a93a1dec06689972755356b5c33042d92d5a4bebb9786e2f62865373c3b1c09cdf416ffbe1097b4a76bc36423c40e7564c3c90c3ef2e323d429f959d8a55f49cb4e275edcda066e9cb630412877d30a5f310897f349c696a567c264cc25577be230c8bb1041f773418bef5077f2b6c7bad1af3e18e84bd615fac441f93a71989f6b92b54ed7dfc3987624547abc4d1bad2a920b93f242581b353ef67367d466bb4e68d1164aa5fd2ffcdc3e128a9ed8e3b15e3c9ea0d4c93166ee2b960b6b9694f7ddb7d9499cfc4a8cc865acd5b3a0c3c468731fc89b712058d80ff3880e884f40f3b833375dedc579ee9b6f8e80360b59e09e8da0c5b33223e46b010866"));
//				System.out.println("wenzhou "+xml);
//				String cbc_check = CRCUtil.GetCRC_XMODEM("00010000bcfd4378a5dfb425c45e3f9069fa0ae328752273149d7b897a769be1753dfd6c76f88a7852648e62a626b706d12b8d6255c70dc37de6465926d00e6d71522757a49d38a7df247767084106ee27bcaed63e625d7cd181f0f0cf39f06b56b868561eb51cddcd89ce93ae9fed43d61de3991140a7a33071041a07ff19097dac88219594fdec80e1a312fc720fcae5097527174ae4f8eefacbf3170b61df5cc5867b49f78b923c504f779f064faf23aaac7a0dab2c331b500ccaf05f48dd113c05e13f7e5d9035d08f49001b3ba278c2cd91c9889b213303b04bfe40edffff5c365b21398df36fedbcfbe111882585343d5e");
//				System.out.println("wenzhou cbc"+cbc_check);
				short crc_check = CRCUtil.GetCRC_XMODEM(ByteUtil.byteMerger(ByteUtil.intToBytesLittleEndian(contentSN), aesXmlBytes));
				Message resultTimeMessage = new Message(contentLength, contentSN, aesXmlBytes, crc_check);
//			HeartResult heartResult = new HeartResult()
				ctx.writeAndFlush(resultTimeMessage);

			}
			else {
				short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
				//心跳消息构建
				Message resultTimeMessage = new Message(contentLength, contentSN, aesXmlBytes, cbc_check);
//			HeartResult heartResult = new HeartResult()
				ctx.writeAndFlush(resultTimeMessage);
			}


		}else if(message.getContentSN()==3){
//			xmlStr = AESUtil.decrypt(message.getContent());
			// xml转bean
//			System.out.println("==================================xml转bean============================");
			Report report = XmlUtil.XMLToJavaBean(xmlStr, Report.class);
//			System.out.println("收到验证结果: " + resultTime.getId_validate().getResult());
			System.out.println("server接收到客户端发送的监测数据:" + xmlStr);
			this.dealWithMessage(message);
		}
		else if(message.getContentSN()==-16){
			System.out.println("server接收到客户端发送的设备信息");
			//表示心跳包 服务端响应心跳包  而不做相关业务处理

			//解密设备信息
			xmlStr = AESUtil.decrypt(message.getContent());
			System.out.println(xmlStr);
			//xml转bean
//			Notify notify = XmlUtil.XMLToJavaBean(xmlStr, Notify.class);

			//发送心跳应答
			List<String> typeList = new ArrayList<>();
//			typeList.add("heart_result");
			typeList.add("device_ack");
//			HeartBeat heartBeat = new HeartBeat("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//			HeartResultValidate id_validate = new HeartResultValidate("heart_result", "0000");
			Device device = new Device("device_ack", "pass");
			DeviceACK deviceACK = new DeviceACK(new Common("330424E002", "01",typeList), device);
			//转换成xml字符串
			xmlStr = XmlUtil.beanToXml(deviceACK, "UTF-8");
			//AES加密
			byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
			//计算内容长度
			int contentLength = 4 + aesXmlBytes.length;
			//计算CBC校验
			int contentSN = -16;
//			short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
			short crc_check = CRCUtil.GetCRC_XMODEM(ByteUtil.byteMerger(ByteUtil.intToBytesLittleEndian(contentSN), aesXmlBytes));

			//心跳消息构建
			Message heartResultMessage = new Message(contentLength, contentSN, aesXmlBytes, crc_check);
//			HeartResult heartResult = new HeartResult()
			ctx.writeAndFlush(heartResultMessage);

			//.服务端主动发送 archive 信息，让设备进行初始化
			//发送心跳应答
			Thread.sleep(10000);
			typeList = new ArrayList<>();
//			typeList.add("heart_result");
			typeList.add("archives");
//			HeartBeat heartBeat = new HeartBeat("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//			HeartResultValidate id_validate = new HeartResultValidate("heart_result", "0000");
			Validate validate = new Validate("archives");
			Archives archives = new Archives(new Common("330424E002", "01",typeList), validate);
			//转换成xml字符串
			xmlStr = XmlUtil.beanToXml(archives, "UTF-8");
			//AES加密
			aesXmlBytes = AESUtil.encrypt(xmlStr);
			//计算内容长度
			contentLength = 4 + aesXmlBytes.length;
			//计算CBC校验
			contentSN = 5;
//			cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
			crc_check = CRCUtil.GetCRC_XMODEM(ByteUtil.byteMerger(ByteUtil.intToBytesLittleEndian(contentSN), aesXmlBytes));

			//心跳消息构建
			Message archivesMessage = new Message(contentLength, contentSN, aesXmlBytes, crc_check);
//			HeartResult heartResult = new HeartResult()
			ctx.writeAndFlush(archivesMessage);
		}
		else if(message.getContentSN()==0&&type.equalsIgnoreCase("report")){
//			System.out.println(xmlStr);
			Report report = XmlUtil.XMLToJavaBean(xmlStr, Report.class);
//			System.out.println("收到验证结果: " + resultTime.getId_validate().getResult());
			System.out.println("server接收到雅全客户端发送的监测数据:" + xmlStr);
			this.dealWithMessage(message);

		}
		else {
			System.out.println("server接收到客户端发送信息:" + msg.toString());
//			xmlStr = AESUtil.decrypt(message.getContent());
			System.out.println(xmlStr);
			System.out.println(message.getContentSN());
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

	private Message requestMessage() throws Exception {
		List<String> typeList = new ArrayList<>();
		typeList.add("request");
		Common common = new Common("330100A001", "01", typeList);
		Request request = new Request(common, new Validate());
//		Sequence sequence = new Sequence(new Common(request.getCommon().getBuilding_id(), request.getCommon().getGateway_id(), typeList), id_validate);
		//转换成xml字符串
		String xmlStr = XmlUtil.beanToXml(request, "UTF-8");
		//AES加密
		byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
		//计算内容长度
		int contentLength = 4 + aesXmlBytes.length;
		//计算CBC校验
		int contentSN = 256;
//		if(message.getContentSN()==256) {
////				Random random = new Random();
//			contentSN = 256;
//		}
		short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
//			short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesLittleEndian(contentSN), aesXmlBytes)));
//			short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intTobytes(), aesXmlBytes)));
		//随机序列消息构建
		Message requestMessage = new Message(contentLength, contentSN, aesXmlBytes, cbc_check);
		return requestMessage;
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
				System.out.println("flink_streaming_ali_kafka");
				System.out.println(content);

				//多线程写入kafka
				String key = "flink_streaming_ali_kafka";
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
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "101.37.32.216:9092");
		props.put(ProducerConfig.LINGER_MS_CONFIG,1);
//		props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG,16384);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG,33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return props;
	}

}
