package net.orangejewce.ojs_rpg_origins.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScytheItem extends SwordItem {
    private static final int COOLDOWN_TICKS = 20 * 5; // 5 second cooldown
    private static final double SWEEP_RADIUS = 3.0; // Radius for the sweeping attack
    private static final float SWEEP_DAMAGE = 4.0f; // Damage dealt by the sweeping attack
    private static final int SLOWNESS_DURATION = 100; // Duration of the slowness effect in ticks (5 seconds)
    private static final float LIFE_STEAL_PERCENTAGE = 0.2f; // 20% of damage dealt is returned as health
    private static final double KNOCKBACK_STRENGTH = 1.0; // Strength of the knockback effect

    private static final float ATTACK_DAMAGE = 6.0f; // Base attack damage

    public ScytheItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getCooldowns().isOnCooldown(this)) {
            performSweepAttack(world, player);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            player.awardStat(Stats.ITEM_USED.get(this));
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
            world.playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    private void performSweepAttack(Level world, Player player) {
        if (!world.isClientSide) {
            Vec3 center = player.position();
            AABB aabb = new AABB(center.add(-SWEEP_RADIUS, -SWEEP_RADIUS, -SWEEP_RADIUS), center.add(SWEEP_RADIUS, SWEEP_RADIUS, SWEEP_RADIUS));
            List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, aabb);
            float totalDamageDealt = 0.0f;
            for (LivingEntity entity : entities) {
                if (entity != player) {
                    entity.hurt(player.damageSources().playerAttack(player), SWEEP_DAMAGE);
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION, 1));
                    entity.knockback(KNOCKBACK_STRENGTH, player.getX() - entity.getX(), player.getZ() - entity.getZ());
                    totalDamageDealt += SWEEP_DAMAGE;
                }
            }
            player.heal(totalDamageDealt * LIFE_STEAL_PERCENTAGE);
            // Add custom particles
            for (int i = 0; i < 20; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * SWEEP_RADIUS * 2;
                double offsetY = (world.random.nextDouble() - 0.5) * SWEEP_RADIUS * 2;
                double offsetZ = (world.random.nextDouble() - 0.5) * SWEEP_RADIUS * 2;
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, center.x + offsetX, center.y + offsetY, center.z + offsetZ, 0, 0, 0);
            }
            world.playSound(null, player.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.0F, 0.0F);
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.balmung.details")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withItalic(true)));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.scythe").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
            pTooltipComponents.add(Component.translatable("tooltip.info_scythe").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            pTooltipComponents.add(Component.translatable("tooltip.sweep_ability").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700))));
            pTooltipComponents.add(Component.translatable("tooltip.lifesteal_ability").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF4500)).withItalic(true)));
            pTooltipComponents.add(Component.translatable("tooltip.shift_info")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // Allows for continuous use if needed
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR; // Display spear animation while using
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            target.hurt(player.damageSources().playerAttack(player), ATTACK_DAMAGE);
            stack.hurtAndBreak(1, attacker, (e) -> e.broadcastBreakEvent(attacker.getUsedItemHand()));
            return true;
        }
        return false;
    }
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == ModItems.SAPPHIRE_RARE.get() || super.isValidRepairItem(toRepair, repair);
    }
}
