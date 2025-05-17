package dvie.mobCoins.config;

import dev.splityosis.sysengine.actions.Actions;
import dev.splityosis.sysengine.configlib.configuration.Configuration;
import dev.splityosis.sysengine.xseries.XSound;

public class MessageConfig implements Configuration {

    @Field public Actions actions_coinDropped = Actions.builder()
            .sendMessage("&aYou have received &f%amount% &aMobCoins from &f%entity%&a!")
            .playSound(XSound.ENTITY_PLAYER_LEVELUP)
            .build();
}
