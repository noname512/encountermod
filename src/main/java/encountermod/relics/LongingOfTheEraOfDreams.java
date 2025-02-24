package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.FiendFire;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import com.megacrit.cardcrawl.characters.Defect;
import com.megacrit.cardcrawl.characters.Ironclad;
import com.megacrit.cardcrawl.characters.TheSilent;
import com.megacrit.cardcrawl.characters.Watcher;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import encountermod.EncounterMod;
import encountermod.patches.RefreshPatch;

import java.util.ArrayList;

public class LongingOfTheEraOfDreams extends CustomRelic {

    public static final String ID = "encountermod:LongingOfTheEraOfDreams";
    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
    public static final String NAME = relicStrings.NAME;
    public static final String[] DESCRIPTIONS = relicStrings.DESCRIPTIONS;
    public static final Texture IMG = new Texture("resources/encountermod/images/relics/LongingOfTheEraOfDreams.png");
    public static final Texture IMG_OUTLINE = new Texture("resources/encountermod/images/relics/LongingOfTheEraOfDreams_p.png");
    public LongingOfTheEraOfDreams() {
        super(ID, IMG, IMG_OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.gainGold(50);
        RefreshPatch.roomWeight.put("Treasure", 6);
        RefreshPatch.totalWeight = 16;
    }

    @Override
    public void onUnequip() {
        RefreshPatch.roomWeight.put("Treasure", 2);
        RefreshPatch.totalWeight = 12;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new LongingOfTheEraOfDreams();
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "returnRandomRelic")
    public static class ReturnRandomRelicPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> Prefix(AbstractRelic.RelicTier tier) {
            if (AbstractDungeon.getCurrRoom() instanceof TreasureRoom && AbstractDungeon.player.hasRelic(LongingOfTheEraOfDreams.ID)) {
                ArrayList<AbstractRelic> lst = new ArrayList<>();
                switch (tier) {
                    case COMMON:
                        if (AbstractDungeon.player.currentHealth < AbstractDungeon.player.maxHealth * 0.3) {
                            lst.add(new BloodVial()); // 小血瓶
                        }
                        if (AbstractDungeon.player.maxHealth < 50) {
                            lst.add(new Strawberry()); // 草莓
                        }
                        if (AbstractDungeon.player.hasRelic(CursedKey.ID)) {
                            lst.add(new Omamori()); // 御守
                        }
                        if (checkPotionFull()) {
                            lst.add(new PotionBelt()); // 药水腰带
                        }
                        if (EncounterMod.ideaCount == 0) {
                            lst.add(new BagOfIdeas()); // 一袋构想
                        }
                        if (AbstractDungeon.player.currentHealth < AbstractDungeon.player.maxHealth * 0.3 && !AbstractDungeon.player.hasRelic(CoffeeDripper.ID)) {
                            lst.add(new RegalPillow()); // 皇家枕头
                        }
                        if (AbstractDungeon.player.currentHealth < AbstractDungeon.player.maxHealth * 0.3 && AbstractDungeon.player instanceof Ironclad) {
                            lst.add(new RedSkull()); // 红头骨
                        }
                        if (calcUnattackCard() >= AbstractDungeon.player.masterDeck.size() * 0.8) {
                            lst.add(new ArtOfWar()); // 孙子兵法
                        }
                        if (calcAttackCard() >= AbstractDungeon.player.masterDeck.size() * 0.7) {
                            lst.add(new PenNib()); // 钢笔尖
                            lst.add(new Nunchaku()); // 双节棍
                        }
                        break;
                    case UNCOMMON:
                        if (AbstractDungeon.player.masterDeck.size() <= 7) {
                            lst.add(new Sundial()); // 日晷
                        }
                        if (calcCurse() >= 2) {
                            lst.add(new BlueCandle()); // 蓝蜡烛
                        }
                        if (AbstractDungeon.player.currentHealth < AbstractDungeon.player.maxHealth * 0.3) {
                            lst.add(new MeatOnTheBone()); // 带骨肉
                        }
                        if (AbstractDungeon.player.maxHealth < 50) {
                            lst.add(new Pear()); // 梨
                        }
                        if (AbstractDungeon.player.hasRelic(CursedKey.ID)) {
                            lst.add(new DarkstonePeriapt()); // 黑石护符
                        }
                        if (calcStrike() >= 5) {
                            lst.add(new StrikeDummy()); // 打击木偶
                        }
                        if (AbstractDungeon.player.gold >= 500) {
                            lst.add(new Courier()); // 送货员
                        }
                        if (AbstractDungeon.actNum == 1 && (AbstractDungeon.player instanceof TheSilent || AbstractDungeon.player.getClass().getName().contains("RhineLab"))) {
                            lst.add(new ToxicEgg2()); // 毒素之蛋
                        }
                        if (AbstractDungeon.actNum == 1 && (AbstractDungeon.player instanceof Ironclad)) {
                            lst.add(new MoltenEgg2()); // 熔火之蛋
                        }
                        if (AbstractDungeon.actNum == 1 && (AbstractDungeon.player instanceof Defect || AbstractDungeon.player.getClass().getName().contains("Nearl"))) {
                            lst.add(new FrozenEgg2()); // 冻结之蛋
                        }
                        if (EncounterMod.ideaCount >= 4) {
                            lst.add(new RevenantRemains()); // 死魂灵残躯
                        }
                        if (calcPowerCard() >= AbstractDungeon.player.masterDeck.size() * 0.3) {
                            lst.add(new MummifiedHand()); // 干瘪之手
                        }
                        if (calcSkillCard() >= AbstractDungeon.player.masterDeck.size() * 0.7) {
                            lst.add(new LetterOpener()); // 开信刀
                        }
                        if (calcZeroCostCard() >= 4) {
                            lst.add(new Shuriken()); // 手里剑
                            lst.add(new Kunai()); // 苦无
                            lst.add(new OrnamentalFan()); // 精致折扇
                        }
                        if (calcNonAtkSkiCard() <= AbstractDungeon.player.masterDeck.size() * 0.3 && AbstractDungeon.player instanceof Watcher) {
                            lst.add(new Duality()); // 两仪
                        }
                        if (AbstractDungeon.player.masterDeck.size() >= 25) {
                            lst.add(new EternalFeather()); // 永恒羽毛
                        }
                        break;
                    case RARE:
                        if (calcExhaust() >= 5) {
                            lst.add(new DeadBranch()); // 枯木树枝
                        }
                        if (calcCurse() >= 2) {
                            lst.add(new DuVuDoll()); // 巫毒娃娃
                        }
                        if (AbstractDungeon.player.currentHealth < AbstractDungeon.player.maxHealth * 0.3) {
                            lst.add(new LizardTail()); // 蜥蜴尾巴
                        }
                        if (AbstractDungeon.player.maxHealth < 50) {
                            lst.add(new Mango()); // 芒果
                        }
                        if (EncounterMod.ideaCount <= 1 && AbstractDungeon.floorNum <= 25) {
                            lst.add(new SpiritHunterEarl()); // 探灵伯爵
                        }
                        if (calcDiscard() >= 3) {
                            lst.add(new ToughBandages()); // 结实绷带
                        }
                        if (calcExhaustOthers() >= 3 && AbstractDungeon.player instanceof Ironclad) {
                            lst.add(new CharonsAshes()); // 卡戎之灰
                        }
                        if (calcPowerCard() >= AbstractDungeon.player.masterDeck.size() * 0.3) {
                            lst.add(new BirdFacedUrn()); // 鸟面瓮
                        }
                        if (calcAverageCost() <= 0.7) {
                            lst.add(new UnceasingTop()); // 不休陀螺
                        }
                        if (calcMaxCost() >= 5) {
                            lst.add(new IceCream()); // 冰淇淋
                        }
                        if (calcRetain() >= 3) {
                            lst.add(new CloakClasp()); // 斗篷扣
                        }
                        break;
                    default:
                        return SpireReturn.Continue();
                }
                lst.removeIf(r -> AbstractDungeon.player.hasRelic(r.relicId));
                if (lst.isEmpty()) {
                    lst.add(new CultistMask()); // 邪教徒头套
                }
                return SpireReturn.Return(lst.get(AbstractDungeon.relicRng.random(lst.size() - 1)));
            }
            return SpireReturn.Continue();
        }

        private static int calcExhaust() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.rawDescription.contains("消耗") || c.rawDescription.contains("Exhaust") || c.rawDescription.contains("廃棄"))
                    cnt++;
            return cnt;
        }

        private static int calcCurse() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.type == AbstractCard.CardType.CURSE)
                    cnt++;
            return cnt;
        }

        private static int calcStrike() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.hasTag(AbstractCard.CardTags.STRIKE))
                    cnt++;
            return cnt;
        }

        private static boolean checkPotionFull() {
            for (AbstractPotion p : AbstractDungeon.player.potions) {
                if (p instanceof PotionSlot) {
                    return false;
                }
            }
            return true;
        }

        private static int calcDiscard() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.rawDescription.contains("丢弃") ||
                        (c.rawDescription.contains("Discard") && !c.rawDescription.contains("Discard pile")) ||
                        (c.rawDescription.contains("discard") && !c.rawDescription.contains("discard pile")) ||
                        c.rawDescription.contains("丟棄") ||
                        (c.rawDescription.contains("捨て") && !c.rawDescription.contains("捨て札")))
                    cnt++;
            return cnt;
        }

        private static int calcExhaustOthers() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (((c.rawDescription.contains("消耗") || c.rawDescription.contains("Exhaust") || c.rawDescription.contains("廃棄")) && !c.exhaust) || c instanceof FiendFire)
                    cnt++;
            return cnt;
        }

        private static int calcPowerCard() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.type == AbstractCard.CardType.POWER)
                    cnt++;
            return cnt;
        }

        private static int calcSkillCard() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.type == AbstractCard.CardType.SKILL)
                    cnt++;
            return cnt;
        }

        private static int calcAttackCard() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.type == AbstractCard.CardType.ATTACK)
                    cnt++;
            return cnt;
        }

        private static int calcUnattackCard() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.type != AbstractCard.CardType.ATTACK)
                    cnt++;
            return cnt;
        }

        private static int calcNonAtkSkiCard() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.type != AbstractCard.CardType.ATTACK && c.type != AbstractCard.CardType.SKILL)
                    cnt++;
            return cnt;
        }

        private static float calcAverageCost() {
            if (AbstractDungeon.player.masterDeck.isEmpty()) return 1;
            int sum = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                sum += c.cost;
            return sum * 1.0f / AbstractDungeon.player.masterDeck.size();
        }

        private static float calcZeroCostCard() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.cardsToPreview instanceof Shiv || (c.type == AbstractCard.CardType.ATTACK &&
                        (c.rawDescription.contains("耗能") && c.rawDescription.contains("减少")) ||
                        ((c.rawDescription.contains("cost") || c.rawDescription.contains("Cost")) &&
                                (c.rawDescription.contains("less") || c.rawDescription.contains("Less") || c.rawDescription.contains("reduce") || c.rawDescription.contains("Reduce"))) ||
                        (c.rawDescription.contains("耗能") && c.rawDescription.contains("減少")) ||
                        ((c.rawDescription.contains("コスト") || c.rawDescription.contains("消費")) && (c.rawDescription.contains("下がる") || c.rawDescription.contains("-1") || c.rawDescription.contains("減少")))))
                    cnt++;
            return cnt;
        }

        private static float calcMaxCost() {
            int ret = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                ret = Math.max(ret, c.cost);
            return ret;
        }

        private static int calcRetain() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.selfRetain)
                    cnt++;
            return cnt;
        }
    }
}
