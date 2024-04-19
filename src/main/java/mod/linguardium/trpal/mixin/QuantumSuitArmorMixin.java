package mod.linguardium.trpal.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import reborncore.common.powerSystem.RcEnergyTier;
import techreborn.items.armor.QuantumSuitItem;
import techreborn.items.armor.TREnergyArmourItem;

import static mod.linguardium.trpal.QuantumChestplatePAL.flightTick;
import static mod.linguardium.trpal.QuantumChestplatePAL.onUnequip;

@Mixin(QuantumSuitItem.class)
public abstract class QuantumSuitArmorMixin extends TREnergyArmourItem {

	public QuantumSuitArmorMixin(ArmorMaterial material, Type slot, long maxCharge, RcEnergyTier energyTier) {
		super(material, slot, maxCharge, energyTier);
	}

	@ModifyExpressionValue(
			method="tickArmor",
			at= @At(value = "FIELD",
					opcode = Opcodes.GETSTATIC,
					target = "Ltechreborn/config/TechRebornConfig;quantumSuitEnableFlight:Z",
					remap = false)
	)
	private boolean redirectFlightLogic(boolean flightConfigurationEnabled, ItemStack stack, PlayerEntity player) {
		flightTick(flightConfigurationEnabled, player,stack,this);
		// disable original code
		return false;
	}


	@ModifyExpressionValue(
			method="onRemoved",
			at= @At(value = "FIELD",
					opcode = Opcodes.GETSTATIC,
					target = "Ltechreborn/config/TechRebornConfig;quantumSuitEnableFlight:Z",
					remap = false)
	)
	private boolean removeFlightAbilityOnUnequip(boolean quantumSuitEnableFlight, PlayerEntity player) {
		if (this.getSlotType() == EquipmentSlot.CHEST) {
			onUnequip(player);
			// disable original code
		}
		return false;
	}
}