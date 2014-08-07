package cr0s.WarpDrive.machines;

import cpw.mods.fml.common.FMLCommonHandler;
import cr0s.WarpDrive.WarpDrive;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IPeripheral;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityMonitor extends TileEntity implements IPeripheral {
	private int frequency = -1;

	private String[] methodsArray =
	{
		"freq"
	};

	private int packetSendTicks = 20;

	@Override
	public void updateEntity() {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			packetSendTicks--;
			if (packetSendTicks == 0) {
				packetSendTicks = 20 * 5;
				sendFreqPacket();
			}
			return;
		}
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int parFrequency) {
		if (frequency != parFrequency) {
			frequency = parFrequency;
			WarpDrive.debugPrint("" + this + " Monitor frequency set to " + frequency);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		frequency = tag.getInteger("frequency");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("frequency", frequency);
	}

	// IPeripheral methods implementation
	@Override
	public String getType() {
		return "monitor";
	}

	@Override
	public String[] getMethodNames() {
		return methodsArray;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		if (arguments.length == 1) {
			setFrequency(((Double)arguments[0]).intValue());
		}
		return new Integer[] { frequency };
	}

	public void sendFreqPacket() {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
			DataOutputStream outputStream = new DataOutputStream(bos);

			try {
				outputStream.writeInt(xCoord);
				outputStream.writeInt(yCoord);
				outputStream.writeInt(zCoord);
				outputStream.writeInt(frequency);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = "WarpDriveFreq";
			packet.data = bos.toByteArray();
			packet.length = bos.size();
			MinecraftServer.getServer().getConfigurationManager().sendToAllNear(xCoord, yCoord, zCoord, 100, worldObj.provider.dimensionId, packet);
//			WarpDrive.debugPrint("" + this + " Packet '" + packet.channel + "' sent (" + xCoord + ", " + yCoord + ", " + zCoord + ") '" + frequency + "'");
		}
	}

	@Override
	public void attach(IComputerAccess computer) {
	}

	@Override
	public void detach(IComputerAccess computer) {
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other == this;
	}
}