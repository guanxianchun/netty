package com.guan.netty.decoder.delimiter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
/**
 * 处理TCP粘包和拆包问题的TimeServer
 * @author guanxc
 * @create 2017年2月20日下午3:19:26
 */
public class EchoServer {
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
			//分隔符与定长解码
			ByteBuf delimter = Unpooled.copiedBuffer("$_".getBytes());
			channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimter));
			channel.pipeline().addLast(new StringDecoder());
			
			channel.pipeline().addLast(new EchoServerHandler());
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
		new EchoServer().bind(port);
	}
}
