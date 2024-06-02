package net.orangejewce.ojs_rpg_origins.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Balmung extends SwordItem {
    public Balmung() {
        super(Tiers.NETHERITE, 12, -2.4F, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            Level level = attacker.level();
            BlockPos pos = target.blockPosition();
            AABB areaOfEffect = new AABB(pos).inflate(3);

            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, areaOfEffect);
            for (LivingEntity entity : entities) {
                if (entity != attacker && entity != target) {
                    entity.hurt(target.damageSources().playerAttack(player), 5.0F);
                }
            }

            level.playSound(null, pos, SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof LivingEntity) {
            target.setSecondsOnFire(5);
            player.getCooldowns().addCooldown(this, 100);
            player.level().playSound(null, target.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(player.level().isClientSide());
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.balmung.details")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withItalic(true)));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.balmung")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700)).withBold(true)));
            tooltipComponents.add(Component.translatable("tooltip.info_balmung")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            tooltipComponents.add(Component.translatable("tooltip.special_attack")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00))));
            tooltipComponents.add(Component.translatable("tooltip.shift_info")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        }
    }
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == ModItems.SAPPHIRE_RARE.get() || super.isValidRepairItem(toRepair, repair);
    }
}
