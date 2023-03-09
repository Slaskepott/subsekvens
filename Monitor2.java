import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.locks.*;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;


class Monitor2 {
    private ArrayList<HashMap<String,Subsekvens>> register = new ArrayList<>();
    private Lock laas = new ReentrantLock(true);
    private Condition ikkeTom = laas.newCondition();
    private static final int SUBSEKVENS_LENGDE = 3;
    private static final int PRINTSTRENG_BREDDE = 15;
    private ArrayList<String> filnavnRegister;
    private boolean leseTraderFerdig = false;
    
    public Monitor2(String mappenavn){
        filnavnRegister = FilnavnLeser.finnFilnavn(mappenavn);
    }

    public boolean harBareEttKart(){
        return register.size() == 1;
    }

    public boolean leseTraderFerdig(){
        return leseTraderFerdig;
    }

    public void settLeseTraderFerdig(boolean sant){
        laas.lock();
        try{
            leseTraderFerdig = sant;
        }
        finally{
            laas.unlock();
        }
    }

    public String hentFilnavn(){
        laas.lock();
        try{
            if(filnavnRegister.size()>0){
                return filnavnRegister.remove(0);
            }
        } 
        finally{
            laas.unlock();
        }
        return null;
    }

    public void flett() throws InterruptedException{
        laas.lock();
        //Finnes ikke kart å flette
        while(register.size()<2){
            ikkeTom.await();
        }

        //Finnes kart å flette
        try{
            if(register.size()>=2){
                HashMap<String,Subsekvens> kart1 = taUtTilfeldig();
                HashMap<String,Subsekvens> kart2 = taUtTilfeldig();
                register.add(flettKart(kart1,kart2));
                ikkeTom.signalAll();
            }
        }
        finally{
            laas.unlock();
        }
    }

    private HashMap<String,Subsekvens> taUtTilfeldig(){
        laas.lock();
        try{
            return register.remove(tilfeldigTall(0,register.size()));
        }
        finally{
            laas.unlock();
        }
    }

    private int tilfeldigTall(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private HashMap<String,Subsekvens> flettKart(HashMap<String,Subsekvens> ettKart,HashMap<String,Subsekvens> annetKart){
        HashMap<String,Subsekvens> nyttKart = new HashMap<>();
        //Kopier over verdier fra første kart
        for(String s : ettKart.keySet()){
            nyttKart.put(s,ettKart.get(s));
        }

        for(String s : annetKart.keySet()){
            if(nyttKart.containsKey(s)){
                nyttKart.get(s).settAntall(annetKart.get(s).hentAntall()+nyttKart.get(s).hentAntall());
            }
            else{
                nyttKart.put(s,annetKart.get(s));
            }
        }
        return nyttKart;
    }

    public void leggTil(String filnavn){
        laas.lock();
        try{
            register.add(lesFraFil(filnavn));
            if(register.size()>=2){
                ikkeTom.signalAll();
            }
        }
        finally{
            laas.unlock();
        }
    }

    public static HashMap<String,Subsekvens> lesFraFil(String filnavn){
        //Lag scanner
        Scanner fil = null;
        try{
            fil = new Scanner(new File(filnavn));
        }catch(FileNotFoundException e){
            System.out.println(e);
        }
        //Lag kart
        HashMap<String,Subsekvens> kart = new HashMap<>();
        if(fil==null){
            return kart;
        }
        while(fil.hasNextLine()){
            //Les datalinje
            String data = fil.nextLine();
            System.out.println("Leser linje " + data + " i " + filnavn);
            for(int i = 0;i<data.length()-SUBSEKVENS_LENGDE+1;i++){
                String sekvens = data.substring(i,i+3);
                //Sjekk om ny
                boolean ny = true;
                for(String subsekvens : kart.keySet()){
                    if(subsekvens.equals(sekvens)){
                        ny=false;
                    }
                }
                //Legg til i kart
                if(ny){
                    kart.put(sekvens,new Subsekvens(sekvens));
                }
            }
        }
        return kart;
    }

    @Override
    public String toString(){
        String printStreng = "Oversikt over innhold i register:\n";
        int subsekvensTeller = 0;
        int kartTeller = 0;
        for(HashMap<String,Subsekvens> kart : register){
            printStreng += "\nKart nr. " + (kartTeller+1) + ":\n";
            for(String nokkel : kart.keySet()){
                printStreng += kart.get(nokkel).toString();
                if(subsekvensTeller==PRINTSTRENG_BREDDE){
                    printStreng += "\n";
                    subsekvensTeller = 0;
                }
                subsekvensTeller++;
            }
            printStreng += "\n Flest forekomster: " + flestForekomster(kart);
            kartTeller++;
        }
        return printStreng;
    }

    private String flestForekomster(HashMap<String,Subsekvens> kart){
        Subsekvens storst = null;

        for(String nokkel : kart.keySet()){
            Subsekvens sekvens = kart.get(nokkel);
            if(storst==null){
                storst = sekvens;
            }
            else{
                if(sekvens.hentAntall()>storst.hentAntall()){
                    storst = sekvens;
                }
            }
        }
        return storst.toString();
    }

}
