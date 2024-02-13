package vazkii.botania.common.core.helper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;


public class LokiCursor {
    private static final String TAG_X_OFFSET = "xOffset";
    private static final String TAG_Y_OFFSET = "yOffset";
    private static final String TAG_Z_OFFSET = "zOffset";
    private static final String TAG_MIRROR_MODE = "mirror";

    private static final byte xAxisBitmask = 4; //100
    private static final byte yAxisBitmask = 2; //010
    private static final byte zAxisBitmask = 1; //001

    private byte mirrorMode;
    private ChunkCoordinates coordinates;

    public LokiCursor(int x, int y, int z, byte mode){
        coordinates = new ChunkCoordinates(x,y,z);
        mirrorMode = mode;
    }

    public static LokiCursor fromNBT(NBTTagCompound cmp){
        int x = cmp.getInteger(TAG_X_OFFSET);
        int y = cmp.getInteger(TAG_Y_OFFSET);
        int z = cmp.getInteger(TAG_Z_OFFSET);
        byte mirror = cmp.getByte(TAG_MIRROR_MODE);

        return new LokiCursor(x,y,z,mirror);
    }
    public static boolean isMirrorX(byte mirrorMode){
        return (mirrorMode & xAxisBitmask) > 0;
    }
    public static boolean isMirrorY(byte mirrorMode){
        return (mirrorMode & yAxisBitmask) > 0;
    }
    public static boolean isMirrorZ(byte mirrorMode){
        return (mirrorMode & zAxisBitmask) > 0;
    }
    public  NBTTagCompound toNBT() {
        NBTTagCompound cmp = new NBTTagCompound();
        cmp.setInteger(TAG_X_OFFSET, coordinates.posX);
        cmp.setInteger(TAG_Y_OFFSET, coordinates.posY);
        cmp.setInteger(TAG_Z_OFFSET, coordinates.posZ);
        cmp.setInteger(TAG_MIRROR_MODE, mirrorMode);
        return cmp;
    }

    public int getX(){
        return coordinates.posX;
    }

    public int getY(){
        return coordinates.posY;
    }

    public int getZ(){
        return coordinates.posZ;
    }

    public ChunkCoordinates getCoordinates(){
        return coordinates;
    }

    public boolean isMirrorX(){
        return isMirrorX(mirrorMode);
    }
    public boolean isMirrorY(){
        return isMirrorY(mirrorMode);
    }
    public boolean isMirrorZ(){
        return isMirrorZ(mirrorMode);
    }

    public boolean isMirror(){
        return mirrorMode!=0;
    }
}
