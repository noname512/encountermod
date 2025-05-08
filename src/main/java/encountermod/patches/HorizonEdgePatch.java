package encountermod.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import encountermod.EncounterMod;
import encountermod.relics.SufferingOfTheEraOfCatastrophe;
import encountermod.vfx.HorizonMapEdge;
import javassist.CtBehavior;

import java.util.ArrayList;

public class HorizonEdgePatch {
    static public int moveCost;

    @SpirePatch(clz = MapRoomNode.class, method = SpirePatch.CLASS)
    public static class OptFields {
        public static SpireField<HorizonMapEdge> l_edge = new SpireField<>(() -> null);
        public static SpireField<HorizonMapEdge> r_edge = new SpireField<>(() -> null);
    }

    public static void generateHorizontalEdge(float chance) {
        for (int i = 1; i < AbstractDungeon.map.size() - 1; i++) {
            ArrayList<MapRoomNode> nodes = AbstractDungeon.map.get(i);
            for (int j = 0; j < nodes.size() - 1; j++)
                if (nodes.get(j).getRoom() != null && nodes.get(j).hasEdges() && EncounterMod.myMapRng.randomBoolean(chance)) {
                    MapRoomNode curNode = nodes.get(j);
                    int k = j + 1;
                    while (k < nodes.size() && (nodes.get(k).getRoom() == null || !nodes.get(k).hasEdges())) k++;
                    if (k >= nodes.size()) break;
                    MapRoomNode nxtNode = nodes.get(k);
                    HorizonMapEdge lEdge = new HorizonMapEdge(curNode.x, curNode.y, curNode.offsetX, curNode.offsetY, nxtNode.x, nxtNode.y, nxtNode.offsetX, nxtNode.offsetY, true);
                    HorizonMapEdge rEdge = new HorizonMapEdge(nxtNode.x, nxtNode.y, nxtNode.offsetX, nxtNode.offsetY, curNode.x, curNode.y, curNode.offsetX, curNode.offsetY, false);
                    lEdge.revertEdge = rEdge;
                    rEdge.revertEdge = lEdge;
                    OptFields.l_edge.set(curNode, lEdge);
                    OptFields.r_edge.set(nxtNode, rEdge);
                }
        }
    }

    public static boolean canHorizonMove() {
        return EncounterMod.ideaCount >= moveCost;
    }

    public static MapEdge horizonConnect(MapRoomNode u, MapRoomNode v) {
        if (OptFields.l_edge.get(u) != null && OptFields.l_edge.get(u).dstX == v.x && OptFields.l_edge.get(u).dstY == v.y) {
            return OptFields.l_edge.get(u);
        }
        if (OptFields.r_edge.get(u) != null && OptFields.r_edge.get(u).dstX == v.x && OptFields.r_edge.get(u).dstY == v.y) {
            return OptFields.r_edge.get(u);
        }
        return null;
    }

    @SpirePatch(clz = MapRoomNode.class, method = "isConnectedTo")
    public static class IsConnectToPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> Prefix(MapRoomNode _inst, MapRoomNode node) {
            if (!canHorizonMove()) {
                return SpireReturn.Continue();
            }
            if (horizonConnect(_inst, node) != null && !node.taken) {
                return SpireReturn.Return(true);
            } else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = MapRoomNode.class, method = "getEdgeConnectedTo")
    public static class GetEdgeConnectedToPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> Prefix(MapRoomNode _inst, MapRoomNode node) {
            MapEdge e = horizonConnect(_inst, node);
            if (e != null) {
                return SpireReturn.Return(e);
            } else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = MapRoomNode.class, method = "render")
    public static class RenderPatch {
        @SpirePrefixPatch
        public static void Prefix(MapRoomNode _inst, SpriteBatch sb) {
            if (OptFields.l_edge.get(_inst) != null) {
                OptFields.l_edge.get(_inst).render(sb);
            }
            if (OptFields.r_edge.get(_inst) != null) {
                OptFields.r_edge.get(_inst).render(sb);
            }
        }

        @SpirePostfixPatch
        public static void Postfix(MapRoomNode _inst, SpriteBatch sb) {
            if (!_inst.taken && _inst.hb.hovered && horizonConnect(_inst, AbstractDungeon.getCurrMapNode()) != null) {
                TipHelper.renderGenericTip(InputHelper.mX + 50.0F * Settings.scale, InputHelper.mY + 50.0F * Settings.scale, EncounterMod.TEXT[4], EncounterMod.TEXT[5] + moveCost + EncounterMod.TEXT[6]);
            }
        }
    }

    @SpirePatch(clz = MapRoomNode.class, method = "update")
    public static class RealMovePatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(MapRoomNode _inst) {
            if (horizonConnect(AbstractDungeon.getCurrMapNode(), _inst) != null) {
                EncounterMod.ideaCount -= moveCost;
            }
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(MapRoomNode.class, "playNodeSelectedSound");
                return LineFinder.findAllInOrder(ctBehavior, methodCallMatcher);
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "populatePathTaken")
    public static class populatePathTakenPatch {
        @SpirePrefixPatch
        public static void Prefix() {
            if (AbstractDungeon.player.hasRelic(SufferingOfTheEraOfCatastrophe.ID)) {
                generateHorizontalEdge(SufferingOfTheEraOfCatastrophe.CHANCE);
            }
        }
    }
}
