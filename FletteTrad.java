import java.util.concurrent.CountDownLatch;

public class FletteTrad implements Runnable {
    public final static int ANTALL_TRADER = 8;
    private Monitor2 monitor;
    CountDownLatch ferdigFletteSignal;

    public FletteTrad(Monitor2 monitor, CountDownLatch ferdigFletteSignal){
        this.monitor = monitor;
        this.ferdigFletteSignal = ferdigFletteSignal;
    }

    public void run(){
        System.out.println(Thread.currentThread().getName() + " starter.");
        //Flettetråden er ferdig når lesetrådene er ferdige og det bare er ett hashmap igjen.
        System.out.println("Tester conditions. Lesetraader ferdig: " + monitor.leseTraderFerdig() + ". Har bare ett kart: " + monitor.harBareEttKart());
        while(!(monitor.leseTraderFerdig() && monitor.harBareEttKart())){
            try{
                monitor.flett();
            }
            catch(InterruptedException e){
                System.out.println(e);
            }
        }
        ferdigFletteSignal.countDown();

        System.out.println(Thread.currentThread().getName() + " ferdig.");
    }
}
