package autocompressor.machine;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageACGUI implements IMessage, IMessageHandler<MessageACGUI, IMessage> {
	public int	x;
	public int	y;
	public int	z;
	public int	whichButton;

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
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.whichButton = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.whichButton);
	}

	/*
	 * This gets called when the packet is read and received.
	 */
	@Override
	public IMessage onMessage(MessageACGUI message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);

		if (tileEntity instanceof TileEntityAutoCompressor) {
			((TileEntityAutoCompressor) tileEntity).toggleRecipe(message.whichButton);
		}

		return null;
	}
}
