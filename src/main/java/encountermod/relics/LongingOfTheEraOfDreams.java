package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.*;
import com.megacrit.cardcrawl.cards.colorless.Bite;
import com.megacrit.cardcrawl.cards.green.*;
import com.megacrit.cardcrawl.cards.red.*;
import com.megacrit.cardcrawl.cards.tempCards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.shop.ShopScreen;
import encountermod.EncounterMod;
import encountermod.patches.RefreshPatch;
import encountermod.reward.ExtraRelicReward;

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
        RefreshPatch.roomWeight.put("Treasure", 9);
        RefreshPatch.totalWeight = 19;
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

    @Override
    public void onChestOpenAfter(boolean bossChest) {
        this.flash();
        this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        AbstractDungeon.getCurrRoom().rewards.add(new ExtraRelicReward(UnseenRelic()));
        AbstractDungeon.getCurrRoom().rewards.add(new ExtraRelicReward(UnseenRelic()));
    }

    AbstractRelic UnseenRelic() {
        ArrayList<String> list = new ArrayList<>();
        RelicTier t = AbstractDungeon.returnRandomRelicTier();
        RelicLibrary.populateRelicPool(list, t, AbstractDungeon.player.chosenClass);
        list.removeIf(r -> AbstractDungeon.player.hasRelic(r));
        for (RewardItem reward: AbstractDungeon.getCurrRoom().rewards) {
            if (reward.type == RewardItem.RewardType.RELIC) {
                list.removeIf(r -> reward.relic.relicId.equals(r));
            }
        }
        if (list.isEmpty()) {
            list.add(CultistMask.ID);
        }
        return RelicLibrary.getRelic(list.get(AbstractDungeon.relicRng.random(list.size() - 1))).makeCopy();
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
                        if (AbstractDungeon.player.hasRelic(WhiteBeast.ID)) {
                            lst.add(new ToyOrnithopter()); // 玩具扑翼飞机
                        }
                        if (AbstractDungeon.player instanceof Watcher && calcMantra() > 0) {
                            lst.add(new Damaru()); // 手摇鼓
                        }
                        if (ShopScreen.actualPurgeCost >= 175) {
                            lst.add(new SmilingMask()); // 微笑面具
                        }
                        if (calcUnupgradeAttack() >= 4) {
                            lst.add(new Whetstone()); // 磨刀石
                        }
                        if (calcUnupgradeSkill() >= 4) {
                            lst.add(new WarPaint()); // 战纹涂料
                        }
                        if (AbstractDungeon.player instanceof Defect && calcChannel() >= 3) {
                            lst.add(new DataDisk()); // 数据磁盘
                        }
                        if (AbstractDungeon.actNum == 1) {
                            lst.add(new CeramicFish()); // 陶瓷小鱼
                        }
                        if (calBlockCard() >= AbstractDungeon.player.masterDeck.size() * 0.4) {
                            lst.add(new OddlySmoothStone()); // 意外光滑的石头
                        }
                        if (calcPoisonTime() >= 16) {
                            lst.add(new SneckoSkull()); // 异蛇头骨
                        }
                        if (calcLittleDamage() >= 4) {
                            lst.add(new Boot()); // 发条靴
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
                        if ((AbstractDungeon.actNum <= 2 && AbstractDungeon.player instanceof Defect) || (AbstractDungeon.actNum == 1 && AbstractDungeon.player.getClass().getName().contains("Nearl"))) {
                            lst.add(new FrozenEgg2()); // 冻结之蛋
                        }
                        if (EncounterMod.ideaCount >= 4) {
                            lst.add(new RevenantRemains()); // 死魂灵残躯
                        }
                        if (calcPowerCard() >= AbstractDungeon.player.masterDeck.size() * 0.2) {
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
                        if (AbstractDungeon.player instanceof Defect && checkSymbioticVirus()) {
                            lst.add(new SymbioticVirus()); // 共生病毒
                        }
                        if (AbstractDungeon.player instanceof TheSilent && checkNinjaScroll()) {
                            lst.add(new NinjaScroll()); // 忍术卷轴
                        }
                        if (AbstractDungeon.actNum < 24) {
                            lst.add(new SingingBowl()); // 颂钵
                            lst.add(new QuestionCard()); // 问号牌
                        }
                        if (AbstractDungeon.player instanceof Defect && checkCables()) {
                            lst.add(new GoldPlatedCables()); // 镀金缆线
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
                        if (EncounterMod.ideaCount <= 1 && AbstractDungeon.floorNum < 24) {
                            lst.add(new SpiritHunterEarl()); // 探灵伯爵
                        }
                        if (calcDiscard() >= 3) {
                            lst.add(new ToughBandages()); // 结实绷带
                            lst.add(new Tingsha()); // 铜钹
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
                        if (AbstractDungeon.actNum <= 2 && Settings.hasRubyKey) {
                            lst.add(new Girya()); // 壶铃
                            lst.add(new Shovel()); // 铲子
                        }
                        if (AbstractDungeon.floorNum < 24) {
                            lst.add(new PrayerWheel()); // 转经轮
                        }
                        if (AbstractDungeon.player.gold <= 300) {
                            lst.add(new OldCoin()); // 古钱币
                        }
                        if (AbstractDungeon.floorNum < 40) {
                            lst.add(new WingBoots()); // 羽翼之靴
                        }
                        if (AbstractDungeon.player instanceof Ironclad && checkMagicFlower()) {
                            lst.add(new MagicFlower()); // 魔法花
                        }
                        if (calcPoisonNum() >= 16) {
                            lst.add(new TheSpecimen()); // 生物样本
                        }
                        if (calcScry() >= 4) {
                            lst.add(new GoldenEye()); // 黄金眼
                        }
                        break;
                    default:
                        AbstractDungeon.getCurrRoom().addRelicToRewards(new RedCirclet());
                        return SpireReturn.Continue();
                }
                lst.removeIf(r -> AbstractDungeon.player.hasRelic(r.relicId));
                if (lst.isEmpty()) {
                    AbstractDungeon.getCurrRoom().addRelicToRewards(new CultistMask());
                    return SpireReturn.Continue();
//                    lst.add(new CultistMask()); // 邪教徒头套
                }
                AbstractRelic spawnRelic = lst.get(AbstractDungeon.relicRng.random(lst.size() - 1));
                if (tier == RelicTier.COMMON) {
                    AbstractDungeon.commonRelicPool.remove(spawnRelic.relicId);
                }
                if (tier == RelicTier.UNCOMMON) {
                    AbstractDungeon.uncommonRelicPool.remove(spawnRelic.relicId);
                }
                if (tier == RelicTier.RARE) {
                    AbstractDungeon.rareRelicPool.remove(spawnRelic.relicId);
                }
                return SpireReturn.Return(spawnRelic);
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
                if ((c.cardsToPreview instanceof Shiv && !(c instanceof Accuracy)) || (c.type == AbstractCard.CardType.ATTACK &&
                        ((c.rawDescription.contains("耗能") && c.rawDescription.contains("减少")) ||
                        ((c.rawDescription.contains("cost") || c.rawDescription.contains("Cost")) &&
                                (c.rawDescription.contains("less") || c.rawDescription.contains("Less") || c.rawDescription.contains("reduce") || c.rawDescription.contains("Reduce"))) ||
                        (c.rawDescription.contains("耗能") && c.rawDescription.contains("減少")) ||
                        ((c.rawDescription.contains("コスト") || c.rawDescription.contains("消費")) && (c.rawDescription.contains("下がる") || c.rawDescription.contains("-1") || c.rawDescription.contains("減少"))) ||
                        c.cost == 0)))
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

        private static int calcMantra() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.rawDescription.contains("真言") || c.rawDescription.contains("Mantra") || c.rawDescription.contains("マントラ"))
                    cnt++;
            return cnt;
        }

        private static int calcUnupgradeAttack() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.type == AbstractCard.CardType.ATTACK && !c.upgraded)
                    cnt++;
            return cnt;
        }

        private static int calcUnupgradeSkill() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.type == AbstractCard.CardType.SKILL && !c.upgraded)
                    cnt++;
            return cnt;
        }

        private static int calcChannel() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if ((c.rawDescription.contains("生成") && c.rawDescription.contains("充能球") && !(c instanceof Blizzard)) ||
                        (c.rawDescription.contains("Channel") && !c.rawDescription.contains("Channeled")) ||
                        (c.rawDescription.contains("生成") && !(c instanceof Blizzard) &&
                                (c.rawDescription.contains("ライトニング") /* 闪电 */ ||
                                c.rawDescription.contains("プラズマ") /* 等离子 */ ||
                                c.rawDescription.contains("フロスト") /* 冰霜 */ ||
                                c.rawDescription.contains("ダーク") /* 黑暗 */)))
                    cnt++;
            return cnt;
        }

        private static boolean checkSymbioticVirus() {
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if ((c instanceof Darkness && c.upgraded) || (c instanceof Recursion) || (c instanceof Loop))
                    return true;
            return false;
        }

        private static boolean checkNinjaScroll() {
            if (AbstractDungeon.player.hasRelic(BagOfPreparation.ID)) return false;
            if (AbstractDungeon.player.hasRelic(Shuriken.ID)) return true;
            if (AbstractDungeon.player.hasRelic(OrnamentalFan.ID)) return true;
            if (AbstractDungeon.player.hasRelic(Kunai.ID)) return true;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c instanceof Accuracy)
                    return true;
            return false;
        }

        private static boolean checkMagicFlower() {
            if (AbstractDungeon.player.hasRelic(BlackBlood.ID)) return true;
            if (AbstractDungeon.player.hasRelic(MeatOnTheBone.ID)) return true;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c instanceof Reaper || c instanceof Bite)
                    return true;
            return false;
        }

        private static int calBlockCard() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.baseBlock != -1)
                    cnt++;
            return cnt;
        }

        private static int calcPoisonNum() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c instanceof BouncingFlask) cnt += 3 * c.baseMagicNumber;
                if (c instanceof CorpseExplosion || c instanceof CripplingPoison || c instanceof DeadlyPoison || c instanceof PoisonedStab) cnt += c.baseMagicNumber;
                if (c instanceof Envenom || c instanceof NoxiousFumes) cnt += 16;
            }
            return cnt;
        }

        private static int calcPoisonTime() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c instanceof BouncingFlask) cnt += 3;
                if (c instanceof CorpseExplosion || c instanceof CripplingPoison || c instanceof DeadlyPoison || c instanceof PoisonedStab || c instanceof Catalyst) cnt++;
                if (c instanceof Envenom || c instanceof NoxiousFumes) cnt += 3;
            }
            return cnt;
        }

        private static int calcLittleDamage() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if ((c.cardsToPreview instanceof Shiv && !(c instanceof Accuracy)) || (c.type == AbstractCard.CardType.ATTACK && c.baseDamage >= 0 && c.baseDamage <= 4))
                    cnt++;
            return cnt;
        }

        private static int calcScry() {
            int cnt = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.rawDescription.contains("预见 !M!") || c.rawDescription.contains("Scry !M!") || c.rawDescription.contains("預見 !M!") || c.rawDescription.contains("占術 !M!"))
                    cnt++;
            return cnt;
        }

        private static boolean checkCables() {
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c instanceof Loop || c.rawDescription.contains("获得 !M! 点 集中") || c.rawDescription.contains("Gain !M! Focus") || c.rawDescription.contains("獲得 !M! 點 集中") || c.rawDescription.contains("集中力 !M! を得る"))
                    return true;
            return false;
        }
    }


}
