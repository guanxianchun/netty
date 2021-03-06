package com.guan.netty.pio.simple;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {
	
	public void bind(int port) throws Exception{
		//配置服务端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();     //专门用于处理服务端接受客户端的连接
		EventLoopGroup workerGroup = new NioEventLoopGroup();   //专门用于网络读写
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();   //创建bootstrap对象，它是netty NIO服务端的辅助启动类
			bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)           //设置创建的Channel为NioServerSocketChannel
				.option(ChannelOption.SO_BACKLOG, 1024)          //配置NioServerSocketChannel的TCP参数
				.childHandler(new ChildChannelHandler());        //绑定IO事件处理类
			//绑定端口，同步等待成功
			ChannelFuture future = bootstrap.bind(port).sync();
			
			//等待服务端监听端口关闭
			future.channel().closeFuture().sync();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//优雅退出  释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		
	}
	
	class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel channel) throws Exception {
			channel.pipeline().addLast(new TimeServerHandler());
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		int port = 9000;
		if (args != null && args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		new TimeServer().bind(port);
	}
}
