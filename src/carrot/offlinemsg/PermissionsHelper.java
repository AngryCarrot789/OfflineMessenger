package carrot.offlinemsg;

import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsHelper {
    public static boolean HasPermission(Player player, String permission) {
        return PermissionsEx.getPermissionManager().has(player, permission);
    }
}
