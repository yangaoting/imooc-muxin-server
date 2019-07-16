package com.imooc.netty;

import com.imooc.enums.MsgActionEnum;
import com.imooc.netty.pojo.ChatMsg;
import com.imooc.netty.pojo.DataContent;
import com.imooc.netty.pojo.UserChannelRel;
import com.imooc.service.ChatMsgService;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.SpringContextUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.imooc.enums.MsgActionEnum.getInstance;

/**
 * 继承ChannelInboundHandlerAdapter，不需要实现ChannelRead0方法
 * 用于监测channel的心跳
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            //判断evt是否是IdleStateEvent，触发用户事件
            IdleStateEvent event = (IdleStateEvent) evt;

            if(event.state() == IdleState.READER_IDLE){
                log.info("读空闲");
            }else if(event.state() == IdleState.WRITER_IDLE){
                log.info("写空闲");
            }else if(event.state() == IdleState.ALL_IDLE){
                //读写空闲(类似飞行模式),关闭channel
                log.info("channel关闭后channel数量：" + ChatHandler.users.size());
                Channel channel = ctx.channel();
                channel.close();
                log.info("channel关闭后channel数量：" + ChatHandler.users.size());
            }
        }
    }
}
