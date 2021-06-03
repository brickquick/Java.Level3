public class Tunnel extends Stage {

    public Tunnel() {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
    }

    @Override
    public void go(Car car) {
        try {
            if (Main.smp.hasQueuedThreads()) {
                System.out.println(car.getName() + " готовится к этапу(ждет): " + description);
            }
            Main.smp.acquire();
            System.out.println(car.getName() + " начал этап: " + description);
            Thread.sleep(length / car.getSpeed() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println(car.getName() + " закончил этап: " + description);
            Main.smp.release();
        }
    }

}
