package com.guan.netty.pio.disassemble;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 未处理TCP粘包和拆包问题的TimeClientHandler
 * NIO SocketChannel网络读写处理类
 * @author guanxc
 * @create 2017年2月20日下午2:23:00
 */
public class TimeClientDisassembleHandler extends ChannelHandlerAdapter {
	
	//记录客户端向服务器端查询请求次数
	private int counter;
	
	private byte[] req;
	
	
	public TimeClientDisassembleHandler() {
		req = ("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf message = null;
		for (int i = 0; i < 100; i++) {
			message = Unpooled.buffer(req.length);
			message.writeBytes(req);
			ctx.writeAndFlush(message);
		}
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		
		String body = new String(req,"UTF-8");
		System.out.println("Now is : "+ body + " ; the counter is : "+ ++counter);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println("Unexcepted exception from downstream : "+cause.getMessage());
		//释放资源
		ctx.close();
	}
}
