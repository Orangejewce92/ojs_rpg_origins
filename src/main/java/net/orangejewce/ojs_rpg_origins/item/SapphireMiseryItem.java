// SapphireMiseryItem.java
package net.orangejewce.ojs_rpg_origins.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = "ojs_rpg_origins")
public class SapphireMiseryItem extends SwordItem {

    private static final int COOLDOWN = 200; // 10 seconds cooldown

    public SapphireMiseryItem() {
        super(Tiers.DIAMOND, 10, -3.0F, new Item.Properties());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!world.isClientSide && !player.getCooldowns().isOnCooldown(this)) {
            player.displayClientMessage(Component.literal("Ability Activated!"), true);
            player.heal(4.0F);
            world.playSound(null, player.blockPosition(), SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.getCooldowns().addCooldown(this, COOLDOWN);
        }
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player) {
            Player player = (Player) attacker;
            target.hurt(player.damageSources().playerAttack(player), target.getHealth() * 2);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.getMainHandItem().getItem() instanceof SapphireMiseryItem) {
            if (player.tickCount % 200 == 0) {
                player.heal(1.0F);
            }
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 210, 0, true, false, false));
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.getMainHandItem().getItem() instanceof SapphireMiseryItem) {
                event.setAmount(event.getAmount() * 0.9F);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.balmung.details")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withItalic(true)));
        } else {
            tooltip.add(Component.translatable("tooltip.sapphire_misery").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
            tooltip.add(Component.translatable("tooltip.info_sapphire_misery").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            tooltip.add(Component.translatable("tooltip.ability").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700))));
            tooltip.add(Component.translatable("tooltip.special_ability").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF4500)).withItalic(true)));
            tooltip.add(Component.translatable("tooltip.shift_info")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == ModItems.SAPPHIRE_RARE.get() || super.isValidRepairItem(toRepair, repair);
    }
}
