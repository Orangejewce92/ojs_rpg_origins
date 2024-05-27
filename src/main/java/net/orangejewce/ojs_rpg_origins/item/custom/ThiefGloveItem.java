package net.orangejewce.ojs_rpg_origins.item.custom;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.orangejewce.ojs_rpg_origins.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ThiefGloveItem extends Item {

    private static final double STEAL_CHANCE = 0.5; // 50% chance to steal
    private static final int COOLDOWN_TICKS = 100; // 5 seconds cooldown (100 ticks)
    private static final List<ItemStack> STEALABLE_ITEMS = Arrays.asList(
            new ItemStack(Items.DIAMOND),
            new ItemStack(Items.EMERALD),
            new ItemStack(Items.GOLD_INGOT),
            new ItemStack(ModItems.SAPPHIRE),
            new ItemStack(Items.BOOK),
            new ItemStack(Items.BOOKSHELF)
    );

    public ThiefGloveItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient && attacker instanceof PlayerEntity player) {
            if (target instanceof net.minecraft.entity.passive.VillagerEntity) {
                interactWithVillager(stack, player, target);
            }
        }
        return super.postHit(stack, target, attacker);
    }

    private void interactWithVillager(ItemStack stack, PlayerEntity player, LivingEntity target) {
        if (player.getItemCooldownManager().isCoolingDown(this)) {
            System.out.println("Item is on cooldown");
            return; // If on cooldown, do nothing
        }

        if (new Random().nextDouble() < STEAL_CHANCE) {
            ItemStack stolenItem = STEALABLE_ITEMS.get(new Random().nextInt(STEALABLE_ITEMS.size())).copy();
            player.getInventory().insertStack(stolenItem);
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 1.0F);
            stack.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand())); // Reduce durability by 1 on success
            player.getItemCooldownManager().set(this, COOLDOWN_TICKS); // Add cooldown
            System.out.println("Successfully stole an item: " + stolenItem);
        } else {
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            player.getItemCooldownManager().set(this, COOLDOWN_TICKS); // Add cooldown even if the steal fails
            System.out.println("Failed to steal an item");
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.thief_glove.details")
                    .styled(style -> style.withColor(TextColor.fromRgb(0x00FF00)).withItalic(true)));
        } else {
            tooltip.add(Text.translatable("tooltip.thief_glove")
                    .styled(style -> style.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
            tooltip.add(Text.translatable("tooltip.info_thief_glove")
                    .styled(style -> style.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            tooltip.add(Text.translatable("tooltip.steal_ability")
                    .styled(style -> style.withColor(TextColor.fromRgb(0xFFD700))));
            tooltip.add(Text.translatable("tooltip.hit_to_steal")
                    .styled(style -> style.withColor(TextColor.fromRgb(0xFF4500)).withItalic(true)));
            tooltip.add(Text.translatable("tooltip.shift_info")
                    .styled(style -> style.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
