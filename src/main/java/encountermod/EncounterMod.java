package encountermod;

import basemod.*;
import basemod.eventUtil.AddEventParams;
import basemod.eventUtil.EventUtils;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import encountermod.events.*;
import encountermod.relics.*;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpireInitializer
public class EncounterMod implements EditKeywordsSubscriber, EditRelicsSubscriber, EditStringsSubscriber, PostBattleSubscriber, PostInitializeSubscriber, PostDungeonInitializeSubscriber, AddCustomModeModsSubscriber, OnStartBattleSubscriber, OnPlayerLoseBlockSubscriber, RelicGetSubscriber {

    private static final Logger logger = LogManager.getLogger(EncounterMod.class.getName());
    public static Texture ideaImg;
    public static int ideaCount;
    public static final Hitbox ideaHb = new Hitbox(210.0F * Settings.scale, 64.0F * Settings.scale);

    public EncounterMod() {
        BaseMod.subscribe(this);
    }

    @SuppressWarnings("unused")
    public static void initialize() {
        new EncounterMod();
    }

    @Override
    public void receivePostInitialize() {
        ideaImg = ImageMaster.loadImage("resources/encountermod/images/ui/Idea.png");
        initializeEvents();
    }

    private void initializeEvents() {
        BaseMod.addEvent(new AddEventParams.Builder(Encounter.ID, Encounter.class).
                eventType(EventUtils.EventType.ONE_TIME).
                dungeonID("TheCity").
                endsWithRewardsUI(false).
                create());
    }

    @Override
    public void receivePostDungeonInitialize() {}

    @Override
    public void receiveCustomModeMods(List<CustomMod> modList) {
    }

    @Override
    public int receiveOnPlayerLoseBlock(int cnt) {
        return cnt;
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom room) {}

    @Override
    public void receivePostBattle(final AbstractRoom p0) {}

    @Override
    public void receiveRelicGet(AbstractRelic r) {}

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();

        String keywordStrings = Gdx.files.internal("resources/encountermod/strings/" + getLang() + "/keywords.json").readString(String.valueOf(StandardCharsets.UTF_8));
        Type typeToken = new TypeToken<Map<String, Keyword>>() {}.getType();

        Map<String, Keyword> keywords = gson.fromJson(keywordStrings, typeToken);

        keywords.forEach((k,v)->{
            logger.info("Adding Keyword - " + v.NAMES[0]);
            BaseMod.addKeyword("rhinemod:", v.PROPER_NAME, v.NAMES, v.DESCRIPTION);
        });
    }

    @Override
    public void receiveEditRelics() {
        // event.
        BaseMod.addRelic(new GraffitiOfTheEraOfHope(), RelicType.SHARED);
        BaseMod.addRelic(new HatredOfTheEraOfVendetta(), RelicType.SHARED);
        BaseMod.addRelic(new LongingOfTheEraOfDreams(), RelicType.SHARED);
    }

    @Override
    public void receiveEditStrings() {
        String lang = getLang();
        String relicStrings = Gdx.files.internal("resources/encountermod/strings/" + lang + "/relics.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
        String eventStrings = Gdx.files.internal("resources/encountermod/strings/" + lang + "/events.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(EventStrings.class, eventStrings);
        String uiStrings = Gdx.files.internal("resources/encountermod/strings/" + lang + "/ui.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
    }

    private String getLang() {
        String lang = "eng";
        if (Settings.language == Settings.GameLanguage.ZHS || Settings.language == Settings.GameLanguage.ZHT) {
            lang = "zhs";
        }
        return lang;
    }
}