package dvie.mobCoins.config;

import dev.splityosis.sysengine.actions.Actions;
import dev.splityosis.sysengine.configlib.configuration.Configuration;
import dev.splityosis.sysengine.xseries.XSound;

public class MessageConfig implements Configuration {

    @Field public Actions actions_coinDropped = Actions.builder()
            .sendMessage("&aYou have received &f%amount% &aMobCoins from &f%entity%&a!")
            .playSound(XSound.ENTITY_PLAYER_LEVELUP)
            .build();

    @Field public Actions actions_coinsGiven = Actions.builder()
            .sendMessage("&aYou have sent &f%amount% &aMobCoins to &f%player%&a!")
            .playSound(XSound.ENTITY_PLAYER_LEVELUP)
            .build();

    @Field public Actions actions_coinsReceived = Actions.builder()
            .sendMessage("&aYou have received &f%amount% &aMobCoins from &f%player%&a!")
            .playSound(XSound.ENTITY_PLAYER_LEVELUP)
            .build();

    @Field public Actions actions_reloadSuccess = Actions.builder()
            .sendMessage("&aReloaded all configs!")
            .playSound(XSound.BLOCK_NOTE_BLOCK_PLING)
            .build();

    @Field public Actions actions_playerNotFound = Actions.builder()
            .sendMessage("&cPlayer %player% is not online!")
            .playSound(XSound.BLOCK_NOTE_BLOCK_BASS)
            .build();

    @Field public Actions actions_invalidAmount = Actions.builder()
            .sendMessage("&cAmount must be greater than 0!")
            .playSound(XSound.BLOCK_NOTE_BLOCK_BASS)
            .build();

    @Field public Actions actions_playerRequired = Actions.builder()
            .sendMessage("&cYou must specify a player!")
            .playSound(XSound.BLOCK_NOTE_BLOCK_BASS)
            .build();

    @Field public Actions actions_coinsTaken = Actions.builder()
            .sendMessage("&aYou have taken &f%amount% &aMobCoins from &f%player%&a!")
            .playSound(XSound.ENTITY_PLAYER_LEVELUP)
            .build();

    @Field public Actions actions_coinsRemoved = Actions.builder()
            .sendMessage("&c%amount% MobCoins were taken from you by &f%player%&c!")
            .playSound(XSound.BLOCK_NOTE_BLOCK_BASS)
            .build();

    @Field public Actions actions_insufficientFunds = Actions.builder()
            .sendMessage("&c%player% does not have enough MobCoins (%amount% required)!")
            .playSound(XSound.BLOCK_NOTE_BLOCK_BASS)
            .build();

    @Field public Actions actions_coinsSet = Actions.builder()
            .sendMessage("&aYou have set &f%player%&a's MobCoins to &f%amount%&a!")
            .playSound(XSound.ENTITY_PLAYER_LEVELUP)
            .build();

    @Field public Actions actions_coinsUpdated = Actions.builder()
            .sendMessage("&aYour MobCoins were set to &f%amount% &aby &f%player%&a!")
            .playSound(XSound.ENTITY_PLAYER_LEVELUP)
            .build();

    @Field public Actions actions_coinsReset = Actions.builder()
            .sendMessage("&aYou have reset &f%player%&a's MobCoins to 0!")
            .playSound(XSound.ENTITY_PLAYER_LEVELUP)
            .build();

    @Field public Actions actions_coinsResetReceived = Actions.builder()
            .sendMessage("&cYour MobCoins were reset to 0 by &f%player%&c!")
            .playSound(XSound.BLOCK_NOTE_BLOCK_BASS)
            .build();

    @Field public Actions actions_balanceSelf = Actions.builder()
            .sendMessage("&aYour MobCoins balance: &f%amount%")
            .playSound(XSound.ENTITY_PLAYER_LEVELUP)
            .build();

    @Field public Actions actions_balanceOther = Actions.builder()
            .sendMessage("&a%player%'s MobCoins balance: &f%amount%")
            .playSound(XSound.ENTITY_PLAYER_LEVELUP)
            .build();
}
