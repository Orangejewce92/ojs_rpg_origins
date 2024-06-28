package net.orangejewce.ojs_rpg_origins.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HarmonicLuteItem extends Item {
    private static final int COOLDOWN_DURATION = 600; // Cooldown duration in ticks (30 seconds)

    public HarmonicLuteItem() {
        super(new Item.Settings().maxCount(1).maxDamage(250).rarity(Rarity.EPIC));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!world.isClient) {
            // Check if the player is on cooldown
            if (player.getItemCooldownManager().isCoolingDown(this)) {
                return TypedActionResult.fail(itemStack);
            }

            playMusic(world, player);
            itemStack.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
            player.getItemCooldownManager().set(this, COOLDOWN_DURATION); // Set the cooldown
        } else {
            if (player.isSneaking()) {
                player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(), 1.0F, 1.0F);
            } else {
                player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value(), 1.0F, 1.0F);
            }
        }
        return TypedActionResult.success(itemStack);
    }

    private void playMusic(World world, PlayerEntity player) {
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(10), entity -> entity instanceof PlayerEntity && entity != player);
        for (LivingEntity entity : entities) {
            applyPassiveEffects((PlayerEntity) entity);
        }

        if (player.isSneaking()) {
            applyShieldingSong(player);
        } else {
            applySongOfInvulnerability(player);
        }
    }

    private void applyPassiveEffects(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 0, true, true));
    }

    private void applyShieldingSong(PlayerEntity player) {
        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(10), entity -> entity instanceof PlayerEntity);
        for (LivingEntity entity : entities) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 1, true, true));
        }
    }

    private void applySongOfInvulnerability(PlayerEntity player) {
        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(5), entity -> entity instanceof PlayerEntity);
        for (LivingEntity entity : entities) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 2, true, true));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.harmonic_lute")
                .styled(style -> style.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
        tooltip.add(Text.translatable("tooltip.info_harmonic_lute")
                .styled(style -> style.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
        tooltip.add(Text.translatable("tooltip.passive_harmonic_lute")
                .styled(style -> style.withColor(TextColor.fromRgb(0xFFD700))));
        tooltip.add(Text.translatable("tooltip.active_harmonic_lute")
                .styled(style -> style.withColor(TextColor.fromRgb(0xFF4500)).withItalic(true)));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity && selected) {
            PlayerEntity player = (PlayerEntity) entity;
            List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(10), e -> e instanceof PlayerEntity);
            for (LivingEntity e : entities) {
                applyPassiveEffects((PlayerEntity) e);
            }
        }
    }
}
