import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Realm {
    private static BufferedReader br;
    private static FantasyCharacter player = null;
    private static BattleScene battleScene = null;
    public static void main(String[] args) {
        br = new BufferedReader(new InputStreamReader(System.in));
        battleScene = new BattleScene();
        System.out.println("Дайте имя своему герою");
        try {
            command(br.readLine());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    private static  void command(String string) throws IOException {
        if (player == null){
            player = new Hero(
                    string,
                    100,
                    20,
                    20,
                    0,
                    0


            );
            System.out.printf("Миру угрожает злой лич! %s пожалуйста спаси нашу деревню! Его приспешников видели в темном лесу!%n", player.getName());
            printNavigation();
        }
        switch (string) {
            case "1" -> {
                System.out.println("Караван торговца задерживается.Возможно стоит зайти позже");
                command(br.readLine());
            }
            case "2" -> commitFight();
            case "3" -> System.exit(1);
            case "да" -> command("2");
            case "нет" -> {
                printNavigation();
                command(br.readLine());
            }
        }
        command(br.readLine());
    }
    private static void printNavigation() {
        System.out.println("Куда вы хотите пойти?");
        System.out.println("1. К Торговцу");
        System.out.println("2. В темный лес");
        System.out.println("3. Выход");
    }
    private static void commitFight() {
        battleScene.fight(player, createMonster(), new FightCallback() {
            @Override
            public void fightWin() {
                System.out.printf("%s победил! Теперь у вас %d опыта и %d золота, а также осталось %d единиц здоровья.%n", player.getName(), player.getXp(), player.getGold(), player.getHealthPoints());
                System.out.println("Желаете продолжить поход ? (да/нет)");
                try {
                    command(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fightLost() {

            }
        });
    }
    interface FightCallback {
        void fightWin();
        void fightLost();
    }
    public abstract static class FantasyCharacter implements Fighter{
        private String name;
        private int healthPoints;
        private int strength;
        private int dexterity;
        private int xp;
        private int gold;
        public FantasyCharacter(String name, int healthPoints, int strength, int dexterity, int xp, int gold){
            this.setName(name);
            this.setHealthPoints(healthPoints);
            this.setStrength(strength);
            this.setDexterity(dexterity);
            this.setXp(xp);
            this.setGold(gold);
        }
        private int attack() {
            if (getDexterity() * 3 > getRandomValue()) return getStrength();
            else return  0;
        }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getHealthPoints() {
            return healthPoints;
        }

        public void setHealthPoints(int healthPoints) {
            this.healthPoints = healthPoints;
        }

        public int getStrength() {
            return strength;
        }

        public void setStrength(int strength) {
            this.strength = strength;
        }

        public int getDexterity() {
            return dexterity;
        }

        public void setDexterity(int dexterity) {
            this.dexterity = dexterity;
        }

        public int getXp() {
            return xp;
        }

        public void setXp(int xp) {
            this.xp = xp;
        }

        public int getGold() {
            return gold;
        }

        public void setGold(int gold) {
            this.gold = gold;
        }
        private int getRandomValue() {
            return (int) (Math.random() * 100);
        }
        @Override
        public String toString(){
            return String.format("%s здоровье:%d",name, healthPoints);
        }
    }
    public interface Fighter {
    }
    public static class Hero extends FantasyCharacter {

        public Hero(String name, int healthPoints, int strength, int dexterity, int xp, int gold) {
            super(name, healthPoints, strength, dexterity, xp, gold);
        }
    }
    public static class Goblin extends FantasyCharacter {
        public Goblin(String name, int healthPoints, int strength, int dexterity, int xp, int gold) {
            super(name, healthPoints, strength, dexterity, xp, gold);
        }
    }
    public static class Skeleton extends FantasyCharacter {
        public Skeleton(String name, int healthPoints, int strength, int dexterity, int xp, int gold) {
            super(name, healthPoints, strength, dexterity, xp, gold);
        }
    }
    private static FantasyCharacter createMonster(){
        int random = (int) (Math.random() * 10);
        if (random % 2 == 0) return new Goblin(
                "Гоблин",
                50,
                10,
                10,
                100,
                20
        );
        else return new Skeleton(
                "Скелет",
                25,
                20,
                20,
                100,
                10
        );

    }
    public static class BattleScene{
        public void fight(FantasyCharacter hero, FantasyCharacter monster, Realm.FightCallback fightCallback) {
            Runnable runnable = () -> {
                int turn = 1;
                boolean isFightEnded = false;
                while (!isFightEnded) {
                    System.out.println("----Ход: " + turn + "----");
                    if (turn++ % 2 != 0) {
                        isFightEnded = makeHit(monster, hero, fightCallback);
                    } else {
                        isFightEnded = makeHit(hero, monster, fightCallback);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
        private Boolean makeHit(FantasyCharacter defender, FantasyCharacter attacker, Realm.FightCallback fightCallback) {
            int hit = attacker.attack();
            int defenderHealth = defender.getHealthPoints() - hit;
            if (hit != 0) {
                System.out.printf("%s Нанес удар в %d единиц!%n", attacker.getName(), hit);
                System.out.printf("У %s осталось %d единиц здоровья...%n", defender.getName(), defenderHealth);
            } else {
                System.out.printf("%s промахнулся!%n", attacker.getName());
            }
            if (defenderHealth <= 0 && defender instanceof Hero) {
                System.out.println("Извините, вы пали в бою...");
                fightCallback.fightLost();
                return true;
            } else if (defenderHealth <= 0) {
                System.out.printf("Враг повержен! Вы получаете %d опыт и %d золота%n", defender.getXp(), defender.getGold());
                attacker.setXp(attacker.getXp() + defender.getXp());
                attacker.setGold(attacker.getGold() + defender.getGold());
                fightCallback.fightWin();
                return true;
            } else {
                defender.setHealthPoints(defenderHealth);
                return false;
            }
        }
    }

}