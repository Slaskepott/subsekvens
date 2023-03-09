import java.util.concurrent.CountDownLatch;

class Test {
    public static void main(String[] args) {
        CountDownLatch ferdigFletteSignal = new CountDownLatch(FletteTrad.ANTALL_TRADER);
        CountDownLatch ferdigLeseSignal = new CountDownLatch(LeseTrad.ANTALL_TRADER);

        Monitor2 monitor = new Monitor2("testdatalitenlike");//Lager nytt Monitor-objekt med ArrayList over filnavn, som er hentet fra metadata.csv

        //Start lesetråder
        for(int i=0;i<LeseTrad.ANTALL_TRADER;i++){
            LeseTrad nyTrad = new LeseTrad(monitor,ferdigLeseSignal);
            Thread leseTrad = new Thread(nyTrad);
            leseTrad.setName("Lesetraad " + i);
            leseTrad.start();
        }

        //Start flettetråder
        for(int i=0;i<FletteTrad.ANTALL_TRADER;i++){
            FletteTrad nyTrad = new FletteTrad(monitor,ferdigFletteSignal);
            Thread fletteTrad = new Thread(nyTrad);
            fletteTrad.setName("Flettetraad " + i);
            fletteTrad.start();
        }

        //Vent på at alle lesetråder er døde
        try{
            ferdigLeseSignal.await();
            System.out.println("Lesing ferdig.");
        }catch(InterruptedException e){
            System.out.println(e);
        }
        monitor.settLeseTraderFerdig(true);
        //Her kjører 8 flettetråder

        //Vent på at alle flettetråder er døde
        try{
            ferdigFletteSignal.await();//hit kommer vi
            System.out.println("Fletting ferdig");//hit kommer vi ikke.
        }catch(InterruptedException e){
            System.out.println(e);
        }

        //Print resultat
        System.out.println(monitor);
    }
}

