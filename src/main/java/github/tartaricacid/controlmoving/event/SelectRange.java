package github.tartaricacid.controlmoving.event;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.HashMap;

import static github.tartaricacid.controlmoving.ControlMoving.tmpSelectMap;

// 选择范围的事件
public class SelectRange {
    // 范围数据，属于临时变量
    private HashMap<String, Object> rangeIn = new HashMap<>();

    // 玩家羽毛左键方块
    // TODO：以后计划增加配置文件，自定义圈地工具
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onPlayerInteractBlockLeft(InteractBlockEvent.Primary.MainHand event, @First Player player) {
        // 如果玩家没有对应权限，直接返回
        if (!player.hasPermission("control_moving.command.main")) {
            return;
        }

        // 获取基本信息，包括打的方块，玩家手持物品
        final BlockSnapshot clickedBlock = event.getTargetBlock();
        final ItemStack itemInHand = player.getItemInHand(event.getHandType()).orElse(ItemStack.empty());

        // 开始判定方块，玩家手持物品
        if (clickedBlock != BlockSnapshot.NONE && !itemInHand.isEmpty() && itemInHand.getType().equals(ItemTypes.FEATHER)) {
            // 获取玩家坐标
            rangeIn.put("world", player.getWorld().getName());
            rangeIn.put("pos1", clickedBlock.getPosition());

            // 往临时变量中存入坐标、世界信息
            tmpSelectMap.put(player.getUniqueId(), rangeIn);

            // 发送客户端消息
            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(String.format("&r&l[&b&lControl Moving&r&l]&e&l pos1 &a&l(%d,%d,%d) &e&l已经选定",
                    clickedBlock.getPosition().getX(), clickedBlock.getPosition().getY(), clickedBlock.getPosition().getZ())));

            // 取消左键事件
            event.setCancelled(true);
        }
    }

    // 玩家羽毛左键方块
    // TODO：以后计划增加配置文件，自定义圈地工具
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onPlayerInteractBlockRight(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
        if (!player.hasPermission("control_moving.command.main")) {
            return;
        }

        final BlockSnapshot clickedBlock = event.getTargetBlock();
        final ItemStack itemInHand = player.getItemInHand(event.getHandType()).orElse(ItemStack.empty());

        if (clickedBlock != BlockSnapshot.NONE && !itemInHand.isEmpty() && itemInHand.getType().equals(ItemTypes.FEATHER)) {
            rangeIn.put("world", player.getWorld().getName());
            rangeIn.put("pos2", clickedBlock.getPosition());

            tmpSelectMap.put(player.getUniqueId(), rangeIn);

            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(String.format("&r&l[&b&lControl Moving&r&l]&e&l pos2 &a&l(%d,%d,%d) &e&l已经选定",
                    clickedBlock.getPosition().getX(), clickedBlock.getPosition().getY(), clickedBlock.getPosition().getZ())));

            event.setCancelled(true);
        }
    }
}
