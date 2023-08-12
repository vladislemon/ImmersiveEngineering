package blusunrize.immersiveengineering.common.util.network;

import blusunrize.immersiveengineering.api.DimensionChunkCoords;
import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

public class MessageMineralCacheSync implements IMessage {
    private final Map<DimensionChunkCoords, ExcavatorHandler.MineralWorldInfo> mineralCache = new HashMap<>();
    private boolean clear;

    public MessageMineralCacheSync(Map<DimensionChunkCoords, ExcavatorHandler.MineralWorldInfo> mineralCache) {
        this.mineralCache.putAll(mineralCache);
        this.clear = true;
    }

    public MessageMineralCacheSync(DimensionChunkCoords coords, ExcavatorHandler.MineralWorldInfo info) {
        this.mineralCache.put(coords, info);
        this.clear = false;
    }

    public MessageMineralCacheSync() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        clear = buf.readBoolean();
        for (int i = 0; i < size; i++) {
            NBTTagCompound tag = ByteBufUtils.readTag(buf);
            DimensionChunkCoords coords = DimensionChunkCoords.readFromNBT(tag);
            ExcavatorHandler.MineralWorldInfo info = ExcavatorHandler.MineralWorldInfo.readFromNBT(tag.getCompoundTag("info"));
            mineralCache.put(coords, info);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(mineralCache.size());
        buf.writeBoolean(clear);
        for (Map.Entry<DimensionChunkCoords, ExcavatorHandler.MineralWorldInfo> e : mineralCache.entrySet()) {
            NBTTagCompound tag = e.getKey().writeToNBT();
            tag.setTag("info", e.getValue().writeToNBT());
            ByteBufUtils.writeTag(buf, tag);
        }
    }

    public static class HandlerClient implements IMessageHandler<MessageMineralCacheSync, IMessage> {
        @Override
        public IMessage onMessage(MessageMineralCacheSync message, MessageContext ctx) {
            if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
                return null;
            }
            if (message.clear) {
                ExcavatorHandler.mineralCache.clear();
            }
            ExcavatorHandler.mineralCache.putAll(message.mineralCache);
            return null;
        }
    }
}