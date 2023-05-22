package com.liqiang.nettyTest2;

import com.liqiang.SimpeEcode.Message;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.Vector;

@Component
@ConfigurationProperties(prefix = "netty")
@Setter
public class Server implements Runnable {
//	@Value("${netty.port}")
	private int port;// 监听端口
	private Vector<ChannelHandlerContext> clients;// 保存在线客户端信息

	public Server() {
		clients = new Vector<ChannelHandlerContext>();
//		this.port = port;
	}

	// 广播
	public void sendAll(Message msg) {
		clients.forEach(c -> {
			c.writeAndFlush(msg);
		});
	}

	public void addClient(ChannelHandlerContext client) {
		clients.add(client);
	}

	public void removeClient(ChannelHandlerContext client) {
		if(clients.contains(client)) {
			clients.remove(client);
		}
	}

	@Override
	public void run() {
		/**
		 * NioEventLoopGroup 内部维护一个线程池 如果构造函数没有指定线程池数量 则默认为系统core*2
		 */
		EventLoopGroup acceptor = new NioEventLoopGroup();// acceptor负责监客户端连接请求
		EventLoopGroup worker = new NioEventLoopGroup();// worker负责io读写（监听注册channel的 read/writer事件）

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(acceptor, worker).channel(NioServerSocketChannel.class)
				.localAddress(new InetSocketAddress(port)).childHandler(new ServerChannelInitializer(this))
				.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
		try {
			ChannelFuture channelFuture = bootstrap.bind(port).sync();

			System.out.println("服务器已启动");
			// 将阻塞 直到服务器端关闭或者手动调用
			 channelFuture.channel().closeFuture().sync();
			// 释放资源
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
						acceptor.shutdownGracefully();
						worker.shutdownGracefully();
		}

	}
	@PostConstruct
	public void startServer() {
		new Thread(this).start();
	}

}
