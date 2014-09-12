package autocompressor;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageACGUI implements IMessage {
	public int	x;
	public int	y;
	public int	z;
	public int	whichButton;
	public int[] buttonStatus;

	public MessageACGUI() {
	}

	public MessageACGUI(TileEntityAutoCompressor tileEntityAC, int buttonPressed) {
		this.x = tileEntityAC.xCoord;
		this.y = tileEntityAC.yCoord;
		this.z = tileEntityAC.zCoord;
		this.whichButton = buttonPressed;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		//DebugOut.debugMessage("MessageACClient:fromBytes", "Decoding Message");
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.whichButton = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		//DebugOut.debugMessage("MessageACClient:toBytes", "Encoding Message");
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.whichButton);
	}

	public static class Handler implements IMessageHandler<MessageACGUI, IMessage> {
		/**
		 * This gets called when the packet is read and received.
		 */
		@Override
		public IMessage onMessage(MessageACGUI message, MessageContext ctx) {
			/*
			 * DebugOut.debugMessage("MessageACClient:onMessage", "Hey, we got a message! It was sent from x:" +
			 * message.x + ", y:"+ message.y + ", z:" + message.z);
			 */

			TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);

			if (tileEntity instanceof TileEntityAutoCompressor) {
				((TileEntityAutoCompressor) tileEntity).toggleRecipe(message.whichButton);
				ctx.getServerHandler().playerEntity.worldObj.markBlockForUpdate(message.x, message.y, message.z);
			}

			/*
			 * // get the player and add a chat message ctx.getServerHandler().playerEntity.addChatComponentMessage(new
			 * ChatComponentText( "AC GUI Button 0 pressed, need to toggle value"));
			 * DebugOut.debugMessage("MessageACClient:onMessage", "Stuff should have just been spat into chat...");
			 */

			return null;
		}
	}
}
