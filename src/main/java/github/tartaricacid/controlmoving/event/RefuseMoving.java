package github.tartaricacid.controlmoving.event;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import github.tartaricacid.controlmoving.ControlMoving;
import github.tartaricacid.controlmoving.data.DataMapSelect;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;

import java.util.HashMap;

// 禁止玩家移动的事件
// TODO：增加权限判定
public class RefuseMoving {
    // 阻止玩家移动
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onPlayerMoving(MoveEntityEvent event) {
        // 首先先判定是不是玩家
        if (event.getTargetEntity() instanceof Player) {
            // 强制类型转换
            Player player = (Player) event.getTargetEntity();

            // 遍历圈地数据
            for (String name : ControlMoving.dataMap.keySet()) {

                // 获取范围数据
                DataMapSelect range = ControlMoving.dataMap.get(name);

                // 取出里面的数据
                Vector3i pos1 = range.getPos1();
                Vector3i pos2 = range.getPos2();
                String world = range.getWorld();

                // 获取玩家坐标数据
                Vector3d position = player.getPosition();

                // 开始判定范围
                if (((position.getFloorX() < pos1.getX() && position.getFloorX() > pos2.getX()) || (position.getFloorX() > pos1.getX() && position.getFloorX() < pos2.getX())) &&
                        ((position.getFloorY() < pos1.getY() && position.getFloorY() > pos2.getY()) || (position.getFloorY() > pos1.getY() && position.getFloorY() < pos2.getY())) &&
                        ((position.getFloorZ() < pos1.getZ() && position.getFloorZ() > pos2.getZ()) || (position.getFloorZ() > pos1.getZ() && position.getFloorZ() < pos2.getZ())) &&
                        player.getWorld().getName().equals(world)) {
                    // TODO：这一块判定还需要优化，目前会卡死玩家，而不是弹开
                    event.setCancelled(true);
                }
            }
        }
    }
}
