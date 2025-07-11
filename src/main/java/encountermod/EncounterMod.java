package encountermod;

import basemod.*;
import basemod.eventUtil.AddEventParams;
import basemod.eventUtil.EventUtils;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.events.exordium.*;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.beyond.*;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.beyond.Exploder;
import com.megacrit.cardcrawl.monsters.beyond.Repulsor;
import com.megacrit.cardcrawl.monsters.beyond.Spiker;
import com.megacrit.cardcrawl.monsters.exordium.GremlinNob;
import com.megacrit.cardcrawl.monsters.exordium.GremlinThief;
import com.megacrit.cardcrawl.monsters.exordium.GremlinWarrior;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.JuzuBracelet;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import encountermod.cards.Empty;
import encountermod.monsters.Catastrophe;
import encountermod.monsters.QuiLon;
import encountermod.monsters.SpinesOfEpoch;
import encountermod.patches.*;
import encountermod.reward.ExtraRelicReward;
import encountermod.reward.IdeaReward;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import encountermod.events.*;
import encountermod.relics.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.megacrit.cardcrawl.helpers.RelicLibrary.getRelic;

@SpireInitializer
public class EncounterMod implements EditRelicsSubscriber, EditStringsSubscriber, PostBattleSubscriber, PostInitializeSubscriber, PostDungeonInitializeSubscriber, AddCustomModeModsSubscriber, OnStartBattleSubscriber, OnPlayerLoseBlockSubscriber, RelicGetSubscriber {

    private static final Logger logger = LogManager.getLogger(EncounterMod.class.getName());
    public static Texture ideaImg;
    public static Texture refreshImg;
    public static int ideaCount;
    public static Hitbox ideaHb;
    public static String[] TEXT;
    public static int prob;
    public static boolean firstEvent;
    public static boolean isLastOpRefresh;
    public static String MOD_ID = "encountermod";
    public static Random refreshRng;
    public static Random myMapRng;
    private static SpireConfig config;
    public static boolean challengeSpines;
    public static boolean quilonBoss;

