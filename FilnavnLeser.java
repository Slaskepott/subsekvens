import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

class FilnavnLeser {
    public static ArrayList<String> finnFilnavn(String mappenavn){
        ArrayList<String> filnavnListe = new ArrayList<>();
        try{
            Scanner sc = new Scanner(new File("./" + mappenavn + "/metadata.csv"));
            while(sc.hasNextLine()){
                filnavnListe.add("./"+ mappenavn + "/" + sc.nextLine());
            }
        }catch(FileNotFoundException e){
            System.out.println("Fant ikke fil.");
        }
        System.out.println(filnavnListe);
        return filnavnListe;
    }    
}
