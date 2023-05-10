package com.liqiang.nettyTest2;

import com.liqiang.SimpeEcode.MessageDecode;
import com.liqiang.SimpeEcode.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	private Server server;
	public ServerChannelInitializer(Server server) {
		this.server=server;
	}
	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		// TODO Auto-generated method stub
		channel.pipeline()
		//7秒没收到客户端信息 则表示客户端因为网络等原因异常关闭 
	    .addLast("ping",new IdleStateHandler(7, 0, 0,TimeUnit.SECONDS))
		.addLast("decoder",new MessageDecode())
		.addLast("encoder",new MessageEncoder())
		.addLast(new ServerHandle(server));
	}

}
