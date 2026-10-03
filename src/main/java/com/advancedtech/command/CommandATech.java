package com.advancedtech.command;

import com.advancedtech.config.ATConfig;
import com.advancedtech.net.Network;
import com.advancedtech.research.IResearchData;
import com.advancedtech.research.ResearchCapability;
import com.advancedtech.research.ResearchNode;
import com.advancedtech.research.ResearchRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/** Тестовая команда: /atech points N | unlock <id|all> | reset */
public class CommandATech extends CommandBase {
    @Override public String getName() { return "atech"; }
    @Override public String getUsage(ICommandSender sender) { return "/atech points <N> | unlock <id|all> | reset"; }
    @Override public int getRequiredPermissionLevel() { return ATConfig.enableDebugCommand ? 0 : 4; }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP p = getCommandSenderAsPlayer(sender);
        IResearchData d = ResearchCapability.get(p);
        if (d == null || args.length == 0) throw new WrongUsageException(getUsage(sender));

        String msg;
        if (args[0].equals("points")) {
            if (args.length < 2) throw new WrongUsageException(getUsage(sender));
            d.addPoints(parseInt(args[1]));
            msg = "Очки исследований: " + d.getPoints();
        } else if (args[0].equals("unlock")) {
            if (args.length < 2) throw new WrongUsageException(getUsage(sender));
            d.addPoints(1000000);
            int count = 0;
            if (args[1].equals("all")) {
                for (ResearchNode n : ResearchRegistry.all()) if (d.unlock(n.id)) count++;
            } else if (d.unlock(args[1])) {
                count = 1;
            }
            d.addPoints(-1000000);
            msg = "Изучено исследований: " + count;
        } else if (args[0].equals("reset")) {
            d.deserializeNBT(new NBTTagCompound());
            msg = "Прогресс сброшен";
        } else {
            throw new WrongUsageException(getUsage(sender));
        }
        Network.syncResearch(p);
        sender.sendMessage(new TextComponentString(msg));
    }
}
