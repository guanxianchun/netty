package com.guan.netty.decoder.delimiter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理TCP粘包和拆包问题的TimeClientHandler
 * NIO SocketChannel网络读写处理类
 * @author guanxc
 * @create 2017年2月20日下午2:23:00
 */
public class EchoClientHandler extends ChannelHandlerAdapter {
	
	//记录客户端向服务器端查询请求次数
	private int counter;
	
	private byte[] req;
	
	private final String ECHO_REQ = "Hi, Lilingfen. welcome to netty.$_";
	
	public EchoClientHandler() {

	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for (int i = 0; i < 100; i++) {
			ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
		}
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String body = (String) msg;
		System.out.println("This is : "+ ++counter + " times receive server : ["+ body +"]");
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println("Unexcepted exception from downstream : "+cause.getMessage());
		//释放资源
		ctx.close();
	}
}
