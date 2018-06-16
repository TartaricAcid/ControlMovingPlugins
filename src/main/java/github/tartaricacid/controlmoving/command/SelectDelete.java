package github.tartaricacid.controlmoving.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import github.tartaricacid.controlmoving.ControlMoving;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;

import static github.tartaricacid.controlmoving.ControlMoving.dataMap;

public class SelectDelete implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        // 获取一个参数
        // name：领地名称
        String nameIn = args.<String>getOne("name").get();

        // 只有玩家执行指令才行
        if (src instanceof Player) {
            // 强制类型转换
            Player player = (Player) src;

            // 如果存在，删除
            if (ControlMoving.dataMap.containsKey(nameIn)) {
                ControlMoving.dataMap.remove(nameIn);

                // 进行一次文件存储，防止关服数据丢失
                Gson gson = new GsonBuilder().setPrettyPrinting().create();  // Json 进行一次格式化，看起来美观
                Path storageFile = ControlMoving.INSTANCE.getConfigDirectory().resolve("storage.json");

                // 不用 sponge api，因为那个 api 不识别 Vector3i 数据
                try {
                    OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(storageFile.toFile()), "UTF-8");
                    out.write(gson.toJson(dataMap));
                    out.close();

                    // 提醒玩家删除成功
                    player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(String.format("&r&l[&b&l保护墙插件&r&l] &c&l%s保护区域被删除！", nameIn)));

                    // 指令成功
                    return CommandResult.success();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 不存在，警告！
            else {
                player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&r&l[&b&l保护墙插件&r&l] &c&l不存在此名称的保护区域！"));
                return CommandResult.empty();
            }
        }

        // 否则，指令只是执行过
        return CommandResult.empty();
    }
}
