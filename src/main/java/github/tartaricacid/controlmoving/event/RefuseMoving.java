package github.tartaricacid.controlmoving.event;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import github.tartaricacid.controlmoving.ControlMoving;
import github.tartaricacid.controlmoving.data.DataMapSelect;
import org.spongepowered.api.data.manipulator.mutable.entity.VelocityData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.text.title.Title;

// 禁止玩家移动的事件
public class RefuseMoving {
    // 阻止玩家移动
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onPlayerMoving(MoveEntityEvent event) {
        // 判定是不是玩家
        if (event.getTargetEntity() instanceof Player) {
            // 强制类型转换
            Player player = (Player) event.getTargetEntity();

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
                    return;
                }

                // 取出里面的数据
                Vector3i pos1 = range.getPos1();
                Vector3i pos2 = range.getPos2();
                String world = range.getWorld();

                // 获取玩家坐标数据
                Vector3d position = player.getPosition();

                // 开始判定范围
                if (((position.getFloorX() <= pos1.getX() && position.getFloorX() >= pos2.getX()) || (position.getFloorX() >= pos1.getX() && position.getFloorX() <= pos2.getX())) &&
                        ((position.getFloorY() <= pos1.getY() && position.getFloorY() >= pos2.getY()) || (position.getFloorY() >= pos1.getY() && position.getFloorY() <= pos2.getY())) &&
                        ((position.getFloorZ() <= pos1.getZ() && position.getFloorZ() >= pos2.getZ()) || (position.getFloorZ() >= pos1.getZ() && position.getFloorZ() <= pos2.getZ())) &&
                        player.getWorld().getName().equals(world)) {

                    // 求所选区域中点
                    int centerX = (pos1.getX() + pos2.getX()) / 2;
                    int centerZ = (pos1.getZ() + pos2.getZ()) / 2;

                    // 求单位向量
                    double x = position.getX() - centerX;
                    double z = position.getZ() - centerZ;
                    double k = Math.sqrt(x * x + z * z);
                    x = x / k;
                    z = z / k;

                    // 警告提醒
                    player.sendTitle(Title.builder()
                            .title(ControlMoving.INSTANCE.getTextTitle())
                            .subtitle(ControlMoving.INSTANCE.getTextSubtitle())
                            .build());

                    // 判定玩家是否骑乘，使用了 MCP 的方法
                    if (((net.minecraft.entity.Entity) player).getRidingEntity() != null) {
                        ((net.minecraft.entity.Entity) player).dismountRidingEntity();
                    }

                    // 将单位向量应用为玩家速度
                    player.offer(player.getOrCreate(VelocityData.class).get().velocity().set(new Vector3d(x, 0, z)));
                }
            }
        }
    }
}
