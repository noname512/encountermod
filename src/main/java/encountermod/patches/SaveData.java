package encountermod.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import encountermod.EncounterMod;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveData {
    private static final Logger logger = LogManager.getLogger(SaveData.class);

    public static class NodeRefreshSave {
        public int x;
        public int y;
        public String result;

        public NodeRefreshSave(int x, int y, String result) {
            this.x = x;
            this.y = y;
            this.result = result;
        }
    }

    public static class ExtraData {
        public int ecm_idea_count = 0;
        public int ecm_idea_prob = 0;
        public boolean ecm_is_first_event = false;
        public ArrayList<NodeRefreshSave> ecm_node_refresh_data = new ArrayList<>();
        public int ecm_refresh_num_dungeon = 0;
        public int ecm_rng_used_num = 0;
        public HashMap<String, Integer> ecm_room_weight = new HashMap<>();
        public int ecm_total_weight = 0;
        public boolean ecm_is_last_op_refresh = false;
        public boolean ecm_first_room_chosen = false;
        public int ecm_max_refresh_num = 1;
    }

    public static int idea_count = 0;
    public static int idea_prob = 0;
    public static boolean is_first_event = false;
    public static ArrayList<NodeRefreshSave> nodeRefreshData = new ArrayList<>();
    public static int refresh_num_dungeon = 0;
    public static int rng_used_num = 0;
    public static boolean fromSaveFile;
    public static HashMap<String, Integer> room_weight = new HashMap<>();
    public static int total_weight = 0;
    public static boolean is_last_op_refresh = false;
    public static boolean first_room_chosen = false;
    public static int max_refresh_num = 1;

    @SpirePatch(clz = SaveFile.class, method = "<ctor>", paramtypez = {SaveFile.SaveType.class})
    public static class SaveTheSaveData {
        @SpirePostfixPatch
        public static void Postfix(SaveFile _inst, SaveFile.SaveType type) {
            idea_count = EncounterMod.ideaCount;
            idea_prob = EncounterMod.prob;
            is_first_event = EncounterMod.firstEvent;
            refresh_num_dungeon = RefreshPatch.refreshNumDungeon;
            rng_used_num = RefreshPatch.rngUsedNum;
            room_weight = new HashMap<>(RefreshPatch.roomWeight);
            total_weight = RefreshPatch.totalWeight;
            is_last_op_refresh = EncounterMod.isLastOpRefresh;
            first_room_chosen = AbstractDungeon.firstRoomChosen;
            max_refresh_num = RefreshPatch.maxRefreshNum;
            SaveData.logger.info("Extra Data Saved!");
        }
    }

    @SpirePatch(clz = SaveAndContinue.class, method = "save", paramtypez = {SaveFile.class})
    public static class SaveDataToFile {
        @SpireInsertPatch(locator = Locator.class, localvars = {"params"})
        public static void Insert(SaveFile save, HashMap<Object, Object> params) {
            params.put("ecm_idea_count", idea_count);
            params.put("ecm_idea_prob", idea_prob);
            params.put("ecm_is_first_event", is_first_event);
            params.put("ecm_node_refresh_data", nodeRefreshData);
            params.put("ecm_refresh_num_dungeon", refresh_num_dungeon);
            params.put("ecm_rng_used_num", rng_used_num);
            params.put("ecm_room_weight", room_weight);
            params.put("ecm_total_weight", total_weight);
            params.put("ecm_is_last_op_refresh", is_last_op_refresh);
            params.put("ecm_first_room_chosen", first_room_chosen);
            params.put("ecm_max_refresh_num", max_refresh_num);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(GsonBuilder.class, "create");
                return LineFinder.findInOrder(ctMethodToPatch, methodCallMatcher);
            }
        }
    }

    @SpirePatch(clz = SaveAndContinue.class, method = "loadSaveFile", paramtypez = {String.class})
    public static class LoadDataFromFile {
        @SpireInsertPatch(locator = Locator.class, localvars = {"gson", "savestr"})
        public static void Insert(String path, Gson gson, String savestr) {
            try {
                ExtraData data = gson.fromJson(savestr, ExtraData.class);
                fromSaveFile = true;
                nodeRefreshData = new ArrayList<>(data.ecm_node_refresh_data);
                idea_count = data.ecm_idea_count;
                idea_prob = data.ecm_idea_prob;
                is_first_event = data.ecm_is_first_event;
                refresh_num_dungeon = data.ecm_refresh_num_dungeon;
                rng_used_num = data.ecm_rng_used_num;
                room_weight = new HashMap<>(data.ecm_room_weight);
                total_weight = data.ecm_total_weight;
                is_last_op_refresh = data.ecm_is_last_op_refresh;
                first_room_chosen = data.ecm_first_room_chosen;
                max_refresh_num = data.ecm_max_refresh_num;
                SaveData.logger.info("Loaded encountermod save data successfully");
            } catch (Exception e) {
                SaveData.logger.error("Fail to load rhinemod save data.");
                e.printStackTrace();
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(Gson.class, "fromJson");
                return LineFinder.findInOrder(ctMethodToPatch, methodCallMatcher);
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "loadSave")
    public static class loadSavePatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractDungeon _inst, SaveFile file) {
            EncounterMod.ideaCount = idea_count;
            EncounterMod.prob = idea_prob;
            EncounterMod.firstEvent = is_first_event;
            RefreshPatch.refreshNumDungeon = refresh_num_dungeon;
            RefreshPatch.rngUsedNum = rng_used_num;
            RefreshPatch.roomWeight = new HashMap<>(room_weight);
            RefreshPatch.totalWeight = total_weight;
            EncounterMod.isLastOpRefresh = is_last_op_refresh;
            RefreshPatch.maxRefreshNum = max_refresh_num;
            SaveData.logger.info("Save loaded.");
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "populatePathTaken")
    public static class PopulatePathTakenPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractDungeon.class.getName()) && m.getMethodName().equals("nextRoomTransition")) {
                        m.replace(String.format("if (%s.isLastOpRefresh) { %s.handle($1); } else { $_ = $proceed($$); }", EncounterMod.class.getName(), SaveData.PopulatePathTakenPatch.class.getName()));
                    }
                }
            };
        }

        public static void handle(SaveFile saveFile) {
            AbstractDungeon.dungeonMapScreen.dismissable = false;
            AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
            AbstractDungeon.previousScreen = null;
            if (first_room_chosen) {
                AbstractDungeon.currMapNode = AbstractDungeon.nextRoom;
            }
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.firstRoomChosen = first_room_chosen;
            AbstractDungeon.dungeonMapScreen.open(false);
            AbstractDungeon.scene.randomizeScene();
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractDungeon _inst, SaveFile saveFile) {
            if (EncounterMod.isLastOpRefresh && !first_room_chosen) {
                AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
                AbstractDungeon.currMapNode.room = new EmptyRoom();
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "isLoadingIntoNeow")
    public static class LoadingIntoNeowPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> Prefix(AbstractDungeon _inst, SaveFile saveFile) {
            if (EncounterMod.isLastOpRefresh) return SpireReturn.Return(false);
            else return SpireReturn.Continue();
        }
    }
}