    public boolean isDemo = false;

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
        RefreshPatch.initPosition();
        IdeaPatch.topEffect = new ArrayList<>();
        initializeConfigs();
        initializeEvents();
        initializeRewards();
        initializeSpecialBattle();
    }

    private void initializeConfigs() {
        Properties defaultProperties = new Properties();
        defaultProperties.setProperty("challengeSpines", Boolean.toString(false));
        defaultProperties.setProperty("quilonBoss", Boolean.toString(false));
        try {
            config = new SpireConfig("encountermod", "encounterConfig", defaultProperties);
            EncounterMod.challengeSpines = config.getBool("challengeSpines");
            EncounterMod.quilonBoss = config.getBool("quilonBoss");
        } catch (IOException e) {
            e.printStackTrace();
        }

        UIStrings uiString = CardCrawlGame.languagePack.getUIString("encountermod:Config");
        ModPanel settingsPanel = new ModPanel();
        ModLabeledToggleButton challengeSpinesLabel = new ModLabeledToggleButton(uiString.TEXT[0], 380.0F, 740.0F,
                Settings.CREAM_COLOR, FontHelper.charDescFont, EncounterMod.challengeSpines, settingsPanel,
                label -> {},
                button -> {
                    EncounterMod.challengeSpines = button.enabled;
                    config.setString("challengeSpines", Boolean.toString(button.enabled));
                    try {
                        config.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        settingsPanel.addUIElement(challengeSpinesLabel);

        ModLabeledToggleButton quilonBossLabel = new ModLabeledToggleButton(uiString.TEXT[1], 380.0F, 690.0F,
                Settings.CREAM_COLOR, FontHelper.charDescFont, EncounterMod.quilonBoss, settingsPanel,
                label -> {},
                button -> {
                    EncounterMod.quilonBoss = button.enabled;
                    config.setString("quilonBoss", Boolean.toString(button.enabled));
                    try {
                        config.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        settingsPanel.addUIElement(quilonBossLabel);

        BaseMod.registerModBadge(ImageMaster.loadImage("img/BaseModBadge.png"), "encounterMod", "_noname512, absi2011, moranzc", "Settings", settingsPanel);
    }

    private void initializeSpecialBattle() {
        BaseMod.addCard(new Empty());
        BaseMod.addMonster("Qui'Lon", () -> new MonsterGroup(new QuiLon(200.0F, 0.0F)));
        BaseMod.addMonster("Hatred 1", () -> new MonsterGroup(new AbstractMonster[] {new Exploder(-200.0F, 0.0F), new Repulsor(0.0F, 0.0F)}));
        BaseMod.addMonster("Hatred 2", () -> new MonsterGroup(new AbstractMonster[] {new GremlinThief(-600.0F, 0.0F), new GremlinWarrior(-400.0F, 0.0F), new Exploder(-200.0F, 0.0F), new Spiker(0.0F, 0.0F)}));
        BaseMod.addMonster("Hatred 3", () -> new MonsterGroup(new AbstractMonster[] {new Spiker(-600.0F, 0.0F), new Spiker(-400.0F, 0.0F), new GremlinThief(-200.0F, 0.0F), new GremlinNob(0.0F, 0.0F)}));
        BaseMod.addMonster("Catastrophe Fight", () -> new MonsterGroup(new AbstractMonster[] {new Catastrophe(150.0F, 0.0F)}));
        if (quilonBoss) {
            BaseMod.addBoss(TheBeyond.ID, "Qui'Lon", "resources/encountermod/images/ui/quilon.png", "resources/encountermod/images/ui/quilon.png");
        }
    }

    private void initializeRewards() {
        BaseMod.registerCustomReward(
                IdeaRewardPatch.IDEA_REWARD,
                (rewardSave) -> { // this handles what to do when this quest type is loaded.
                    return new IdeaReward();
                },
                (customReward) -> { // this handles what to do when this quest type is saved.
                    return new RewardSave(customReward.type.toString(), null, -1, 0);
                });
        BaseMod.registerCustomReward(
                ExtraRelicRewardPatch.IDEA_REWARD,
                (rewardSave) -> { // this handles what to do when this quest type is loaded.
                    return new ExtraRelicReward(getRelic(rewardSave.id));
                },
                (customReward) -> { // this handles what to do when this quest type is saved.
                    return new RewardSave(customReward.type.toString(), customReward.relic.toString(), -1, 0);
                });
    }

    private void initializeEvents() {
        BaseMod.addEvent(new AddEventParams.Builder(Encounter.ID, Encounter.class).
                eventType(EventUtils.EventType.ONE_TIME).
                endsWithRewardsUI(false).
                spawnCondition(() -> false).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(EpochalRevision.ID, EpochalRevision.class).
                eventType(EventUtils.EventType.NORMAL).
                endsWithRewardsUI(false).
                dungeonID("TheCity").
                dungeonID("TheBeyond").
                create());
        BaseMod.addEvent(new AddEventParams.Builder(InaugurationCeremony.ID, InaugurationCeremony.class).
                eventType(EventUtils.EventType.NORMAL).
                endsWithRewardsUI(true).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(LostAndFound.ID, LostAndFound.class).
                eventType(EventUtils.EventType.ONE_TIME).
                endsWithRewardsUI(false).
                create());

        // Replacement
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.BigFish.ID,
                encountermod.events.replacement.BigFish.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(BigFish.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.Cleric.ID,
                encountermod.events.replacement.Cleric.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(Cleric.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.GoldenIdolEvent.ID,
                encountermod.events.replacement.GoldenIdolEvent.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(GoldenIdolEvent.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.ShiningLight.ID,
                encountermod.events.replacement.ShiningLight.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(ShiningLight.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.GoldenWing.ID,
                encountermod.events.replacement.GoldenWing.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(GoldenWing.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.ScrapOoze.ID,
                encountermod.events.replacement.ScrapOoze.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID) &&
                                    !AbstractDungeon.player.hasRelic(JuzuBracelet.ID)).
                endsWithRewardsUI(false).
                overrideEvent(ScrapOoze.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.GoopPuddle.ID,
                encountermod.events.replacement.GoopPuddle.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(GoopPuddle.ID).
                create());

        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.Beggar.ID,
                encountermod.events.replacement.Beggar.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(Beggar.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.BackToBasics.ID,
                encountermod.events.replacement.BackToBasics.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(BackToBasics.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.Nest.ID,
                encountermod.events.replacement.Nest.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(Nest.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.TheJoust.ID,
                encountermod.events.replacement.TheJoust.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(TheJoust.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.TheLibrary.ID,
                encountermod.events.replacement.TheLibrary.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(TheLibrary.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.TheMausoleum.ID,
                encountermod.events.replacement.TheMausoleum.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(TheMausoleum.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.Colosseum.ID,
                encountermod.events.replacement.Colosseum.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(true).
                overrideEvent(Colosseum.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.ForgottenAltar.ID,
                encountermod.events.replacement.ForgottenAltar.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(ForgottenAltar.ID).
                create());

        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.Falling.ID,
                encountermod.events.replacement.Falling.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(Falling.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.MindBloom.ID,
                encountermod.events.replacement.MindBloom.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(true).
                overrideEvent(MindBloom.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.SecretPortal.ID,
                encountermod.events.replacement.SecretPortal.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(true).
                overrideEvent(SecretPortal.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.TombRedMask.ID,
                encountermod.events.replacement.TombRedMask.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(TombRedMask.ID).
                create());

        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.GremlinWheelGame.ID,
                encountermod.events.replacement.GremlinWheelGame.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(GremlinWheelGame.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.FountainOfCurseRemoval.ID,
                encountermod.events.replacement.FountainOfCurseRemoval.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(FountainOfCurseRemoval.ID).
                create());
        BaseMod.addEvent(new AddEventParams.Builder(
                encountermod.events.replacement.AccursedBlacksmith.ID,
                encountermod.events.replacement.AccursedBlacksmith.class).
                eventType(EventUtils.EventType.OVERRIDE).
                bonusCondition(() -> AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)).
                endsWithRewardsUI(false).
                overrideEvent(AccursedBlacksmith.ID).
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
    public void receiveEditRelics() {
        // common.
        BaseMod.addRelic(new BagOfIdeas(), RelicType.SHARED);

        // uncommon.
        BaseMod.addRelic(new RevenantRemains(), RelicType.SHARED);
        BaseMod.addRelic(new PersonalPaintbrush(), RelicType.SHARED);

        // rare.
        BaseMod.addRelic(new SpiritHunterEarl(), RelicType.SHARED);

        // event.
        BaseMod.addRelic(new GraffitiOfTheEraOfHope(), RelicType.SHARED);
        BaseMod.addRelic(new HatredOfTheEraOfVendetta(), RelicType.SHARED);
        BaseMod.addRelic(new LongingOfTheEraOfDreams(), RelicType.SHARED);
        BaseMod.addRelic(new VisionsOfTheEraOfProsperity(), RelicType.SHARED);
        BaseMod.addRelic(new SufferingOfTheEraOfCatastrophe(), RelicType.SHARED);
    }

    @Override
    public void receiveEditStrings() {
        String lang = getLang();
        String relicStrings = Gdx.files.internal("resources/encountermod/strings/" + lang + "/relics.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
        String eventStrings = Gdx.files.internal("resources/encountermod/strings/" + lang + "/events.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(EventStrings.class, eventStrings);
        String powerStrings = Gdx.files.internal("resources/encountermod/strings/" + lang + "/powers.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(PowerStrings.class, powerStrings);
        String uiStrings = Gdx.files.internal("resources/encountermod/strings/" + lang + "/ui.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
        String monsterStrings = Gdx.files.internal("resources/encountermod/strings/" + lang + "/monsters.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(MonsterStrings.class, monsterStrings);
        String cardStrings = Gdx.files.internal("resources/encountermod/strings/" + lang + "/cards.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(CardStrings.class, cardStrings);
    }

    private String getLang() {
        String lang = "eng";
        if (Settings.language == Settings.GameLanguage.ZHS || Settings.language == Settings.GameLanguage.ZHT) {
            lang = "zhs";
        }
        return lang;
    }
}