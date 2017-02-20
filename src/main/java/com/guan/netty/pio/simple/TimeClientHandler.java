package com.guan.netty.pio.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/**
 * NIO SocketChannel网络读写处理类
 * @author guanxc
 * @create 2017年2月20日下午2:23:00
 */
public class TimeClientHandler extends ChannelHandlerAdapter {
	
	private final ByteBuf firstMessage;
	
	public TimeClientHandler() {
		byte[] req = "QUERY TIME ORDER".getBytes();
		firstMessage = Unpooled.buffer(req.length);
		firstMessage.writeBytes(req);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(firstMessage);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req,"UTF-8");
		System.out.println("Now is : "+ body);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println("Unexcepted exception from downstream : "+cause.getMessage());
		//释放资源
		ctx.close();
	}
}
