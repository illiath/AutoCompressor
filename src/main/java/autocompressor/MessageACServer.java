package autocompressor;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageACServer implements IMessage, IMessageHandler<MessageACServer, IMessage>{

	@Override
	public IMessage onMessage(MessageACServer message, MessageContext ctx) {
		// TODO Auto-generated method stub
		DebugOut.debugMessage("MessageACServer:onMessage", "Hey, we got a message!");
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		DebugOut.debugMessage("MessageACServer:fromBytes", "Hey, we got a message!");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		DebugOut.debugMessage("MessageACServer:toBytes", "Hey, we got a message!");
		// TODO Auto-generated method stub
		
	}

}
