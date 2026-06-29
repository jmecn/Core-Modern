package su.terrafirmagreg.core.mixins.common.tfc;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;

import net.dries007.tfc.util.AxeLoggingHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

import su.terrafirmagreg.core.TFGCore;

@Mixin(value = AxeLoggingHelper.class, remap = false)
public abstract class AxeLoggingHelperMixin {

    @Inject(method = "doLogging", at = @At("HEAD"), remap = false)
    private static void tfg$doLogging$enter(LevelAccessor level, BlockPos pos, Player player, ItemStack axe,
            CallbackInfo ci) {
        TFGCore.LOGGER.info("[tfg-tree] AxeLoggingHelper.doLogging player={} item={} pos={}",
                player.getGameProfile().getName(), axe.getItem(), pos);
    }

    @Redirect(method = "doLogging", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", remap = true), remap = false)
    private static void tfg$doLogging$hurtAndBreak(ItemStack axe, int amount, Player player,
            Consumer<LivingEntity> onBreak) {
        if (axe.isEmpty()) {
            return;
        }
        if (axe.getItem() instanceof IGTTool gtTool) {
            int damage = gtTool.getToolStats().getToolDamagePerBlockBreak(axe);
            TFGCore.LOGGER.debug("[tfg-tree] axe-logging gt item={} damage={}", axe.getItem(), damage);
            ToolHelper.damageItem(axe, player, damage);
        } else {
            TFGCore.LOGGER.debug("[tfg-tree] axe-logging vanilla item={}", axe.getItem());
            axe.hurtAndBreak(amount, player, onBreak);
        }
    }
}
