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
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import encountermod.patches.RefreshPatch;
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
    public static Texture refreshImg;
    public static int ideaCount;
    public static Hitbox ideaHb;
    public static String[] TEXT;
    public static int prob;

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
        refreshImg = ImageMaster.loadImage("resources/encountermod/images/ui/Refresh.png");
        ideaHb = new Hitbox(140.0F * Settings.scale, 64.0F * Settings.scale);
        float ICON_W = 64.0F * Settings.scale;
        float ICON_Y = Settings.HEIGHT - ICON_W;
        ideaHb.move(Settings.WIDTH - 670.0F * Settings.scale, ICON_Y + ICON_W / 2.0F);
        UIStrings uiString = CardCrawlGame.languagePack.getUIString("encountermod:panel");
        TEXT = uiString.TEXT;
        RefreshPatch.init();
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
        // common.
        BaseMod.addRelic(new BagOfIdeas(), RelicType.SHARED);

        // uncommon.
        BaseMod.addRelic(new RevenantRemains(), RelicType.SHARED);

        // rare.
        BaseMod.addRelic(new SpiritHunterEarl(), RelicType.SHARED);

        // event.
        BaseMod.addRelic(new GraffitiOfTheEraOfHope(), RelicType.SHARED);
        BaseMod.addRelic(new HatredOfTheEraOfVendetta(), RelicType.SHARED);
        BaseMod.addRelic(new LongingOfTheEraOfDreams(), RelicType.SHARED);
        BaseMod.addRelic(new VisionsOfTheEraOfProsperity(), RelicType.SHARED);
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