import java.util.concurrent.CountDownLatch;

public class LeseTrad implements Runnable {
    public final static int ANTALL_TRADER = 8;
    private Monitor2 monitor;
    CountDownLatch ferdigLeseSignal;
    private String filnavn;

    public LeseTrad(Monitor2 monitor,CountDownLatch ferdigLeseSignal){
        this.monitor = monitor;
        filnavn = monitor.hentFilnavn();
        this.ferdigLeseSignal = ferdigLeseSignal;
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " starter.");

        while(filnavn!=null){
            monitor.leggTil(filnavn);
            filnavn = monitor.hentFilnavn();
        }
        ferdigLeseSignal.countDown();

        System.out.println(Thread.currentThread().getName() + " ferdig.");
    }

}
