package net.orangejewce.ojs_rpg_origins.item.custom;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.orangejewce.ojs_rpg_origins.item.ModToolMaterial;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScytheItem extends SwordItem {
    private static final int COOLDOWN_TICKS = 20 * 5; // 5 second cooldown
    private static final double SWEEP_RADIUS = 3.0; // Radius for the sweeping attack
    private static final float SWEEP_DAMAGE = 4.0f; // Damage dealt by the sweeping attack
    private static final int SLOWNESS_DURATION = 100; // Duration of the slowness effect in ticks (5 seconds)
    private static final float LIFE_STEAL_PERCENTAGE = 0.2f; // 20% of damage dealt is returned as health
    private static final double KNOCKBACK_STRENGTH = 1.0; // Strength of the knockback effect

    private static final float ATTACK_DAMAGE = 4.5f; // Base attack damage

    public ScytheItem() {
        super((ToolMaterial) ModToolMaterial.SAPPHIRE, (int) ATTACK_DAMAGE, -2.4F, new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.getItemCooldownManager().isCoolingDown(this)) {
            performSweepAttack(world, player);
            player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            stack.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 1.0F);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.fail(stack);
    }

    private void performSweepAttack(World world, PlayerEntity player) {
        if (!world.isClient) {
            Vec3d center = player.getPos();
            Box box = new Box(center.add(-SWEEP_RADIUS, -SWEEP_RADIUS, -SWEEP_RADIUS), center.add(SWEEP_RADIUS, SWEEP_RADIUS, SWEEP_RADIUS));
            List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box, entity -> entity != player);
            float totalDamageDealt = 0.0f;
            for (LivingEntity entity : entities) {
                entity.damage(player.getDamageSources().playerAttack(player), SWEEP_DAMAGE);
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, SLOWNESS_DURATION, 1));
                entity.takeKnockback(KNOCKBACK_STRENGTH, player.getX() - entity.getX(), player.getZ() - entity.getZ());
                totalDamageDealt += SWEEP_DAMAGE;
            }
            player.heal(totalDamageDealt * LIFE_STEAL_PERCENTAGE);
            // Add custom particles
            for (int i = 0; i < 20; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * SWEEP_RADIUS * 2;
                double offsetY = (world.random.nextDouble() - 0.5) * SWEEP_RADIUS * 2;
                double offsetZ = (world.random.nextDouble() - 0.5) * SWEEP_RADIUS * 2;
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, center.x + offsetX, center.y + offsetY, center.z + offsetZ, 0, 0, 0);
            }
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 0.0F, 1.0F);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.balmung.details")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withItalic(true)));
        } else {
            tooltip.add(Text.translatable("tooltip.scythe").styled(style -> style.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
            tooltip.add(Text.translatable("tooltip.info_scythe").styled(style -> style.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            tooltip.add(Text.translatable("tooltip.sweep_ability").styled(style -> style.withColor(TextColor.fromRgb(0xFFD700))));
            tooltip.add(Text.translatable("tooltip.lifesteal_ability").styled(style -> style.withColor(TextColor.fromRgb(0xFF4500)).withItalic(true)));
            tooltip.add(Text.translatable("tooltip.shift_info")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            target.damage(player.getDamageSources().playerAttack(player), ATTACK_DAMAGE);
            stack.damage(1, attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            return true;
        }
        return false;
    }
}
