package com.imooc.netty;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.imooc.enums.MsgActionEnum;
import com.imooc.netty.pojo.ChatMsg;
import com.imooc.netty.pojo.DataContent;
import com.imooc.netty.pojo.UserChannelRel;
import com.imooc.service.ChatMsgService;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.SpringContextUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.imooc.enums.MsgActionEnum.*;

/**
 * 
 * @Description: 处理消息的handler
 * TextWebSocketFrame： 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
@Slf4j
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	// 用于记录和管理所有客户端的channle
	public static ChannelGroup users =
			new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) 
			throws Exception {

		Channel currentChannel = ctx.channel();

		// 1.获取客户端传输过来的消息
		String content = msg.text();
		DataContent dataContent = JsonUtils.jsonToPojo(content,DataContent.class);
		//2.判断消息类型、处理不同业务
		MsgActionEnum msgActionEnum = getInstance(dataContent.getAction());
		switch (msgActionEnum){
			case CONNECT:
				//2.1 当websocket第一次open的时候，初始化channel，将channel和用户管理起来
				String sendId = dataContent.getChatMsg().getSendId();
				UserChannelRel.put(sendId,currentChannel);

				//日志输出
				users.stream().forEach(channel -> log.info(channel.id().asLongText()));
				UserChannelRel.print();

				break;
			case CHAT:
				//2.2 聊天消息 存储到数据库 标记消息的签收状态
				ChatMsg chatMsg = dataContent.getChatMsg();
				String msgText = chatMsg.getMsg();
				String receiverId = chatMsg.getReceiverId();
				String sendId1 = chatMsg.getSendId();

				//保存消息到数据库，标记消息未签收
				ChatMsgService chatMsgService = (ChatMsgService) SpringContextUtils.getBean("chatMsgServiceImpl");
				String msgId = chatMsgService.save(chatMsg);

				chatMsg.setMsgId(msgId);

				DataContent resultDataContent = new DataContent();
				resultDataContent.setChatMsg(chatMsg);

				//发送消息到接收者
				//获取接收方channel
				Channel receiveChannel = UserChannelRel.get(receiverId);
				if(receiveChannel == null){
					//TODO 用户离线  推送消息 JPUSH 小米推送
				}else{
					Channel findChannel = users.find(receiveChannel.id());
					if(findChannel != null){
						//用户在线
						receiveChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(resultDataContent)));
					}else{
						//用户离线 TODO 推送
					}
				}
				break;
			case SIGNED:
				//2.3 签收消息，针对具体消息，进行签收
				chatMsgService = (ChatMsgService)SpringContextUtils.getBean("chatMsgServiceImpl");
				//扩展字段，在signed类型的消息中，代表要去签收的消息id,逗号间隔
				String[] msgIds = dataContent.getExtend().split(",");
				List<String> msgIdList = Arrays.stream(msgIds).filter((msgIdStr) -> StringUtils.isNotBlank(msgIdStr))
						.collect(Collectors.toList());
				log.info(msgIdList.toString());
				if(msgIdList != null && msgIdList.size() > 0){
					chatMsgService.updateMsgSigned(msgIdList);
				}
				break;
			case KEEPALIVE:
				//2.4心跳类型的消息
				log.info(currentChannel.id() + "心跳发送");
				break;
			default:
				throw new IllegalStateException("Unexpected msg type: " + dataContent.getAction());
		}

//		System.out.println("接受到的数据：" + content);
		
//		for (Channel channel: clients) {
//			channel.writeAndFlush(
//				new TextWebSocketFrame(
//						"[服务器在]" + LocalDateTime.now() 
//						+ "接受到消息, 消息为：" + content));
//		}
		// 下面这个方法，和上面的for循环，一致
//		users.writeAndFlush(
//				new TextWebSocketFrame(
//						"[服务器在]" + LocalDateTime.now()
//						+ "接受到消息, 消息为：" + content));
		
	}

	/**
	 * 当客户端连接服务端之后（打开连接）
	 * 获取客户端的channle，并且放到ChannelGroup中去进行管理
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		users.add(ctx.channel());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
		String channelId = ctx.channel().id().asShortText();
		users.remove(ctx.channel());
//		System.out.println("客户端断开，channle对应的长id为："
//							+ ctx.channel().id().asLongText());
//		System.out.println("客户端断开，channle对应的短id为："
//							+ ctx.channel().id().asShortText());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		//发生异常、关闭channle,并移除
		ctx.channel().close();
		users.remove(ctx.channel());
	}
}
