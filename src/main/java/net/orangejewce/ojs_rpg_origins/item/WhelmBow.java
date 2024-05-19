package net.orangejewce.ojs_rpg_origins.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.List;

public class WhelmBow extends BowItem {

    public WhelmBow(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.whelm").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
        // Additional information
        pTooltipComponents.add(Component.translatable("tooltip.info")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));

        // Unicode icon example
        pTooltipComponents.add(Component.translatable("tooltip.multishot")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700))));

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, net.minecraft.world.entity.LivingEntity shooter, int timeLeft) {
        if (shooter instanceof ServerPlayer player) {
            boolean infinity = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            ItemStack ammo = player.getProjectile(stack);

            if (!ammo.isEmpty() || infinity) {
                if (ammo.isEmpty()) {
                    ammo = new ItemStack(Items.ARROW);
                }

                int charge = this.getUseDuration(stack) - timeLeft;
                charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, world, player, charge, !ammo.isEmpty() || infinity);
                if (charge < 0) return;

                float velocity = getPowerForTime(charge);
                if ((double)velocity >= 0.1D) {
                    boolean infiniteAmmo = player.getAbilities().instabuild || (ammo.getItem() instanceof ArrowItem && ((ArrowItem)ammo.getItem()).isInfinite(ammo, stack, player));
                    if (!world.isClientSide) {
                        ArrowItem arrowItem = (ArrowItem)(ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
                        AbstractArrow arrow1 = arrowItem.createArrow(world, ammo, player);
                        AbstractArrow arrow2 = arrowItem.createArrow(world, ammo, player);
                        AbstractArrow arrow3 = arrowItem.createArrow(world, ammo, player);

                        arrow1 = customArrow(arrow1);
                        arrow2 = customArrow(arrow2);
                        arrow3 = customArrow(arrow3);

                        arrow1.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 3.0F, 1.0F);
                        arrow2.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 3.0F, 1.0F);
                        arrow3.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 3.0F, 1.0F);

                        arrow2.setDeltaMovement(arrow2.getDeltaMovement().add(0.1D, 0.0D, 0.1D));
                        arrow3.setDeltaMovement(arrow3.getDeltaMovement().add(-0.1D, 0.0D, -0.1D));

                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));

                        world.addFreshEntity(arrow1);
                        world.addFreshEntity(arrow2);
                        world.addFreshEntity(arrow3);
                    }

                    world.playSound((ServerPlayer)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
                    if (!infiniteAmmo && !player.getAbilities().instabuild) {
                        ammo.shrink(3);
                        if (ammo.isEmpty()) {
                            player.getInventory().removeItem(ammo);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }
}
