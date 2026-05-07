package vazkii.botania.client.integration.nei;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;

public class IMCForNEI {
    public static void IMCSender() {
        sendCatalyst("botania.brewery", "Botania:vial", -1);
        sendCatalyst("botania.brewery", "Botania:vial:1", -1);
        sendCatalyst("botania.brewery", "Botania:incenseStick", -1);
    }

    private static void sendCatalyst(String handlerName, String stack, int priority) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("handlerID", handlerName);
        nbt.setString("itemName", stack);
        nbt.setInteger("priority", priority);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerCatalystInfo", nbt);
    }
}
