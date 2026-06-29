package su.terrafirmagreg.core.mixins.common.fallingtrees;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import me.pandamods.fallingtrees.event.EventHandler;

import su.terrafirmagreg.core.TFGCore;

@Mixin(value = EventHandler.class, remap = false)
public abstract class EventHandlerMixin {

    @Redirect(method = "makeTreeFall(Lme/pandamods/fallingtrees/api/Tree;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/player/Player;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", remap = true), remap = false)
    private static void tfg$makeTreeFall$hurtAndBreak(ItemStack stack, int damage, LivingEntity entity,
            Consumer<LivingEntity> onBreak) {
        if (stack.isEmpty() || damage <= 0) {
            return;
        }
        if (stack.getItem() instanceof IGTTool gtTool) {
            int amount = gtTool.getToolStats().getToolDamagePerBlockBreak(stack) * damage;
            TFGCore.LOGGER.debug("[tfg-tree] falling-trees gt item={} blocks={} damage={}", stack.getItem(), damage, amount);
            ToolHelper.damageItem(stack, entity, amount);
        } else {
            TFGCore.LOGGER.debug("[tfg-tree] falling-trees vanilla item={} blocks={}", stack.getItem(), damage);
            stack.hurtAndBreak(damage, entity, onBreak);
        }
    }
}
