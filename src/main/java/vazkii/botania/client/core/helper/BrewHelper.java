package vazkii.botania.client.core.helper;

import codechicken.nei.ItemList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.brew.BrewUtilities;
import vazkii.botania.api.brew.IBrewContainer;
import vazkii.botania.common.Botania;

import java.util.ArrayList;
import java.util.List;

public final class BrewHelper {

    public static List<ItemStack> getBrewContainers() {
        class Cache {
            private static final List<ItemStack> BREW_CONTAINERS = List.copyOf(scanBrewContainers());
        }

        return Cache.BREW_CONTAINERS;
    }

    private static List<ItemStack> scanBrewContainers() {
        ArrayList<ItemStack> containers = new ArrayList<>();

        if (Botania.neiLoaded) {
            for (ItemStack item : ItemList.items) {
                if (item != null && BrewUtilities.isEmptyBrewContainer(item)) {
                    containers.add(item);
                }
            }
        }

        if (containers.isEmpty()) {
            @SuppressWarnings("unchecked")
            final Iterable<Item> itemRegistry = Item.itemRegistry;

            ArrayList<ItemStack> potentialContainers = new ArrayList<>();
            for (Item item : itemRegistry) {
                if (item instanceof IBrewContainer) {
                    item.getSubItems(item, item.getCreativeTab(), potentialContainers);

                    for (ItemStack maybeContainer : potentialContainers) {
                        if (maybeContainer != null && BrewUtilities.isEmptyBrewContainer(maybeContainer)) {
                            containers.add(maybeContainer);
                        }
                    }

                    potentialContainers.clear();
                }
            }
        }

        return containers;
    }

}
