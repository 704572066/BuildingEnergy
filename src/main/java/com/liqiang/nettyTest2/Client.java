package com.liqiang.nettyTest2;

import com.liqiang.SimpeEcode.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client implements Runnable{
	private String ip;// ip
	private int port;// 端口
	private boolean isConnection = false;
	private ChannelHandlerContext serverChannel;

	public Client(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	// 与服务器建立连接
	public void connection() {
		new Thread(this).start();
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		EventLoopGroup group = new NioEventLoopGroup();// 服务器监听服务器发送信息
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
				.handler(new ClientChannelInitializer(this));// 基于NIO编程模型通信
		try {
			ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();

			channelFuture.channel().closeFuture().sync();
			setConnection(true);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("连接服务器失败");
		}finally {
			//尝试重连
			System.out.println("正在重连");
			run();
		}	
	}

	public void close() {
		serverChannel.close();
	}
	public boolean isConnection() {
		return isConnection;
	}

	public void setConnection(boolean isConnection) {
		this.isConnection = isConnection;
	}

	public void sendMsg(Message msg) {
		if(isConnection) {
			serverChannel.writeAndFlush(msg);
		}
		
	}

	public ChannelHandlerContext getServerChannel() {
		return serverChannel;
	}

	public void setServerChannel(ChannelHandlerContext serverChannel) {
		this.serverChannel = serverChannel;
	}

}
