package com.advancedtech.net;

import com.advancedtech.research.IResearchData;
import com.advancedtech.research.ResearchCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Клиент -> сервер: изучить исследование. */
public class UnlockResearchMessage implements IMessage {
    private String id = "";

    public UnlockResearchMessage() {}
    public UnlockResearchMessage(String id) { this.id = id; }

    @Override public void fromBytes(ByteBuf buf) { id = ByteBufUtils.readUTF8String(buf); }
    @Override public void toBytes(ByteBuf buf) { ByteBufUtils.writeUTF8String(buf, id); }

    public static class Handler implements IMessageHandler<UnlockResearchMessage, IMessage> {
        @Override
        public IMessage onMessage(UnlockResearchMessage msg, MessageContext ctx) {
            final EntityPlayerMP p = ctx.getServerHandler().player;
            p.getServerWorld().addScheduledTask(() -> {
                IResearchData d = ResearchCapability.get(p);
                if (d != null && d.unlock(msg.id)) {
                    Network.syncResearch(p);
                    p.sendStatusMessage(new TextComponentString("Изучено: " + msg.id), true);
                }
            });
            return null;
        }
    }
}
