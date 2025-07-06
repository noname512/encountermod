package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import encountermod.EncounterMod;
import encountermod.monsters.SpinesOfEpoch;
import encountermod.patches.HorizonEdgePatch;
import encountermod.patches.RefreshPatch;
import encountermod.vfx.HorizonMapEdge;

import java.util.ArrayList;

public class SufferingOfTheEraOfCatastrophe extends CustomRelic {
    public static final String ID = "encountermod:SufferingOfTheEraOfCatastrophe";
    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
    public static final String NAME = relicStrings.NAME;
    public static final String[] DESCRIPTIONS = relicStrings.DESCRIPTIONS;
    public static final Texture IMG = new Texture("resources/encountermod/images/relics/SufferingOfTheEraOfCatastrophe.png");
    public static final Texture IMG_OUTLINE = new Texture("resources/encountermod/images/relics/SufferingOfTheEraOfCatastrophe_p.png");
    public static final float CHANCE = 1.0F;
    public SufferingOfTheEraOfCatastrophe() {
        super(ID, IMG, IMG_OUTLINE, AbstractRelic.RelicTier.SPECIAL, AbstractRelic.LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        if (AbstractDungeon.id.equals("Exordium") || AbstractDungeon.id.equals("TheCity") || AbstractDungeon.id.equals("TheBeyond")) {
            HorizonEdgePatch.generateHorizontalEdge(CHANCE);
        }
        HorizonEdgePatch.moveCost = 2;
    }

    @Override
    public void onUnequip() {
        // Remove_HorizontalEdge
        if (AbstractDungeon.id.equals("Exordium") || AbstractDungeon.id.equals("TheCity") || AbstractDungeon.id.equals("TheBeyond")) {
            for (int i = 1; i < AbstractDungeon.map.size(); i++) {
                ArrayList<MapRoomNode> nodes = AbstractDungeon.map.get(i);
                for (int j = 0; j < nodes.size() - 1; j++)
                    if (nodes.get(j).getRoom() != null && nodes.get(j).hasEdges()) {
                        MapRoomNode curNode = nodes.get(j);
                        int k = j + 1;
                        while (k < nodes.size() && (nodes.get(k).getRoom() == null || !nodes.get(k).hasEdges())) k++;
                        if (k >= nodes.size()) break;
                        MapRoomNode nxtNode = nodes.get(k);
                        MapEdge remove = null;
                        for (MapEdge e : curNode.getEdges()) {
                            if ((e.dstX == nxtNode.x) && (e.dstY == nxtNode.y)) {
                                remove = e;
                                break;
                            }
                        }
                        if (remove != null) {
                            curNode.delEdge(remove);
                        }
                        remove = null;
                        for (MapEdge e : nxtNode.getEdges()) {
                            if ((e.dstX == curNode.x) && (e.dstY == curNode.y)) {
                                remove = e;
                                break;
                            }
                        }
                        if (remove != null) {
                            nxtNode.delEdge(remove);
                        }
                        HorizonEdgePatch.OptFields.l_edge.set(curNode, null);
                        HorizonEdgePatch.OptFields.r_edge.set(nxtNode, null);
                    }
            }
        }
        HorizonEdgePatch.moveCost = 2;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        float leftX = 1000.0F;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
            if (!m.isDeadOrEscaped()) {
                leftX = Math.min(leftX, (m.hb.x - Settings.WIDTH * 0.75F) / Settings.xScale);
            }
        float rng = AbstractDungeon.monsterRng.random(1, 10);
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
            AbstractMonster spines = new SpinesOfEpoch(leftX - 100.F, 0.0F);
            spines.usePreBattleAction();
            addToTop(new SpawnMonsterAction(spines, false));
            if (rng <= 3) {
                AbstractMonster spine2 = new SpinesOfEpoch(leftX - 300.F, 0.0F);
                spine2.usePreBattleAction();
                addToTop(new SpawnMonsterAction(spine2, false));
            }
        } else if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite) {
            if (rng <= 6) {
                AbstractMonster spines = new SpinesOfEpoch(leftX - 100.F, 0.0F);
                spines.usePreBattleAction();
                addToTop(new SpawnMonsterAction(spines, false));
            }
        } else if (AbstractDungeon.getCurrRoom() instanceof MonsterRoom) {
            if (rng <= 3) {
                AbstractMonster spines = new SpinesOfEpoch(leftX - 100.F, 0.0F);
                spines.usePreBattleAction();
                addToTop(new SpawnMonsterAction(spines, false));
            }
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SufferingOfTheEraOfCatastrophe();
    }

    @SpirePatch(clz = MonsterGroup.class, method = "areMonstersDead")
    public static class AllDeadPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> Prefix(MonsterGroup _inst) {
            for (AbstractMonster m : _inst.monsters) {
                if (!(m instanceof SpinesOfEpoch) && !m.isDead && !m.escaped) {
                    return SpireReturn.Return(false);
                }
            }
            return SpireReturn.Return(true);
        }
    }

    @SpirePatch(clz = MonsterGroup.class, method = "areMonstersBasicallyDead")
    public static class AllBasicallyDeadPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> Prefix(MonsterGroup _inst) {
            for (AbstractMonster m : _inst.monsters) {
                if (!(m instanceof SpinesOfEpoch) && !m.isDying && !m.isEscaping) {
                    return SpireReturn.Return(false);
                }
            }
            return SpireReturn.Return(true);
        }
    }
}
