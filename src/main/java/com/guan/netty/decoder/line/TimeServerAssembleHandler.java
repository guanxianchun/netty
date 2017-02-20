package com.guan.netty.decoder.line;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
/**
 * 处理TCP粘包和拆包问题的TimeServerHandler
 * NIO SocketChannel网络读写处理类
 * @author guanxc
 * @create 2017年2月20日下午2:23:00
 */
public class TimeServerAssembleHandler extends ChannelHandlerAdapter {
	//记录接受客户端查询请求次数
	private int counter;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		String body = (String) msg;
		System.out.println("The time server receive order : "+ body +" ; the counter is : "+ ++counter);
		String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?new Date(System.currentTimeMillis()).toString():"BAD ORDER";
		currentTime = currentTime + System.getProperty("line.separator");
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.writeAndFlush(resp);
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("Unexcepted exception from downstream : "+cause.getMessage());
		//释放资源
		ctx.close();
	}
}
