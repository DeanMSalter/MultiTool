package me.darkolythe.multitool;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static me.darkolythe.multitool.MultitoolInventory.giveMultitool;

public class MultitoolCommand implements CommandExecutor {

	private Multitool main = Multitool.getInstance();

    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("multitool.command")) {
				if (cmd.getName().equalsIgnoreCase("Multitool")) {
					if (args.length == 1) {

						if (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("O")) {
                            if (!main.multitoolutils.allowedWorld(player.getWorld())){
                                sender.sendMessage(main.prefix + ChatColor.RED + "MultiTool is not allowed in this world!");
                                return true;
                            }
							player.openInventory(main.multitoolutils.getToolInv(player));
						} else if (args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("T")) {
                            if (!main.multitoolutils.allowedWorld(player.getWorld())){
                                sender.sendMessage(main.prefix + ChatColor.RED + "MultiTool is not allowed in this world!");
                                return true;
                            }
							main.multitoolutils.setToggle(player.getUniqueId(), !main.multitoolutils.getToggle(player.getUniqueId()));
							if (main.multitoolutils.getToggle(player.getUniqueId())) {
								sender.sendMessage(main.prefix + ChatColor.GREEN + "Multitool On!");
							} else {
								sender.sendMessage(main.prefix + ChatColor.GREEN + "Multitool Off!");
							}
						} else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")) {
                            if (!main.multitoolutils.allowedWorld(player.getWorld())){
                                sender.sendMessage(main.prefix + ChatColor.RED + "MultiTool is not allowed in this world!");
                                return true;
                            }
							giveMultitool(main, player);
						} else if (args[0].equalsIgnoreCase("reload")) {
							if (player.hasPermission("multitool.reload")) {
								main.multitoolutils.reload();
								player.sendMessage(main.prefix + ChatColor.GREEN + "The config has been reloaded");
							} else {
								sender.sendMessage(main.prefix + ChatColor.RED + "You do not have permission to do that!");
							}
						} else if (args[0].equalsIgnoreCase("generate")) {
                            if (!main.multitoolutils.allowedWorld(player.getWorld())){
                                sender.sendMessage(main.prefix + ChatColor.RED + "MultiTool is not allowed in this world!");
                                return true;
                            }
                            if (player.hasPermission("multitool.generate")) {
                                Inventory multitoolInv = main.toolinv.get(player.getUniqueId());


                                Inventory playerInventory = player.getInventory();
                                List<ItemStack> pickaxes = new ArrayList<>();
                                List<ItemStack> shovels = new ArrayList<>();
                                List<ItemStack> axes = new ArrayList<>();
                                List<ItemStack> swords = new ArrayList<>();

                                for(int i = 0; i < 9; i++) {
                                    ItemStack item = player.getInventory().getItem(i);
                                    if(item == null) continue;

                                    // Do something with item
                                }
                                for(int i = 0; i < 9; i++) {
                                    ItemStack item = player.getInventory().getItem(i);
                                    if (item == null) {
                                        continue;
                                    }
                                    if (item.getType().toString().contains("PICKAXE")) {
                                        pickaxes.add(item);
                                    }else if (item.getType().toString().contains("SHOVEL")) {
                                        shovels.add(item);
                                    }else if (item.getType().toString().contains("AXE")) {
                                        axes.add(item);
                                    }else if (item.getType().toString().contains("SWORD")) {
                                        swords.add(item);
                                    }
                                }

                                for (ItemStack item : playerInventory.getContents()) {
                                    if (item == null) {
                                        continue;
                                    }
                                    if (item.getType().toString().contains("PICKAXE")) {
                                        if (pickaxes.isEmpty()){
                                            pickaxes.add(item);
                                        }
                                    }else if (item.getType().toString().contains("SHOVEL")) {
                                        if (shovels.isEmpty()){
                                            shovels.add(item);
                                        }
                                    }else if (item.getType().toString().contains("AXE")) {
                                        if(axes.isEmpty()){
                                            axes.add(item);
                                        }
                                    }else if (item.getType().toString().contains("SWORD")) {
                                        if(swords.isEmpty()){
                                            swords.add(item);
                                        }
                                    }
                                }
                                ItemStack sword = getBestTool(swords);
                                ItemStack pickaxe = getBestTool(pickaxes);
                                ItemStack axe = getBestTool(axes);
                                ItemStack shovel = getBestTool(shovels);

                                if (sword != null && multitoolInv.getItem(0).getType() == Material.GRAY_STAINED_GLASS_PANE) {
                                    multitoolInv.setItem(0, sword);
                                    playerInventory.remove(sword);
                                }
                                if (pickaxe != null && multitoolInv.getItem(1).getType() == Material.GRAY_STAINED_GLASS_PANE){
                                    multitoolInv.setItem(1, pickaxe);
                                    playerInventory.remove(pickaxe);
                                }
                                if (axe != null && multitoolInv.getItem(2).getType() == Material.GRAY_STAINED_GLASS_PANE){
                                    multitoolInv.setItem(2, axe);
                                    playerInventory.remove(axe);
                                }
                                if (shovel != null && multitoolInv.getItem(3).getType() == Material.GRAY_STAINED_GLASS_PANE){
                                    multitoolInv.setItem(3, shovel);
                                    playerInventory.remove(shovel);
                                }

                                player.sendMessage(main.prefix + ChatColor.GREEN + "Added tools to your multitool!");
                                giveMultitool(main, player);
                            } else {
                                sender.sendMessage(main.prefix + ChatColor.RED + "You do not have permission to do that!");
                            }
                        }else if (args[0].equalsIgnoreCase("empty")) {
                            if (!main.multitoolutils.allowedWorld(player.getWorld())){
                                sender.sendMessage(main.prefix + ChatColor.RED + "MultiTool is not allowed in this world!");
                                return true;
                            }


                            if (player.hasPermission("multitool.empty")) {
                                Inventory multitoolInv = main.toolinv.get(player.getUniqueId());
                                Inventory playerInventory = player.getInventory();

                                Optional<ItemStack> multitool = Arrays.stream(playerInventory.getStorageContents())
                                        .filter(item -> item != null && main.multitoolutils.isTool(item))
                                        .findFirst();

                                multitool.ifPresent(playerInventory::remove);

                                ItemStack[] slots = new ItemStack[]{
                                        multitoolInv.getItem(0),
                                        multitoolInv.getItem(1),
                                        multitoolInv.getItem(2),
                                        multitoolInv.getItem(3)
                                };

                                for (int i = 0; i < slots.length; i++) {
                                    ItemStack slot = slots[i];
                                    if (slot.getType() != Material.GRAY_STAINED_GLASS_PANE) {
                                        if (playerInventory.firstEmpty() != -1){
                                            playerInventory.addItem(slot);
                                            multitoolInv.setItem(i, main.placeholders.get(i));
                                        }
                                    }
                                }
                                player.sendMessage(main.prefix + ChatColor.GREEN + "Added tools from your multitool back into your inventory where there was space!");

                            } else {
                                sender.sendMessage(main.prefix + ChatColor.RED + "You do not have permission to do that!");
                            }
                        }
                        else {
							sender.sendMessage(main.prefix + ChatColor.RED + "Invalid Arguments: /mt [open, toggle, create]");
						}
					} else if (args.length == 2 && (args[0].equalsIgnoreCase("Open") || args[0].equalsIgnoreCase("O"))) {
						if (player.hasPermission("multitool.useothers")) {
							for (Player players : Bukkit.getServer().getOnlinePlayers()) {
								if (args[1].equalsIgnoreCase(players.getName())) {
									player.openInventory(main.multitoolutils.getToolInv(players));
									return true;
								}
							}
						} else {
							sender.sendMessage(main.prefix + ChatColor.RED + "You do not have permission to do that!");
						}
						sender.sendMessage(main.prefix + ChatColor.RED + "Player is not online");
					} else {
						sender.sendMessage(main.prefix + ChatColor.RED + "Invalid Arguments: /mt [open, toggle, create]");
					}
				}
			} else {
				sender.sendMessage(main.prefix + ChatColor.RED + "You do not have permission to do that!");
			}
		}
		return true;
	}

    private ItemStack getBestTool(List<ItemStack> tools){
        ItemStack bestTool = null;
        for (ItemStack tool : tools) {
            if (tool.getType().toString().contains("NETHERITE")){
                bestTool = tool;
            } else if (tool.getType().toString().contains("DIAMOND")) {
                if (bestTool == null || (!bestTool.getType().toString().contains("NETHERITE"))){
                    bestTool = tool;
                }
            } else if (tool.getType().toString().contains("IRON")) {
                if (bestTool == null || (!bestTool.getType().toString().contains("DIAMOND")
                        && !bestTool.getType().toString().contains("NETHERITE"))){
                    bestTool = tool;
                }
            } else if (tool.getType().toString().contains("STONE")) {
                if (bestTool == null || (!bestTool.getType().toString().contains("DIAMOND")
                        && !bestTool.getType().toString().contains("NETHERITE")
                        && !bestTool.getType().toString().contains("IRON"))){
                    bestTool = tool;
                }
            } else if (tool.getType().toString().contains("WOODEN")) {
                if (bestTool == null || (!bestTool.getType().toString().contains("DIAMOND") &&
                    !bestTool.getType().toString().contains("NETHERITE") &&
                    !bestTool.getType().toString().contains("IRON") &&
                    !bestTool.getType().toString().contains("STONE"))){
                    bestTool = tool;
                }
            }else {
                bestTool = tool;
            }
        }
        return bestTool;
    }
}
