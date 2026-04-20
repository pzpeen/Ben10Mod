package net.pzpeen.ben10mod.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.pzpeen.ben10mod.Ben10Mod;

public class ModTags {
    public static class Items{

        public static TagKey<Item> POWER_ITEMS = tag("power_items");


        private static TagKey<Item> tag(String name){
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, name));
        }
    }

    public static class Blocks{

        private static TagKey<Block> tag(String name){
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, name));
        }
    }
}
