package autocompressor;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageACGUIButton4 implements IMessage {
	public int	x;
	public int	y;
	public int	z;

	public MessageACGUIButton4() {
	}

	public MessageACGUIButton4(TileEntityAutoCompressor tileEntityAC) {
		this.x = tileEntityAC.xCoord;
		this.y = tileEntityAC.yCoord;
		this.z = tileEntityAC.zCoord;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		DebugOut.debugMessage("MessageACClient:fromBytes", "Decoding Message");
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		DebugOut.debugMessage("MessageACClient:toBytes", "Encoding Message");
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
	}

	public static class Handler implements IMessageHandler<MessageACGUIButton4, IMessage> {
		/**
		 * This gets called when the packet is read and received.
		 */
		@Override
		public IMessage onMessage(MessageACGUIButton4 message, MessageContext ctx) {
			DebugOut.debugMessage("MessageACClient:onMessage", "Hey, we got a message! It was sent from x:" + message.x + ", y:"
					+ message.y + ", z:" + message.z);

			TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);

			if (tileEntity instanceof TileEntityAutoCompressor) {
				// ((TileEntityAutoCompressor) tileEntity).setRecipes(message.recipeNum, message.recipeValue);
				DebugOut.debugMessage("MessageACClient:onMessage", "And shockingly it's for our actual tileEntity!");
			}

			// get the player and add a chat message
			ctx.getServerHandler().playerEntity.addChatComponentMessage(new ChatComponentText(
					"AC GUI Button 0 pressed, need to toggle value"));
			DebugOut.debugMessage("MessageACClient:onMessage", "Stuff should have just been spat into chat...");

			return null;
		}
	}
}
