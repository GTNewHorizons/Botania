package vazkii.botania.api.lexicon.multiblock.compat;

import com.gtnewhorizon.structurelib.alignment.constructable.IMultiblockInfoContainer;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import vazkii.botania.api.lexicon.multiblock.Multiblock;
import vazkii.botania.api.lexicon.multiblock.component.MultiblockComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Only gets loaded if StructureLib is present inside the Modpack
 */
public class MultiblockCompatRegistry {

    public static <T extends TileEntity> void registerMultiblock(
            Multiblock mb, Class<T> controllerTileClass,
            Block controllerBlock,
            MultiblockComponent... extra
    ) {
        List<MultiblockComponent> components = mb.getComponents();
        if (extra.length != 0) {
            components = new ArrayList<>(components);
            Collections.addAll(components, extra);
        }

        Map<Integer, RawStructureData> structureDataMap = new HashMap<>();
        int min_x = Integer.MAX_VALUE, max_x = Integer.MIN_VALUE;
        int min_y = Integer.MAX_VALUE, max_y = Integer.MIN_VALUE;
        int min_z = Integer.MAX_VALUE, max_z = Integer.MIN_VALUE;

        char ch = 'a';
        for (MultiblockComponent component : components) {
            int hash = getHash(component.getBlock(), component.getMeta());
            ChunkCoordinates pos = component.getRelativePosition();
            RawStructureData structureData = structureDataMap.get(hash);
            if (structureData == null) {
                List<ChunkCoordinates> coordinates = new ArrayList<>();
                structureData = new RawStructureData(
                        coordinates,
                        component.getBlock(),
                        component.getMeta(),
                        ch++
                );
                structureDataMap.put(hash, structureData);
            }
            structureData.getPositions().add(pos);

            min_x = Math.min(min_x, pos.posX); max_x = Math.max(max_x, pos.posX);
            min_y = Math.min(min_y, pos.posY); max_y = Math.max(max_y, pos.posY);
            min_z = Math.min(min_z, pos.posZ); max_z = Math.max(max_z, pos.posZ);
        }

        int x_size = max_x - min_x + 1;
        int y_size = max_y - min_y + 1;
        int z_size = max_z - min_z + 1;

        //Data is saved in [z][y][x] format
        String[][] stringData = new String[z_size][y_size];

        char[] buffer = new char[x_size];
        Arrays.fill(buffer, ' ');
        //Since Strings and their content are immutable, this is fine.
        String filledWithSpaces = new String(buffer);
        for (int z = 0; z < z_size; z++) {
            for (int y = 0; y < y_size; y++) {
                stringData[z][y] = filledWithSpaces;
            }
        }

        int controller_x = 0, controller_y = 0, controller_z = 0;
        StringBuilder builder = new StringBuilder(x_size);
        for (RawStructureData data : structureDataMap.values()) {
            for (ChunkCoordinates pos : data.getPositions()) {
                int x = pos.posX - min_x;
                int z = pos.posZ - min_z;
                //Data has to get flipped
                int y = (stringData[z].length - 1) - (pos.posY - min_y);

                if (data.getBlock() == controllerBlock) {
                    controller_x = x;
                    controller_y = y;
                    controller_z = z;
                }

                String s = stringData[z][y];
                builder.append(s);
                builder.setCharAt(x, data.getBlockIdentifier());
                stringData[z][y] = builder.toString();
                builder.setLength(0);
            }
        }

        StructureDefinition.Builder<? super T> structureBuilder = IStructureDefinition.builder()
                .addShape("main", stringData);

        for (RawStructureData data : structureDataMap.values()) {
            structureBuilder.addElement(
                    data.getBlockIdentifier(),
                    StructureUtility.ofBlock(
                            data.getBlock(),
                            data.getMetadata()
                    )
            );
        }

        IMultiblockInfoContainer.registerTileClass(
                controllerTileClass,
                new BotaniaMultiblockInfoContainer<>(
                        structureBuilder.build(),
                        controller_x,
                        controller_y,
                        controller_z
                )
        );
    }

    private static int getHash(Block block, int metadata) {
        return block.hashCode() * 31 + metadata;
    }


    private static class BotaniaMultiblockInfoContainer<T extends TileEntity> implements IMultiblockInfoContainer<T> {

        private final IStructureDefinition<? super T> structure;
        private final int x_offset, y_offset, z_offset;

        public BotaniaMultiblockInfoContainer(
                IStructureDefinition<? super T> structure,
                int x_offset, int y_offset, int z_offset
        ) {
            this.structure = structure;
            this.x_offset = x_offset;
            this.y_offset = y_offset;
            this.z_offset = z_offset;
        }

        @Override
        public void construct(ItemStack stackSize, boolean hintsOnly, T tileEntity, ExtendedFacing aSide) {
            structure.buildOrHints(
                    tileEntity,
                    stackSize,
                    "main",
                    tileEntity.getWorldObj(),
                    noSideWay(aSide),
                    tileEntity.xCoord,
                    tileEntity.yCoord,
                    tileEntity.zCoord,
                    x_offset,
                    y_offset,
                    z_offset,
                    hintsOnly
            );
        }

        @Override
        public int survivalConstruct(ItemStack stackSize, int elementBudge, ISurvivalBuildEnvironment env, T tileEntity, ExtendedFacing aSide) {
            return structure.survivalBuild(
                    tileEntity,
                    stackSize,
                    "main",
                    tileEntity.getWorldObj(),
                    noSideWay(aSide),
                    tileEntity.xCoord,
                    tileEntity.yCoord,
                    tileEntity.zCoord,
                    x_offset,
                    y_offset,
                    z_offset,
                    elementBudge,
                    env,
                    false
            );
        }

        @Override
        public String[] getDescription(ItemStack stackSize) {
            return new String[0];
        }

        private static ExtendedFacing noSideWay(ExtendedFacing aSide) {
            return aSide.getDirection().offsetY != 0 ? ExtendedFacing.DEFAULT : aSide.getOppositeDirection();
        }
    }

    private static class RawStructureData {
        private final List<ChunkCoordinates> positions;
        private final char identifier;
        private final Block block;
        private final int metadata;

        public RawStructureData(List<ChunkCoordinates> positions, Block block, int metadata, char identifier) {
            this.positions = positions;
            this.identifier = identifier;
            this.block = block;
            this.metadata = metadata;
        }

        public Block getBlock() {
            return block;
        }

        public int getMetadata() {
            return metadata;
        }

        public List<ChunkCoordinates> getPositions() {
            return positions;
        }

        public char getBlockIdentifier() {
            return identifier;
        }
    }
}
