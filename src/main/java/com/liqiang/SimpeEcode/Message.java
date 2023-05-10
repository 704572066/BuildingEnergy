package com.liqiang.SimpeEcode;

import com.liqiang.nettyTest2.Md5Utils;
import io.netty.util.internal.StringUtil;

import java.text.SimpleDateFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 自定义协议 数据包格式
 * -----------------------------------
 * | 协议开始标志 | 包长度|消息类型(定长50个字节)|令牌 (定长50个字节)|令牌生成时间(定长50个字节)| 包内容 |   
 * -----------------------------------
 * 令牌生成规则
 *  协议开始标志 +包长度+消息类型+令牌生成时间+包内容+服务器与客户端约定的秘钥
 * @author Administrator
 *
 */
@Getter
@Setter
public class Message {
	public Message(int contentLength, int contentSN, byte[] content, short cbc_check) {
		this.contentSN = contentSN;
		this.content = content;
		this.contentLength = contentLength;
		this.cbc_check = cbc_check;
	}
	// 协议头
//	private MessageHead head;

	//消息头
	private final byte[] HEADER = { 0x68, 0x68, 0x16, 0x16 };

	//内容长度
	private int contentLength;

	//指令序号
	private int contentSN;

	//指令内容
	private byte[] content;

	public short getCbc_check() {
		return cbc_check;
	}

	//CBC校验
	private short cbc_check;

	//消息尾
	private final byte[] FOOTER = { 0x55, (byte) 0xAA, 0x55, (byte) 0xAA };

//
//	public MessageHead getHead() {
//		return head;
//	}
//
//	public void setHead(MessageHead head) {
//		this.head = head;
//	}
//
//	public byte[] getContent() {
//		return content;
//	}
//
//	public void setContent(byte[] content) {
//		this.content = content;
//	}
//	@Override
//	public String toString() {
//		// TODO Auto-generated method stub
//		return "[head:"+head.toString()+"]"+"content:"+new String(content);
//	}
//
//	public String buidToken() {
//		//生成token
//		SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String time = format0.format(this.getHead().getCreateDate());// 这个就是把时间戳经过处理得到期望格式的时间
//		String allData=String.valueOf(this.getHead().getHeadData());
//		allData+=String.valueOf(this.getHead().getLength());
//		allData+=StringUtil.isNullOrEmpty(this.getHead().getType())?"":this.getHead().getType();
//		allData+=time;
//		allData+=new String(this.getContent());
//		allData+="11111";//秘钥
//		return Md5Utils.stringMD5(allData);
//
//	}
//
//	public boolean authorization(String token) {
//		//表示参数被修改
//		if(!token.equals(this.getHead().getToken())) {
//			return false;
//		}
//		//验证是否失效
//		Long s = (System.currentTimeMillis() - getHead().getCreateDate().getTime()) / (1000 * 60);
//		if(s>60) {
//			return false;
//		}
//		return true;
//	}

}
