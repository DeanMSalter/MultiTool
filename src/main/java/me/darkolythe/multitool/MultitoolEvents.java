package me.darkolythe.multitool;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class MultitoolEvents implements Listener {

	private Multitool main;
	public MultitoolEvents(Multitool plugin) {
		this.main = plugin; // set it equal to an instance of main
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////Make sure player isnt removing MT from inv
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Item dropitem = event.getItemDrop();
		ItemStack dropstack = dropitem.getItemStack();
		if (main.multitoolutils.isTool(dropstack)) {
			dropitem.remove();
			event.getPlayer().sendMessage(main.prefix + ChatColor.RED + "You dropped your Multitool!");
		}
	}
	
	@EventHandler
	public void onItemBreak(PlayerItemBreakEvent event) {
		ItemStack brokenitem = event.getBrokenItem();
		if (main.multitoolutils.isTool(brokenitem)) {
			Inventory mtinv = main.multitoolutils.getToolInv(event.getPlayer()); //create inventory of mtinv
			for (int i = 0; i < 4; i++) {
				if (mtinv.getItem(i).getType() == brokenitem.getType()) {
					mtinv.setItem(i, main.placeholders.get(i));
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryCheck(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if ((event.getClickedInventory() != player.getInventory()) || (event.isShiftClick())) {
			if ((player.getItemOnCursor().getType() != Material.AIR)) {
				ItemStack cursorstack = player.getItemOnCursor();
				if (main.multitoolutils.isTool(cursorstack)) {
					player.setItemOnCursor(null);
					event.setCancelled(true);
					player.sendMessage(main.prefix + ChatColor.RED + "You removed your Multitool!");
				}
			} else if (event.isShiftClick() && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				ItemStack clickstack = event.getCurrentItem();
				if (main.multitoolutils.isTool(clickstack)) {
					event.setCurrentItem(null);
					event.setCancelled(true);
					player.sendMessage(main.prefix + ChatColor.RED + "You removed your Multitool!");
				}
			} else if (event.getClick() == ClickType.NUMBER_KEY) {
				ItemStack item = player.getInventory().getItem(event.getHotbarButton());
				if (item != null && item.getType() != Material.AIR) {
					if (main.multitoolutils.isTool(item)) {
						player.getInventory().setItem(event.getHotbarButton(), null);
						event.setCancelled(true);
						player.sendMessage(main.prefix + ChatColor.RED + "You removed your Multitool!");
					}
				}
			} else if (main.multitoolutils.isTool(event.getCurrentItem())) {
				event.setCurrentItem(null);
				event.setCancelled(true);
				player.sendMessage(main.prefix + ChatColor.RED + "Your Multitool was outside your inventory. It has been removed!");
			}
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getOldCursor().getType() != Material.AIR) {
			if (event.getInventory().getType() != InventoryType.PLAYER) {
				ItemStack clickstack = event.getOldCursor();
				if (main.multitoolutils.isTool(clickstack)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Inventory playerInventory = player.getInventory();
        Optional<ItemStack> multitool = Arrays.stream(playerInventory.getStorageContents())
                .filter(item -> item != null && main.multitoolutils.isTool(item))
                .findFirst();

        if (multitool.isPresent()) {
            playerInventory.remove(multitool.get());
            if (!main.dropondeath) {
                if (main.multitoolutils.allowedWorld(player.getWorld())){
                    player.sendMessage(main.prefix + ChatColor.RED + "You died, so your Multitool was put away!");
                }
            }
        }
        if (!main.multitoolutils.allowedWorld(player.getWorld())){
            return;
        }

        if (main.toolinv.containsKey(player.getUniqueId())) {
            if (!event.getKeepInventory()) {
                for (ItemStack i : main.toolinv.get(player.getUniqueId()).getContents()) {
                    if (i.getType() != Material.FEATHER && i.getType() != Material.GRAY_STAINED_GLASS_PANE) {
                        long inventorySize = Arrays.stream(playerInventory.getStorageContents())
                                .filter(item -> item != null && item.getType() != Material.AIR)
                                .count();

                        if (playerInventory.getStorageContents().length == inventorySize){
                            ItemStack lastItem;
                            for (int o = 34; o >= 0; o--) {
                                lastItem = playerInventory.getStorageContents()[o - 1];
                                if (lastItem == null){
                                    continue;
                                }
                                if (!isImportantItem(lastItem)){
                                    event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), lastItem);
                                    playerInventory.clear(o - 1);
                                    break;
                                }
                            }
                        }
                        playerInventory.addItem(i);
                    }
                }
                event.getDrops().clear();
                event.getDrops().addAll(Arrays.asList(playerInventory.getContents()));

                main.toolinv.remove(player.getUniqueId());
                main.multitoolutils.getToolInv(player);
            }
        }

	}
    private boolean isImportantItem(ItemStack item){
        if (item instanceof Damageable) {
            return true;
        }
        if (item.getType().isEdible()){
            return true;
        }
        if (item.getType().equals(Material.TOTEM_OF_UNDYING)){
            return true;
        }
        if(item.getItemMeta() instanceof BlockStateMeta) {
            BlockStateMeta im = (BlockStateMeta) item.getItemMeta();
            if (im.getBlockState() instanceof ShulkerBox) {
                return true;
            }
        }

        Set<Material> importantItems = new HashSet<>();
        importantItems.add(Material.BEACON);
        importantItems.add(Material.DIAMOND);
        importantItems.add(Material.DIAMOND_BLOCK);
        importantItems.add(Material.IRON_INGOT);
        importantItems.add(Material.IRON_BLOCK);
        importantItems.add(Material.GOLD_INGOT);
        importantItems.add(Material.GOLD_BLOCK);
        importantItems.add(Material.EMERALD);
        importantItems.add(Material.EMERALD_BLOCK);
        importantItems.add(Material.LAPIS_LAZULI);
        importantItems.add(Material.LAPIS_BLOCK);
        importantItems.add(Material.REDSTONE);
        importantItems.add(Material.REDSTONE_BLOCK);
        importantItems.add(Material.TNT);
        importantItems.add(Material.NETHER_STAR);
        importantItems.add(Material.ENCHANTED_GOLDEN_APPLE);
        importantItems.add(Material.TOTEM_OF_UNDYING);

        return importantItems.contains(item.getType());
    }

	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		ItemStack handitem = player.getInventory().getItemInMainHand();
		if (main.multitoolutils.isTool(handitem)) {
            if (!main.multitoolutils.allowedWorld(player.getWorld())){
                event.getPlayer().getInventory().remove(handitem);
                event.getPlayer().sendMessage(main.prefix + ChatColor.RED + "MultiTool is not allowed in this world!");
                return;
            }
			Entity ent = event.getRightClicked();
			if (ent.getType() == EntityType.ITEM_FRAME) {
				if (((ItemFrame)ent).getItem().getType() == Material.AIR) {
					event.setCancelled(true);
					player.sendMessage(main.prefix + ChatColor.RED + "You cannot give your multitool to item frames!");
				}
			} else if (ent.getType() == EntityType.ARMOR_STAND) {
				if (((ArmorStand)ent).getItemInHand().getType() == Material.AIR) {
					event.setCancelled(true);
					player.sendMessage(main.prefix + ChatColor.RED + "You cannot give your multitool to armour stands!");
				}
			}
		}
	}
}
