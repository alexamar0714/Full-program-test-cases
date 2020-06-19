package drinkwater.boot;

/**
 * Hello world!
 */
public class DrinkWaterBoot {

    private Main drinkWaterMain;

    public DrinkWaterBoot() {
        drinkWaterMain = new Main();
    }

    public void run() throws Exception {
        drinkWaterMain.run();
    }

    public void runFor(int millis) throws Exception {
        drinkWaterMain.setDuration(millis);
        this.run();
    }

    public void start() throws Exception {
        drinkWaterMain.start();

    }

    public void stop() throws Exception {
        drinkWaterMain.stop();
    }

    public Main getDrinkWaterMain() {
        return drinkWaterMain;
    }

    public void setDrinkWaterMain(Main drinkWaterMain) {
        this.drinkWaterMain = drinkWaterMain;
    }
}
