package net.orangejewce.ojs_rpg_origins.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.orangejewce.ojs_rpg_origins.config.ThiefGloveConfig;

@Mod.EventBusSubscriber(modid = "ojs_rpg_origins")
public class ThiefGloveItem extends Item implements ICurioItem {
    public ThiefGloveItem() {
        super(new Item.Properties().stacksTo(1).durability(50));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level().isClientSide && player.isShiftKeyDown() && target instanceof Villager) {
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResult.PASS; // If on cooldown, pass the interaction
            }

            Villager villager = (Villager) target;
            if (new Random().nextDouble() < ThiefGloveConfig.stealChance.get()) {
                List<ItemStack> possibleItems = ThiefGloveConfig.getStealableItems();
                ItemStack stolenItem = possibleItems.get(new Random().nextInt(possibleItems.size())).copy();
                player.getInventory().add(stolenItem);
                player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand)); // Reduce durability by 1 on success
                player.getCooldowns().addCooldown(this, ThiefGloveConfig.cooldownTicks.get()); // Add cooldown
                return InteractionResult.SUCCESS;
            } else {
                player.level().playSound(null, player.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);
                player.getCooldowns().addCooldown(this, ThiefGloveConfig.cooldownTicks.get()); // Add cooldown even if the steal fails
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag unused) {
        return CuriosApi.createCurioProvider(new ICurio() {

            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public void curioTick(SlotContext slotContext) {
                // Ticking logic if needed
            }

            public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
                return slotContext.identifier().equals("thieves_pockets");
            }
        });
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.thief_glove.details")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withItalic(true)));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.thief_glove")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
            tooltipComponents.add(Component.translatable("tooltip.info_thief_glove")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            tooltipComponents.add(Component.translatable("tooltip.steal_ability")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700))));
            tooltipComponents.add(Component.translatable("tooltip.steal_shift_right_click")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF4500)).withItalic(true)));
            tooltipComponents.add(Component.translatable("tooltip.shift_info")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
        }
        super.appendHoverText(stack, level, tooltipComponents, flag);
    }
}




