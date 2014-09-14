package autocompressor.mk2;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ACGUIMark2MessageHandler implements IMessage, IMessageHandler<ACGUIMark2MessageHandler, IMessage> {
	public int	x;
	public int	y;
	public int	z;
	public int	whichButton;

	public ACGUIMark2MessageHandler() {
	}

	public ACGUIMark2MessageHandler(TEACMark2 tileEntityAC, int buttonPressed) {
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
	public IMessage onMessage(ACGUIMark2MessageHandler message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);

		if (tileEntity instanceof TEACMark2) {
			((TEACMark2) tileEntity).toggleRecipe(message.whichButton);
		}

		return null;
	}
}
