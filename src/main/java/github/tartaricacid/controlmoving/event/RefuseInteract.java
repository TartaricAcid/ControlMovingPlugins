package github.tartaricacid.controlmoving.event;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import github.tartaricacid.controlmoving.ControlMoving;
import github.tartaricacid.controlmoving.data.DataMapSelect;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.title.Title;

public class RefuseInteract {
    // 阻止玩家所有交互事件
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onPlayerInteract(InteractEvent event, @First Player player) {
        // 判定玩家权限
        // bypass 权限
        if (player.hasPermission("control_moving.moving.bypass")) {
            return;
        }

        // 遍历圈地数据
        for (String name : ControlMoving.dataMap.keySet()) {
            // 获取范围数据
            DataMapSelect range = ControlMoving.dataMap.get(name);

            // 先判定玩家优先级权限
            int i = range.getPriority();
            if (player.hasPermission("control_moving.moving." + i)) {
                continue;
            }

            // 取出里面的数据
            Vector3i pos1 = range.getPos1();
            Vector3i pos2 = range.getPos2();
            String world = range.getWorld();

            // 考虑到玩家交互的距离可以延伸5格，故各个方向加5，防止边界问题
            /* 貌似实现有点问题
            Vector3i unitPos1 = pos1.sub(pos2).div(pos1.sub(pos2).length());
            Vector3i unitPos2 = pos2.sub(pos1).div(pos2.sub(pos1).length());
            pos1 = pos1.add(unitPos1.mul(5));
            pos2 = pos2.add(unitPos2.mul(5));
            */

            // 获取玩家坐标数据
            Vector3d position = player.getPosition();

            // 开始判定范围
            if (((position.getFloorX() <= pos1.getX() && position.getFloorX() >= pos2.getX()) || (position.getFloorX() >= pos1.getX() && position.getFloorX() <= pos2.getX())) &&
                    ((position.getFloorY() <= pos1.getY() && position.getFloorY() >= pos2.getY()) || (position.getFloorY() >= pos1.getY() && position.getFloorY() <= pos2.getY())) &&
                    ((position.getFloorZ() <= pos1.getZ() && position.getFloorZ() >= pos2.getZ()) || (position.getFloorZ() >= pos1.getZ() && position.getFloorZ() <= pos2.getZ())) &&
                    player.getWorld().getName().equals(world)) {

                // 发送警告
                player.sendTitle(Title.builder()
                        .title(ControlMoving.INSTANCE.getTextTitle())
                        .subtitle(ControlMoving.INSTANCE.getTextSubtitle())
                        .build());

                // 取消事件
                event.setCancelled(true);
            }
        }
    }
}
