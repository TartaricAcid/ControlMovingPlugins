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

import java.util.HashMap;

import static github.tartaricacid.controlmoving.ControlMoving.tmpSelectMap;

// 选择范围的事件
// TODO：增加权限判定
public class SelectRange {
    // 范围数据，属于临时变量
    private HashMap<String, Object> rangeIn = new HashMap<>();

    // 玩家羽毛左键方块
    // TODO：以后计划增加配置文件，自定义圈地工具
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onPlayerInteractBlockLeft(InteractBlockEvent.Primary.MainHand event, @First Player player) {
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
            // TODO：自定义语言和样式
            player.sendMessage(Text.builder(String.format("[ControlMoving] pos1 - (%d, %d, %d) 已经选好",
                    clickedBlock.getPosition().getX(), clickedBlock.getPosition().getY(), clickedBlock.getPosition().getZ()))
                    .color(TextColors.GREEN)
                    .style(TextStyles.BOLD)
                    .build());

            // 取消左键事件
            event.setCancelled(true);
        }
    }

    // 玩家羽毛左键方块
    // TODO：以后计划增加配置文件，自定义圈地工具
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onPlayerInteractBlockRight(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
        final BlockSnapshot clickedBlock = event.getTargetBlock();
        final ItemStack itemInHand = player.getItemInHand(event.getHandType()).orElse(ItemStack.empty());

        if (clickedBlock != BlockSnapshot.NONE && !itemInHand.isEmpty() && itemInHand.getType().equals(ItemTypes.FEATHER)) {
            rangeIn.put("world", player.getWorld().getName());
            rangeIn.put("pos2", clickedBlock.getPosition());

            tmpSelectMap.put(player.getUniqueId(), rangeIn);

            // TODO：自定义语言和样式
            player.sendMessage(Text.builder(String.format("[ControlMoving] pos2 - (%d, %d, %d) 已经选好",
                    clickedBlock.getPosition().getX(), clickedBlock.getPosition().getY(), clickedBlock.getPosition().getZ()))
                    .color(TextColors.GREEN)
                    .style(TextStyles.BOLD)
                    .build());

            event.setCancelled(true);
        }
    }
}
