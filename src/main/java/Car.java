public class Car implements Runnable {

    private static int CARS_COUNT;
    private static int FINISH_CARS_COUNT;

    private Race race;
    private int speed;
    private String name;
    private int finished;

    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            Main.cdl.countDown();
            System.out.println(this.name + " готов");
            Main.cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }

        FINISH_CARS_COUNT++;
        this.finished = FINISH_CARS_COUNT;
        if (FINISH_CARS_COUNT == 1) {
            System.out.println(name + " прибыл первым! - WIN");
        }
        Main.cdlEnd.countDown();
    }

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public int getFinished() {
        return finished;
    }

}
