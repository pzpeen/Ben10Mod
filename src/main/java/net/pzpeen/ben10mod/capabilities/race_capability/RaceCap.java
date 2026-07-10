package net.pzpeen.ben10mod.capabilities.race_capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.pzpeen.ben10mod.effects.ModEffects;
import net.pzpeen.ben10mod.races.AbstractRace;
import net.pzpeen.ben10mod.races.RacesRegistries;


@AutoRegisterCapability
public class RaceCap {
    private Player player;
    private AbstractRace race = null;
    private ResourceLocation alienId = RacesRegistries.humanRaceID;

    public RaceCap(Player player) {
        this.player = player;
    }

    public AbstractRace getRace(){
        return this.race;
    }
    public ResourceLocation getRaceId(){
        if(this.getRace() != null){
            return this.race.getID();
        }else{
            return RacesRegistries.humanRaceID;
        }

    }

    public void setRace(ResourceLocation raceId, Player player){
        setRace(raceId, player, false);
    }

    public void setRace(ResourceLocation raceId, Player player, boolean dropArmor){
        //Transforming
        if(this.player == null){
            this.player = player;
        }
        this.alienId = raceId;
        this.race = RacesRegistries.pickRace(raceId, player);
        this.player.refreshDimensions();

        //Removing wet effect
        if(this.player.getEffect(ModEffects.WET.get()) != null){
            this.player.removeEffect(ModEffects.WET.get());
        }

        //Handling Attributes
        AttributeInstance baseAttackDamage = this.player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance baseArmor = this.player.getAttribute(Attributes.ARMOR);
        if(this.race != null){
            //Setting the attributes if not human
            if(baseAttackDamage != null){
                baseAttackDamage.setBaseValue(this.race.getBaseDamage());
            }

            if(baseArmor != null){
                baseArmor.setBaseValue(this.race.getBaseArmor());
            }
        }else{
            //Returning the attribute to original values if human
            if(baseAttackDamage != null){
                baseAttackDamage.setBaseValue(1.0f);
            }
            if(baseArmor != null){
                baseArmor.setBaseValue(0.0f);
            }
        }

        //Handling the armor
        if(dropArmor){
            EquipmentSlot[] armorSlot = {
                    EquipmentSlot.FEET,
                    EquipmentSlot.LEGS,
                    EquipmentSlot.CHEST,
                    EquipmentSlot.HEAD
            };

            for(int i = 0; i < 4; i++){
                EquipmentSlot slot = armorSlot[i];
                ItemStack armorItem = player.getItemBySlot(slot);

                if(!armorItem.isEmpty()){
                    player.setItemSlot(slot, ItemStack.EMPTY);
                    if(!player.getInventory().add(armorItem)){
                        player.drop(armorItem, false);
                    }
                }
            }

        }

        //Handling offhand shield
        if(player.getItemBySlot(EquipmentSlot.OFFHAND).getItem() instanceof ShieldItem){
            ItemStack shield = player.getItemBySlot(EquipmentSlot.OFFHAND);
            player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

            if(!player.getInventory().add(shield)){
                player.drop(shield, false);
            }

        }

    }

    public void saveNBT(CompoundTag nbt){
        if(this.alienId != null){
            nbt.putString("alien_id", this.alienId.toString());
        }else{
            nbt.putString("alien_id", RacesRegistries.humanRaceID.toString());
        }

    }
    public void loadNBT(CompoundTag nbt){
        //this.setRace(new ResourceLocation(nbt.getString("alien_id")));
        this.setRace(ResourceLocation.parse(nbt.getString("alien_id")), player);
    }

}
