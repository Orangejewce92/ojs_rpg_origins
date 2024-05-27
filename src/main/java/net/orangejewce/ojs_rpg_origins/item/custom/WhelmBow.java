package net.orangejewce.ojs_rpg_origins.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WhelmBow extends BowItem {

    public WhelmBow(Rarity epic, Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.whelm")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
        tooltip.add(Text.translatable("tooltip.info_whelm")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
        tooltip.add(Text.translatable("tooltip.multishot")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700))));
        tooltip.add(Text.translatable("tooltip.powerful_whelm")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)).withBold(true)));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        boolean bl = !user.getProjectileType(itemStack).isEmpty();

        if (!user.getAbilities().creativeMode && !bl) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            boolean bl = playerEntity.getAbilities().creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemStack = playerEntity.getProjectileType(stack);

            if (!itemStack.isEmpty() || bl) {
                if (itemStack.isEmpty()) {
                    itemStack = new ItemStack(Items.ARROW);
                }

                int i = this.getMaxUseTime(stack) - remainingUseTicks;
                float f = BowItem.getPullProgress(i);

                if ((double) f >= 0.1D) {
                    boolean bl2 = bl && itemStack.getItem() instanceof ArrowItem;
                    if (!world.isClient) {
                        ArrowItem arrowItem = (ArrowItem) (itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);
                        PersistentProjectileEntity arrow1 = arrowItem.createArrow(world, itemStack, playerEntity);
                        PersistentProjectileEntity arrow2 = arrowItem.createArrow(world, itemStack, playerEntity);
                        PersistentProjectileEntity arrow3 = arrowItem.createArrow(world, itemStack, playerEntity);

                        // Custom arrow properties
                        arrow1.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 3.0F, 1.0F);
                        arrow2.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 3.0F, 1.0F);
                        arrow3.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 3.0F, 1.0F);

                        arrow2.setVelocity(arrow2.getVelocity().add(new Vec3d(0.15D, 0.0D, 0.15D)));
                        arrow3.setVelocity(arrow3.getVelocity().add(new Vec3d(-0.15D, 0.0D, -0.15D)));

                        arrow1.setDamage(arrow1.getDamage() + 2.0D);
                        arrow2.setDamage(arrow2.getDamage() + 2.0D);
                        arrow3.setDamage(arrow3.getDamage() + 2.0D);

                        for (int j = 0; j < 10; j++) {
                            world.addParticle(ParticleTypes.CRIT, arrow1.getX() + (world.random.nextDouble() - 0.5D) * (double) arrow1.getWidth(), arrow1.getY() + world.random.nextDouble() * (double) arrow1.getHeight(), arrow1.getZ() + (world.random.nextDouble() - 0.5D) * (double) arrow1.getWidth(), 0.0D, 0.0D, 0.0D);
                            world.addParticle(ParticleTypes.CRIT, arrow2.getX() + (world.random.nextDouble() - 0.5D) * (double) arrow2.getWidth(), arrow2.getY() + world.random.nextDouble() * (double) arrow2.getHeight(), arrow2.getZ() + (world.random.nextDouble() - 0.5D) * (double) arrow2.getWidth(), 0.0D, 0.0D, 0.0D);
                            world.addParticle(ParticleTypes.CRIT, arrow3.getX() + (world.random.nextDouble() - 0.5D) * (double) arrow3.getWidth(), arrow3.getY() + world.random.nextDouble() * (double) arrow3.getHeight(), arrow3.getZ() + (world.random.nextDouble() - 0.5D) * (double) arrow3.getWidth(), 0.0D, 0.0D, 0.0D);
                        }

                        stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(playerEntity.getActiveHand()));

                        world.spawnEntity(arrow1);
                        world.spawnEntity(arrow2);
                        world.spawnEntity(arrow3);
                    }

                    world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                    if (!bl2 && !playerEntity.getAbilities().creativeMode) {
                        itemStack.decrement(3);
                        if (itemStack.isEmpty()) {
                            playerEntity.getInventory().removeOne(itemStack);
                        }
                    }

                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                }
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }
}
