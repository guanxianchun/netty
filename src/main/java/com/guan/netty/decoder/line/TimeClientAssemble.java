package com.guan.netty.decoder.line;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 处理TCP粘包和拆包问题的TimeClient
 * @author guanxc
 * @create 2017年2月20日下午3:17:21
 */
public class TimeClientAssemble {
	public void connect(int port , String host) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel channel) throws Exception {
					//解决TCP粘包和拆包问题
					channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
					channel.pipeline().addLast(new StringDecoder());
					
					channel.pipeline().addLast(new TimeClientAssembleHandler());
				};
			});
			
			//发起异步连接操作
			ChannelFuture future = bootstrap.connect(host, port).sync();
			
			//等待客户端链路关闭
			future.channel().closeFuture().sync();
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		int port =9000;
		if (args != null && args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		new TimeClientAssemble().connect(port, "127.0.0.1");
	}
}
