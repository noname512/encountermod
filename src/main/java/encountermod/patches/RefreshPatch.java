package encountermod.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.ui.buttons.CancelButton;
import encountermod.EncounterMod;
import encountermod.relics.VisionsOfTheEraOfProsperity;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class RefreshPatch {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RefreshPatch.class);

    @SpirePatch(clz = MapRoomNode.class, method = SpirePatch.CLASS)
    public static class OptFields {
        public static SpireField<Hitbox> refreshHb = new SpireField<>(() -> new Hitbox(25.0F * Settings.scale, 25.0F * Settings.scale));
        public static SpireField<Integer> refreshNumRoom = new SpireField<>(() -> 0);
    }

    private static ArrayList<PowerTip> tips;
    private static float SPACING_X;
    private static float OFFSET_X;
    private static float OFFSET_Y;
    public static HashMap<String, Integer> roomWeight;
    public static int totalWeight;
    public static int refreshNumDungeon;
    public static int rngUsedNum;
    public static int maxRefreshNum;

    private static final Logger logger = Logger.getLogger(RefreshPatch.class.getName());

    public static void initPosition() {
        tips = new ArrayList<>();
        tips.add(new PowerTip(EncounterMod.TEXT[0], EncounterMod.TEXT[1]));
        SPACING_X = Settings.xScale * 64.0F * 2.0F;
        OFFSET_X = 610.0F * Settings.xScale;
        OFFSET_Y = 200.0F * Settings.scale;
    }

    public static void init() {
        roomWeight = new HashMap<>();
        roomWeight.put("Monster", 4);
        roomWeight.put("Elite", 1);
        roomWeight.put("Event", 2);
        roomWeight.put("Treasure", 2);
        roomWeight.put("Shop", 1);
        roomWeight.put("Rest", 2);
        totalWeight = 12;
        maxRefreshNum = 1;
    }

    @SpirePatch(clz = MapRoomNode.class, method = "render")
    public static class RenderMapPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(MapRoomNode _inst, SpriteBatch sb) {
            if (getRoomTypeStr(_inst).isEmpty() || (!AbstractDungeon.id.equals("Exordium") && !AbstractDungeon.id.equals("TheCity") && !AbstractDungeon.id.equals("TheBeyond") && !AbstractDungeon.id.equals("samirg:TheSami"))) {
                return;
            }
            if (!(AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMPLETE)) {
                return;
            }
            if (AbstractDungeon.getCurrMapNode().isConnectedTo(_inst) || AbstractDungeon.getCurrMapNode().wingedIsConnectedTo(_inst) || (!AbstractDungeon.firstRoomChosen && _inst.y == 0)) {
                logger.info("render refresh, x = " + _inst.x + ", y = " + _inst.y);
                sb.draw(EncounterMod.refreshImg, _inst.x * SPACING_X + OFFSET_X - 42.0F + _inst.offsetX, _inst.y * Settings.MAP_DST_Y + OFFSET_Y + DungeonMapScreen.offsetY - 42.0F + _inst.offsetY, 42.0F, 42.0F, 84.0F, 84.0F, 0.3F * Settings.scale, 0.3F * Settings.scale, 0.0F, 0, 0, 84, 84, false, false);
                if (OptFields.refreshHb.get(_inst).hovered) {
                    OptFields.refreshHb.get(_inst).render(sb);
                    TipHelper.queuePowerTips(InputHelper.mX + 50.0F * Settings.scale, InputHelper.mY + 50.0F * Settings.scale, tips);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.FieldAccessMatcher fieldAccessMatcher = new Matcher.FieldAccessMatcher(MapRoomNode.class, "taken");
                int[] token = LineFinder.findAllInOrder(ctBehavior, fieldAccessMatcher);
                return new int[]{token[1]};
            }
        }
    }

    @SpirePatch(clz = MapRoomNode.class, method = "update")
    public static class MapUpdatePatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(MapRoomNode _inst, @ByRef float[] ___animWaitTimer) {
            if (getRoomTypeStr(_inst).isEmpty() || (!AbstractDungeon.id.equals("Exordium") && !AbstractDungeon.id.equals("TheCity") && !AbstractDungeon.id.equals("TheBeyond") && !AbstractDungeon.id.equals("samirg:TheSami"))) {
                return;
            }
            if (!(AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMPLETE)) {
                return;
            }
            if (AbstractDungeon.getCurrMapNode().isConnectedTo(_inst) || AbstractDungeon.getCurrMapNode().wingedIsConnectedTo(_inst) || (!AbstractDungeon.firstRoomChosen && _inst.y == 0)) {
                OptFields.refreshHb.get(_inst).move(_inst.x * SPACING_X + OFFSET_X + _inst.offsetX, _inst.y * Settings.MAP_DST_Y + OFFSET_Y + DungeonMapScreen.offsetY + _inst.offsetY);
                OptFields.refreshHb.get(_inst).update();

                if (OptFields.refreshHb.get(_inst).hovered) {
                    if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP && AbstractDungeon.dungeonMapScreen.clicked && ___animWaitTimer[0] <= 0.0F &&
                            (EncounterMod.ideaCount > 0 || (AbstractDungeon.player.hasRelic(VisionsOfTheEraOfProsperity.ID) && refreshNumDungeon == 0))
                            && OptFields.refreshNumRoom.get(_inst) < maxRefreshNum) {
                        OptFields.refreshNumRoom.set(_inst, OptFields.refreshNumRoom.get(_inst) + 1);
                        String roomType = getRoomTypeStr(_inst);
                        String targetRoomType = "";
                        if (AbstractDungeon.player.hasRelic(VisionsOfTheEraOfProsperity.ID) && refreshNumDungeon == 0) {
                            targetRoomType = "Shop";
                            AbstractDungeon.player.getRelic(VisionsOfTheEraOfProsperity.ID).counter = 0;
                        } else {
                            EncounterMod.ideaCount--;
                            logger.info("totalWeight: " + totalWeight);
                            for (String type : roomWeight.keySet())
                                logger.info("weight of " + type + ": " + roomWeight.get(type));
                            logger.info("real room type: " + roomType);
                            int resWeight = totalWeight - roomWeight.getOrDefault(roomType, 0);
                            int rnd = AbstractDungeon.mapRng.random(resWeight - 1);
                            rngUsedNum++;
                            for (String s : roomWeight.keySet())
                                if (!s.equals(roomType)) {
                                    if (rnd < roomWeight.get(s)) {
                                        targetRoomType = s;
                                        break;
                                    } else {
                                        rnd -= roomWeight.get(s);
                                    }
                                }
                        }
                        if (targetRoomType.isEmpty()) {
                            logger.warning("Invalid refresh result!");
                        } else {
                            logger.info("node(" + _inst.x + ", " + _inst.y + ") refreshed to " + targetRoomType + " Room.");
                            SaveData.nodeRefreshData.add(new SaveData.NodeRefreshSave(_inst.x, _inst.y, targetRoomType));
                            _inst.room = getRoomFromType(targetRoomType);
                            refreshNumDungeon++;
                        }
                        EncounterMod.isLastOpRefresh = true;
                        AbstractDungeon.overlayMenu.cancelButton.hide();
                        AbstractDungeon.dungeonMapScreen.dismissable = false;
                        if (!CardCrawlGame.loadingSave) {
                            if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMPLETE) {
                                SaveHelper.saveIfAppropriate(SaveFile.SaveType.POST_COMBAT);
                            } else {
                                SaveHelper.saveIfAppropriate(SaveFile.SaveType.ENTER_ROOM);
                            }
                        }
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.FieldAccessMatcher fieldAccessMatcher = new Matcher.FieldAccessMatcher(MapRoomNode.class, "hb");
                return LineFinder.findInOrder(ctBehavior, fieldAccessMatcher);
            }
        }
    }

    private static String getRoomTypeStr(MapRoomNode _inst) {
        if (_inst.room instanceof MonsterRoomElite) {
            return "Elite";
        } else if (_inst.room instanceof MonsterRoom && !(_inst.room instanceof MonsterRoomBoss)) {
            return "Monster";
        } else if (_inst.room instanceof EventRoom) {
            return "Event";
        } else if (_inst.room instanceof TreasureRoom) {
            return "Treasure";
        } else if (_inst.room instanceof ShopRoom) {
            return "Shop";
        } else if (_inst.room instanceof RestRoom) {
            return "Rest";
        }
        return "";
    }

    private static AbstractRoom getRoomFromType(String type) {
        switch (type) {
            case "Elite":
                return new MonsterRoomElite();
            case "Monster":
                return new MonsterRoom();
            case "Event":
                return new EventRoom();
            case "Treasure":
                return new TreasureRoom();
            case "Shop":
                return new ShopRoom();
            case "Rest":
                return new RestRoom();
        }
        return new EventRoom();
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "generateMap")
    public static class InitFreshNumDungeonPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            logger.info("fromSaveFile = " + SaveData.fromSaveFile);
            if (SaveData.fromSaveFile) {
                for (SaveData.NodeRefreshSave data : SaveData.nodeRefreshData) {
                    MapRoomNode node = AbstractDungeon.map.get(data.y).get(data.x);
                    if (node.x != data.x || node.y != data.y) {
                        logger.info("invalid node (" + data.x + ", " + data.y + ")!");
                        continue;
                    }
                    OptFields.refreshNumRoom.set(node, OptFields.refreshNumRoom.get(node) + 1);
                    logger.info("Refresh node (" + data.x + ", " + data.y + ") to " + data.result + " by save file");
                    node.room = getRoomFromType(data.result);
                }
                logger.info("Encountermod refreshed the dungeon map as follows:");
                logger.info(MapGenerator.toString(AbstractDungeon.map, true));
                for (int i = 0; i < rngUsedNum; i++) AbstractDungeon.mapRng.random(2); // used random
                SaveData.fromSaveFile = false;
            } else {
                refreshNumDungeon = 0;
                rngUsedNum = 0;
                if (AbstractDungeon.player.hasRelic(VisionsOfTheEraOfProsperity.ID)) {
                    AbstractDungeon.player.getRelic(VisionsOfTheEraOfProsperity.ID).counter = 1;
                }
                SaveData.nodeRefreshData.clear();
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "nextRoomTransition", paramtypez = {SaveFile.class})
    public static class NextRoomTransitionPatch {
        @SpirePrefixPatch
        public static void Prefix() {
            EncounterMod.isLastOpRefresh = false;
        }
    }

    @SpirePatch(clz = DungeonMapScreen.class, method = "open")
    public static class CancelButtonShowPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(CancelButton.class.getName()) && m.getMethodName().equals("show")) {
                        m.replace(String.format(
                                "if (%s.isLastOpRefresh) { %s.overlayMenu.cancelButton.hide(); %s.dungeonMapScreen.dismissable = false; } else { $_ = $proceed($$); }",
                                EncounterMod.class.getName(),
                                AbstractDungeon.class.getName(),
                                AbstractDungeon.class.getName()
                        ));
                    }
                }
            };
        }
    }
}
