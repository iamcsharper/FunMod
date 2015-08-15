package ru.XuPoH.funmod;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;

public class PlayerEvents {
	static class ArmorStatus {
		private ItemStack stack;
		private int lastDamage;

		public ArmorStatus(ItemStack stack, int lastDamage) {
			this.stack = stack;
			this.lastDamage = lastDamage;
		}

		public ItemStack getStack() {
			return this.stack;
		}

		public ItemArmor getArmor() {
			if (this.stack == null) {
				return null;
			}

			return (ItemArmor) this.stack.getItem();
		}

		public int getLastDamage() {
			return this.lastDamage;
		}

		public void setLastDamage(int ld) {
			this.lastDamage = ld;
		}

		public void setItemStack(ItemStack stack) {
			this.stack = stack;
		}
	}

	private Map<EntityPlayer, ArmorStatus[]> statuses = new HashMap<EntityPlayer, ArmorStatus[]>();

	@SubscribeEvent
	public void onPlayerJoined(PlayerLoggedInEvent event) {
		statuses.put(event.player, new ArmorStatus[4]);

		System.out.println("Joined player, put into array! Now stored: "
				+ statuses.size());
	}

	@SubscribeEvent
	public void onPlayerQuit(PlayerLoggedOutEvent event) {
		statuses.remove(event.player);

		System.out.println("Player left, remove from array! Now stored: "
				+ statuses.size());
	}

	@SubscribeEvent
	public void tickPlayer(PlayerTickEvent event) {
		if (event.side == Side.SERVER) {
			EntityPlayer player = event.player;

			if (player != null) {
				ItemStack[] items = player.inventory.armorInventory;
				ArmorStatus[] localStatuses = new ArmorStatus[4];

				for (int i = 0; i < items.length; ++i) {
					ItemStack current = items[i];

					int damage = -1;
					if (current != null) {
						damage = current.getMaxDamage()
								- current.getItemDamage();
					}

					localStatuses[i] = new ArmorStatus(current, damage);
				}

				statuses.put(player, localStatuses);

				checkStatus(player);
			}
		}
	}

	private void checkStatus(EntityPlayer player) {
		ArmorStatus[] stats = statuses.get(player);

		for (int i = 0; i < stats.length; ++i) {
			ArmorStatus status = stats[i];

			if (status.lastDamage != -1)
				System.out.println("Armor with #" + i + " has " + status.lastDamage + " (player " + player.getDisplayName());

			if (status.getArmor() != null) {
				ItemStack stack = status.getStack();
				status.setLastDamage(stack.getMaxDamage() - stack.getItemDamage());

				if (status.getLastDamage() == 0) {
					if (i == 0) {
						onFeetBroken(player, status.getArmor());
					} else if (i == 1) {
						onLegsBroken(player, status.getArmor());
					} else if (i == 2) {
						onBodyBroken(player, status.getArmor());
					} else if (i == 3) {
						onHelmetBroken(player, status.getArmor());
					}
				}
			} else {
				status.setLastDamage(-1);
			}

			stats[i] = status;
		}
	}

	private void onHelmetBroken(EntityPlayer player, ItemArmor armor) {

	}

	private void onBodyBroken(EntityPlayer player, ItemArmor armor) {

	}

	private void onLegsBroken(EntityPlayer player, ItemArmor armor) {
		player.playSound(Main.MODID + ":shtany", 1.0F, 1.0F);
	}

	private void onFeetBroken(EntityPlayer player, ItemArmor armor) {

	}
}
