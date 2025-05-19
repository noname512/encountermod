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
import encountermod.relics.LongingOfTheEraOfDreams;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
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

        // metrics from longing of the era of dreams below
        public int ecm_max_block_at_turn_start = 0;
        public HashMap<Integer, Integer> ecm_max_dmg_received = new HashMap<>();
        public int ecm_max_first_dmg_taken = 0;
        public boolean ecm_is_first_dmg_taken = false;
        public boolean ecm_empty_hand_end_turn = false;
        public int ecm_max_dmg_received_elite = 0;
        public int ecm_attacked_cnt = 0;
        public int ecm_max_attacked_cnt = 0;
        public boolean ecm_rested = false;
        public int ecm_actor_cnt = 0;
        public int ecm_total_battle_cnt = 0;
        public int ecm_total_turn_cnt = 0;
        public int ecm_dmg_received_cnt = 0;
        public ArrayList<Integer> ecm_recent_dmg_received_cnt = new ArrayList<>();
        public int ecm_total_dmg_received = 0;
        public int ecm_max_turn_1_res_card = 0;
        public int ecm_total_res_card = 0;
        public int ecm_small_dmg_received_cnt = 0;
        public ArrayList<Integer> ecm_recent_small_dmg_received_cnt = new ArrayList<>();
        public int ecm_max_non_boss_turn = 0;
        public int ecm_kill_minion_cnt = 0;
        public int ecm_exhaust_card_cnt = 0;
        public int ecm_max_exhaust_card_cnt = 0;
        public int ecm_horizon_move_cost = 0;
    }

    public static ExtraData exSaveData = new ExtraData();

    public static ArrayList<NodeRefreshSave> nodeRefreshData = new ArrayList<>();
    public static boolean fromSaveFile;

    @SpirePatch(clz = SaveFile.class, method = "<ctor>", paramtypez = {SaveFile.SaveType.class})
    public static class SaveTheSaveData {
        @SpirePostfixPatch
        public static void Postfix(SaveFile _inst, SaveFile.SaveType type) {
            exSaveData.ecm_idea_count = EncounterMod.ideaCount;
            exSaveData.ecm_idea_prob = EncounterMod.prob;
            exSaveData.ecm_is_first_event = EncounterMod.firstEvent;
            exSaveData.ecm_refresh_num_dungeon = RefreshPatch.refreshNumDungeon;
            exSaveData.ecm_rng_used_num = RefreshPatch.rngUsedNum;
            exSaveData.ecm_room_weight = new HashMap<>(RefreshPatch.roomWeight);
            exSaveData.ecm_total_weight = RefreshPatch.totalWeight;
            exSaveData.ecm_is_last_op_refresh = EncounterMod.isLastOpRefresh;
            exSaveData.ecm_first_room_chosen = AbstractDungeon.firstRoomChosen;
            exSaveData.ecm_max_refresh_num = RefreshPatch.maxRefreshNum;
            exSaveData.ecm_max_block_at_turn_start = LongingOfTheEraOfDreams.maxBlockAtTurnStart;
            exSaveData.ecm_max_dmg_received = LongingOfTheEraOfDreams.maxDmgReceived;
            exSaveData.ecm_max_first_dmg_taken = LongingOfTheEraOfDreams.maxFirstDmgTaken;
            exSaveData.ecm_is_first_dmg_taken = LongingOfTheEraOfDreams.isFirstDmgTaken;
            exSaveData.ecm_max_dmg_received_elite = LongingOfTheEraOfDreams.maxDmgReceivedElite;
            exSaveData.ecm_empty_hand_end_turn = LongingOfTheEraOfDreams.emptyHandEndTurn;
            exSaveData.ecm_attacked_cnt = LongingOfTheEraOfDreams.attackedCnt;
            exSaveData.ecm_max_attacked_cnt = LongingOfTheEraOfDreams.maxAttackedCnt;
            exSaveData.ecm_rested = LongingOfTheEraOfDreams.rested;
            exSaveData.ecm_actor_cnt = LongingOfTheEraOfDreams.actorCnt;
            exSaveData.ecm_total_battle_cnt = LongingOfTheEraOfDreams.totalBattleCnt;
            exSaveData.ecm_total_turn_cnt = LongingOfTheEraOfDreams.totalTurnCnt;
            exSaveData.ecm_dmg_received_cnt = LongingOfTheEraOfDreams.dmgReceivedCnt;
            exSaveData.ecm_recent_dmg_received_cnt = LongingOfTheEraOfDreams.recentDmgReceivedCnt;
            exSaveData.ecm_total_dmg_received = LongingOfTheEraOfDreams.totalDmgReceived;
            exSaveData.ecm_max_turn_1_res_card = LongingOfTheEraOfDreams.maxTurn1ResCard;
            exSaveData.ecm_total_res_card = LongingOfTheEraOfDreams.totalResCard;
            exSaveData.ecm_small_dmg_received_cnt = LongingOfTheEraOfDreams.smallDmgReceivedCnt;
            exSaveData.ecm_recent_small_dmg_received_cnt = LongingOfTheEraOfDreams.recentSmallDmgReceivedCnt;
            exSaveData.ecm_max_non_boss_turn = LongingOfTheEraOfDreams.maxNonBossTurn;
            exSaveData.ecm_kill_minion_cnt = LongingOfTheEraOfDreams.killMinionCnt;
            exSaveData.ecm_exhaust_card_cnt = LongingOfTheEraOfDreams.exhaustCardCnt;
            exSaveData.ecm_max_exhaust_card_cnt = LongingOfTheEraOfDreams.maxExhaustCardCnt;
            exSaveData.ecm_horizon_move_cost = HorizonEdgePatch.moveCost;
        }
    }

    @SpirePatch(clz = SaveAndContinue.class, method = "save", paramtypez = {SaveFile.class})
    public static class SaveDataToFile {
        @SpireInsertPatch(locator = Locator.class, localvars = {"params"})
        public static void Insert(SaveFile save, HashMap<Object, Object> params) {
            try {
                for (Field field : ExtraData.class.getFields()) {
                    field.setAccessible(true);
                    Object value = field.get(exSaveData);
                    params.put(field.getName(), value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            SaveData.logger.info("Extra Data Saved!");
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
                exSaveData = gson.fromJson(savestr, ExtraData.class);
                fromSaveFile = true;
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
            EncounterMod.ideaCount = exSaveData.ecm_idea_count;
            EncounterMod.prob = exSaveData.ecm_idea_prob;
            EncounterMod.firstEvent = exSaveData.ecm_is_first_event;
            RefreshPatch.refreshNumDungeon = exSaveData.ecm_refresh_num_dungeon;
            RefreshPatch.rngUsedNum = exSaveData.ecm_rng_used_num;
            RefreshPatch.roomWeight = exSaveData.ecm_room_weight;
            RefreshPatch.totalWeight = exSaveData.ecm_total_weight;
            EncounterMod.isLastOpRefresh = exSaveData.ecm_is_last_op_refresh;
            RefreshPatch.maxRefreshNum = exSaveData.ecm_max_refresh_num;
            LongingOfTheEraOfDreams.maxBlockAtTurnStart = exSaveData.ecm_max_block_at_turn_start;
            LongingOfTheEraOfDreams.maxDmgReceived = exSaveData.ecm_max_dmg_received;
            LongingOfTheEraOfDreams.maxFirstDmgTaken = exSaveData.ecm_max_first_dmg_taken;
            LongingOfTheEraOfDreams.isFirstDmgTaken = exSaveData.ecm_is_first_dmg_taken;
            LongingOfTheEraOfDreams.maxDmgReceivedElite = exSaveData.ecm_max_dmg_received_elite;
            LongingOfTheEraOfDreams.emptyHandEndTurn = exSaveData.ecm_empty_hand_end_turn;
            LongingOfTheEraOfDreams.attackedCnt = exSaveData.ecm_attacked_cnt;
            LongingOfTheEraOfDreams.maxAttackedCnt = exSaveData.ecm_max_attacked_cnt;
            LongingOfTheEraOfDreams.rested = exSaveData.ecm_rested;
            LongingOfTheEraOfDreams.actorCnt = exSaveData.ecm_actor_cnt;
            LongingOfTheEraOfDreams.totalBattleCnt = exSaveData.ecm_total_battle_cnt;
            LongingOfTheEraOfDreams.totalTurnCnt = exSaveData.ecm_total_turn_cnt;
            LongingOfTheEraOfDreams.dmgReceivedCnt = exSaveData.ecm_dmg_received_cnt;
            LongingOfTheEraOfDreams.recentDmgReceivedCnt = exSaveData.ecm_recent_dmg_received_cnt;
            LongingOfTheEraOfDreams.totalDmgReceived = exSaveData.ecm_total_dmg_received;
            LongingOfTheEraOfDreams.maxTurn1ResCard = exSaveData.ecm_max_turn_1_res_card;
            LongingOfTheEraOfDreams.totalResCard = exSaveData.ecm_total_res_card;
            LongingOfTheEraOfDreams.smallDmgReceivedCnt = exSaveData.ecm_small_dmg_received_cnt;
            LongingOfTheEraOfDreams.recentSmallDmgReceivedCnt = exSaveData.ecm_recent_small_dmg_received_cnt;
            LongingOfTheEraOfDreams.maxNonBossTurn = exSaveData.ecm_max_non_boss_turn;
            LongingOfTheEraOfDreams.killMinionCnt = exSaveData.ecm_kill_minion_cnt;
            LongingOfTheEraOfDreams.exhaustCardCnt = exSaveData.ecm_exhaust_card_cnt;
            HorizonEdgePatch.moveCost = exSaveData.ecm_horizon_move_cost;
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
            if (AbstractDungeon.firstRoomChosen) {
                AbstractDungeon.currMapNode = AbstractDungeon.nextRoom;
            }
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.dungeonMapScreen.open(false);
            AbstractDungeon.scene.randomizeScene();
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractDungeon _inst, SaveFile saveFile) {
            if (EncounterMod.isLastOpRefresh && !AbstractDungeon.firstRoomChosen) {
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
