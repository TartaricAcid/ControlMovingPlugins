package github.tartaricacid.controlmoving.event;

import com.flowpowered.math.vector.Vector3i;
import github.tartaricacid.controlmoving.ControlMoving;
import github.tartaricacid.controlmoving.data.DataMapSelect;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
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
            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(String.format("&r&l[&b&l保护墙插件&r&l]&e&l pos1 &a&l(%d,%d,%d) &e&l已经选定",
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

            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(String.format("&r&l[&b&l保护墙插件&r&l]&e&l pos2 &a&l(%d,%d,%d) &e&l已经选定",
                    clickedBlock.getPosition().getX(), clickedBlock.getPosition().getY(), clickedBlock.getPosition().getZ())));

            event.setCancelled(true);
        }

        // 玩家手持骨头右击，显示范围
        if (clickedBlock != BlockSnapshot.NONE && !itemInHand.isEmpty() && itemInHand.getType().equals(ItemTypes.BONE)) {
            // 遍历圈地数据
            for (String name : ControlMoving.dataMap.keySet()) {

                // 获取范围数据
                DataMapSelect range = ControlMoving.dataMap.get(name);

                // 取出里面的数据
                Vector3i pos1 = range.getPos1();
                Vector3i pos2 = range.getPos2();
                String world = range.getWorld();
                int priority = range.getPriority();

                // 获取玩家坐标数据
                Vector3i position = clickedBlock.getPosition();

                // 开始判定范围
                if (((position.getX() <= pos1.getX() && position.getX() >= pos2.getX()) || (position.getX() >= pos1.getX() && position.getX() <= pos2.getX())) &&
                        ((position.getY() <= pos1.getY() && position.getY() >= pos2.getY()) || (position.getY() >= pos1.getY() && position.getY() <= pos2.getY())) &&
                        ((position.getZ() <= pos1.getZ() && position.getZ() >= pos2.getZ()) || (position.getZ() >= pos1.getZ() && position.getZ() <= pos2.getZ())) &&
                        player.getWorld().getName().equals(world)) {
                    player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(String.format("&r&l[&b&l保护墙插件&r&l]&e&l 这块地已经被选定：名称为“%s”，等级为 %d", name, priority)));
                }
            }
        }
    }
}
