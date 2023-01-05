package com.lzb.rock.mqtt.handler;

import java.util.concurrent.TimeUnit;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Async
public class MqttService implements CommandLineRunner{

	@Autowired
	MqttHeartBeatBrokerHandler mqttHeartBeatBrokerHandler;

	@Override
	public void run(String... args) throws Exception {


		EventLoopGroup bossGroup = new NioEventLoopGroup(2);
		EventLoopGroup workerGroup = new NioEventLoopGroup(8);

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup);
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.channel(NioServerSocketChannel.class);
			b.childHandler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("encoder", MqttEncoder.INSTANCE);
					ch.pipeline().addLast("decoder", new MqttDecoder());
					ch.pipeline().addLast("heartBeatHandler", new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
					ch.pipeline().addLast("handler", mqttHeartBeatBrokerHandler);
				}
			});

			ChannelFuture f = b.bind(1883).sync();
			log.info("启动成功==>{}", 1883);
			f.channel().closeFuture().sync();

		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
		
	}

}
