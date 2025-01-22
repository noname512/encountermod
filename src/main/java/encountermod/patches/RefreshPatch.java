package encountermod.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import encountermod.EncounterMod;
import encountermod.relics.VisionsOfTheEraOfProsperity;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class RefreshPatch {
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
    public static int maxRefreshNum;

    public static void init() {
        tips = new ArrayList<>();
        tips.add(new PowerTip(EncounterMod.TEXT[0], EncounterMod.TEXT[1]));
        SPACING_X = Settings.xScale * 64.0F * 2.0F;
        OFFSET_X = 610.0F * Settings.xScale;
        OFFSET_Y = 200.0F * Settings.scale;
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
            if (getRoomTypeStr(_inst).isEmpty() || (!AbstractDungeon.id.equals("Exordium") && !AbstractDungeon.id.equals("TheCity") && !AbstractDungeon.id.equals("TheBeyond"))) {
                return;
            }
            if (AbstractDungeon.getCurrMapNode().isConnectedTo(_inst) || AbstractDungeon.getCurrMapNode().wingedIsConnectedTo(_inst) || (!AbstractDungeon.firstRoomChosen && _inst.y == 0)) {
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
            if (getRoomTypeStr(_inst).isEmpty() || (!AbstractDungeon.id.equals("Exordium") && !AbstractDungeon.id.equals("TheCity") && !AbstractDungeon.id.equals("TheBeyond"))) {
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
                            int resWeight = totalWeight - roomWeight.getOrDefault(roomType, 0);
                            int rnd = AbstractDungeon.mapRng.random(resWeight - 1);
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
                            Logger.getLogger(RefreshPatch.class.getName()).warning("Invalid refresh result!");
                        } else {
                            Logger.getLogger(RefreshPatch.class.getName()).info("node(" + _inst.x + ", " + _inst.y + ") refreshed to " + targetRoomType + " Room.");
                            switch (targetRoomType) {
                                case "Elite":
                                    _inst.room = new MonsterRoomElite();
                                    break;
                                case "Monster":
                                    _inst.room = new MonsterRoom();
                                    break;
                                case "Event":
                                    _inst.room = new EventRoom();
                                    break;
                                case "Treasure":
                                    _inst.room = new TreasureRoom();
                                    break;
                                case "Shop":
                                    _inst.room = new ShopRoom();
                                    break;
                                case "Rest":
                                    _inst.room = new RestRoom();
                                    break;
                            }
                        }
                        refreshNumDungeon++;
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
        String roomType = "";
        if (_inst.room instanceof MonsterRoomElite) {
            roomType = "Elite";
        } else if (_inst.room instanceof MonsterRoom && !(_inst.room instanceof MonsterRoomBoss)) {
            roomType = "Monster";
        } else if (_inst.room instanceof EventRoom) {
            roomType = "Event";
        } else if (_inst.room instanceof TreasureRoom) {
            roomType = "Treasure";
        } else if (_inst.room instanceof ShopRoom) {
            roomType = "Shop";
        } else if (_inst.room instanceof RestRoom) {
            roomType = "Rest";
        }
        return roomType;
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "generateMap")
    public static class InitFreshNumDungeonPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            RefreshPatch.refreshNumDungeon = 0;
            if (AbstractDungeon.player.hasRelic(VisionsOfTheEraOfProsperity.ID)) {
                AbstractDungeon.player.getRelic(VisionsOfTheEraOfProsperity.ID).counter = 1;
            }
        }
    }
}
