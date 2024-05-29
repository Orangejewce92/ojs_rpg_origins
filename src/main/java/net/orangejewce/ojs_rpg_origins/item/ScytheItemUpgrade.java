package net.orangejewce.ojs_rpg_origins.item;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class ScytheItemUpgrade extends SwordItem {
    private static final int COOLDOWN_TICKS = 20 * 5; // 5 second cooldown
    private static final double SWEEP_RADIUS = 4.0; // Radius for the sweeping attack
    private static final float SWEEP_DAMAGE = 4.0f; // Damage dealt by the sweeping attack
    private static final int SLOWNESS_DURATION = 100; // Duration of the slowness effect in ticks (5 seconds)
    private static final float LIFE_STEAL_PERCENTAGE = 0.2f; // 20% of damage dealt is returned as health
    private static final double KNOCKBACK_STRENGTH = 1.0; // Strength of the knockback effect
    private static final float ATTACK_DAMAGE = 7.5f; // Base attack damage
    private static final int BUFF_DURATION = 200; // Duration of the player's buff in ticks (10 seconds)
    private static final int DEBUFF_DURATION = 200; // Duration of the enemies' debuff in ticks (10 seconds)

    public ScytheItemUpgrade(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getCooldowns().isOnCooldown(this)) {
            performSpecialAbility(world, player);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            player.awardStat(Stats.ITEM_USED.get(this));
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
            world.playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    private void performSpecialAbility(Level world, Player player) {
        if (!world.isClientSide) {
            Vec3 center = player.position();
            AABB aabb = new AABB(center.add(-SWEEP_RADIUS, -SWEEP_RADIUS, -SWEEP_RADIUS), center.add(SWEEP_RADIUS, SWEEP_RADIUS, SWEEP_RADIUS));
            List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, aabb);

            // Apply debuff to enemies
            for (LivingEntity entity : entities) {
                if (entity != player) {
                    entity.hurt(player.damageSources().playerAttack(player), SWEEP_DAMAGE);
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, DEBUFF_DURATION, 1));
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, DEBUFF_DURATION, 1));
                    entity.knockback(KNOCKBACK_STRENGTH, player.getX() - entity.getX(), player.getZ() - entity.getZ());
                }
            }

            // Apply buff to player
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, BUFF_DURATION, 1));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, BUFF_DURATION, 1));

            // Add custom particles
            for (int i = 0; i < 20; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * SWEEP_RADIUS * 2;
                double offsetY = (world.random.nextDouble() - 0.5) * SWEEP_RADIUS * 2;
                double offsetZ = (world.random.nextDouble() - 0.5) * SWEEP_RADIUS * 2;
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, center.x + offsetX, center.y + offsetY, center.z + offsetZ, 0, 0, 0);
            }
            world.playSound(null, player.blockPosition(), SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.scythe_1").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700)).withBold(true)));
        tooltipComponents.add(Component.translatable("tooltip.info_scythe_1").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
        tooltipComponents.add(Component.translatable("tooltip.sweep_ability_1").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00))));
        tooltipComponents.add(Component.translatable("tooltip.lifesteal_ability_1").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF4500)).withItalic(true)));
        tooltipComponents.add(Component.translatable("tooltip.special_ability_1").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x8A2BE2)).withItalic(true)));
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
}
