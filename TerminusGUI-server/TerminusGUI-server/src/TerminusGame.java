import java.util.*;

public class TerminusGame {
    
    private String nick;
    private int userLoc;
    private Vector<String> userCommands = new Vector<String>();
    
    private static String[][] locs = new String[15][5];
    private static String[][][] els = new String[15][5][5];
    
    /**
     * Konstruktor klasy TerminusGame ustawiajacy nick, oraz dodajacy do zmiennej klasy Vector dostepne komendy dla gra na danym etapie gry
     * @param nick 
     */
    public TerminusGame(String nick) {
        //ustawiam nazwy lokacji
        setWorldLocations();
        
        this.nick = nick;
        userLoc = 0;
        
        // ustawiam dostepne komendy dla uzytkownika
        userCommands.add("ls");
        userCommands.add("cd");
        userCommands.add("less");
        userCommands.add("help");
        userCommands.add("exit");
        userCommands.add("rm");
        
        // wyswietlam
        System.out.println("Siema " + nick);
    }
    
    /** 
     * Publiczna metoda majaca za zadanie zwrocenie ciagu tekstowego z wprowadzeniem do gry Terminus 
    */
    public String introduction() {
        
        String start = "\n\r\n\r\n\r************************************************************\n\r\n\r";
        
        String introduction = " w grze Terminus.\n\r\n\r"
                + "Aby zobaczyc lokacje oraz elementy danego miejsca uzyj polecenia 'ls' \n\r"
                + "Aby przejsc do nowego miejsca uzyj polecenia 'cd [nazwa_lokacji]'\n\r"
                + "Aby uzyc danego przedmiotu uzyj polecenia 'less [nazwa_przedmiotu]\n\r"
                + "Aby wyjsc z gry wpisz komende 'exit'.\n\r";
        
        return start + "Witaj " + nick + introduction;
    }
    
    
    
    
    
    /**
     * Metoda zwracajaca wartosc tekstowa
     * Jej zadaniem jest przyjecie kolejnej komendy, przetworzenie jej i zwrocenie wyniku
     * @param cmd
     * @return 
     */
    public String nextCommand(String cmd) {
        
        String ret;
        
        String[] cmds = cmd.split(" ");
        int howLong = cmds.length;
        //System.out.println("howLong =  " + howLong);
        
        if (!checkSyntax(cmds[0])) {
            ret = "Blad skladni\n\r\n\r";
        } else if ("help".equals(cmds[0])) {
            ret = showHelp();
        } else if ("ls".equals(cmds[0])) {
            ret = printListOfLocations();
        } else if ("cd".equals(cmds[0])) {
            if (howLong == 2) {
                ret = moveToNewLocation(cmds[1]);
            } else {
                ret = "";
            }
        } else if ("less".equals(cmds[0])) {
            if (howLong == 2) {
                ret = useElement(cmds[1]);
            } else {
                ret = "";
            }
        } else if ("rm".equals(cmds[0])) {
            if (howLong == 2) {
                ret = removeElement(cmds[1]);
            } else {
                ret = "";
            }
        } else {
            ret = "ok";
        }
        
        return ret;
    }
    
    
    private String removeElement(String element) {
        
        for (int i = 0; i < els[userLoc].length; i++) {
            if (els[userLoc][i][0] != null && element.equals(els[userLoc][i][0]) && "1".equals(els[userLoc][i][3])) {
                els[userLoc][i][4] = "0";
                
                if ("zwalone_drzewo".equals(els[userLoc][i][0])) {
                    locs[3][4] = "1";
                }
            }
        }
        
        return "";
    }
    
    
    
    /**
     * Metoda prywatna zwracajaca jako ciag znakow, dostepne komendy dla uzytkownika
     * @return 
     */
    private String showHelp() {
        
        String result = "----\n\r";
        
        for (int i = 0; i < userCommands.size(); i++) {
            result = result + " - " + userCommands.get(i) + "\n\r";
        }
        
        return result;
    }
    
    
    
    private String useElement(String what) {
        
        String[] result = checkElement(what);
        String extra = "";
        
        if (result[3] != null && "rm".equals(result[3])) {
            if (!userCommands.contains("rm")) {
                userCommands.add("rm");
                extra = "\n\r\n\rNauczyles sie nowego czaru, wystarczy ze wpiszesz polecenie 'rm' [nazwa_elementu], a element zniknie";
            }
        }
        
        return "\n\r" + result[2] + extra + "\n\r\n\r";
    }
    
    private String[] checkElement(String what) {
        
        String[] result = new String[5];
        
        System.out.println(els[userLoc].length);
        
        for (int i = 0; i < els[userLoc].length; i++) {
            if (els[userLoc][i][0] != null && what.equals(els[userLoc][i][0])) {
                result[0] = "true";
                result[1] = els[userLoc][i][0];
                result[2] = els[userLoc][i][1];
                result[3] = els[userLoc][i][2];
            } else {
                result[0] = "false";
            }
        }
        
        return result;
    }
    
    /**
     * Metoda prywatna nie zwracajaca zadnej wartosc, ustawiajaca zmienna userLocation w zaleznosci o polozenia
     * @param where 
     */
    private String moveToNewLocation(String where) {
        
        String[] list;
        String result = "\n\r";
        
        if (where.equals("~") && userLoc != 0) {
            userLoc = 0;
            result = "wrociles do domu\n\r\n\r";
        } else {
            for (int i = 0; i < locs.length; i++) {
                if ( where.equals(locs[i][0])) {     
                    list = locs[i][2].split(",");
                    for (String l : list) {
                        if ((userLoc == Integer.parseInt(l)) && Integer.parseInt(locs[i][4]) == 1) {
                            userLoc = Integer.parseInt(locs[i][3]);
                            System.out.println("OK " + userLoc);
                            result = "\n\r" + where + "\n\r\n\r";
                        }
                    }  
                }
            }
        }
        
        return result;
    }
    
    /**
     * Metoda prywatna zwracajaca wartosc tekstowa, jej zadaniem jest wyswietlenie listy lokacji w zaleznosci od tego gdzie znajduje sie postac
     * @return 
     */
    private String printListOfLocations() {
        
        String result = "\n\r\n\r--------------------------------------------------------\n\r";
        String elements = "\n\rElementy: \n\r";
        
        for (String location[] : locs) {
            if (location[0] != null && Integer.parseInt(location[3]) == userLoc) {
                result = result + location[1] + "\n\r\n\r";
            }
        }
        
        result = result + "\n\rLokacje:\n\r";

        
        for (int i = 0; i < locs.length; i++) {
            if (locs[i][0] != null) {
                String[] list = locs[i][2].split(",");
                for (String l : list) {
                    if ((userLoc == Integer.parseInt(l)) && locs[i][4] != null && Integer.parseInt(locs[i][4]) == 1 ) {
                        result = result + " - " + locs[i][0] + "\n\r";
                    }
                }
            }
        }
        
        for (int i = 0; i < els[userLoc].length; i++) {
            if (els[userLoc][i][0] != null && "1".equals(els[userLoc][i][4])) {
                elements = elements + " - " + els[userLoc][i][0] + "\n\r";

            }
        }
        
        return result + elements;
    }
    
    /**
     * Metoda zwracajaca wartosc logiczna, jej zadaniem jest sprawdzenie skladni komendy wpisanej przez uzytkownika
     * @param cmd
     * @return 
     */
    private boolean checkSyntax(String cmd) {
     
        boolean result = false;
        
        if (userCommands.contains(cmd)) {
            result = true;
        }
        
        return result;
    }
    
    /**
     * Metoda prywatna nie zwracajaca zadnej wartosci, jej zadaniem jest ustawienie tablic lokacji i elementow
     */
    private void setWorldLocations() {
        
        // [0] nazwa lokacji
        // [1] opis, dialog, whatever
        // [2] lista lokalizacji z ktorych dostepna jest dana lokalizacja
        // [3] numer danej lokalizacji
        // [4] dostepnosc lokalizacji 0 - nie, 1 - tak
        
        locs[0][0] = "dom";        
        locs[0][1] = "Stoisz przed swoim domem, znajdujacym sie poza miastem.\n\rNiedaleko znajduje sie strumien, ktory kojaco szumi,\n\rna poludniu, pare kilometrow dalej, znajduje sie Akademia Magii.";    
        locs[0][2] = "1,2,3";
        locs[0][3] = "0";
        locs[0][4] = "1";

        locs[1][0] = "akademia";
        locs[1][1] = "Znajdujesz sie w Akademii Magii i Czarodziejstwa";
        locs[1][2] = "0,2,5,6";
        locs[1][3] = "1";
        locs[1][4] = "1";
        
        locs[2][0] = "rzeka";
        locs[2][1] = "Jestes nad rzeka, widzisz uszkodzony most, lepiej na niego nie wchodzic";
        locs[2][2] = "0,1";
        locs[2][3] = "2";
        locs[2][4] = "1";
        
        locs[3][0] = "mroczna_droga";
        locs[3][1] = "Mroczna droga prowadzaca w gory";
        locs[3][2] = "0,4";
        locs[3][3] = "3";
        locs[3][4] = "0";
        
        locs[4][0] = "jaskinia";
        locs[4][1] = "Znajdujesz sie w ciemnej, mrocznej jaskini";
        locs[4][2] = "3";
        locs[4][3] = "4";
        locs[4][4] = "1";
        
        locs[5][0] = "sala_wykladowa";
        locs[5][1] = "Wchodzisz do sali wykladowej posrodku ktorej stoi dostojny sedziwy mag";
        locs[5][2] = "1";
        locs[5][3] = "5";
        locs[5][4] = "1";
        
        locs[6][0] = "sala_treningowa";
        locs[6][1] = "Wchodzisz do sali treningowej w ktorej adepci magii czwicza nowa zaklecia";
        locs[6][2] = "1";
        locs[6][3] = "6";
        locs[6][4] = "1";
        
        
        
        
        // [0] - nazwa
        // [1] - opis
        // [2] - mozliwy czar do nauczenia sie
        // [3] - usuwalnosc 0 - nie, 1 - tak
        // [4] - widocznosc 0 - nie, 1 - tak
        
        els[0][0][0] = "stary_czlowiek";
        els[0][0][1] = "Witaj, czy nie wiesz jak dostac sie w gory?\n\rWidze ze niestety drzewo bloku droge... ohh nie wiesz co z tym zrobic, no nic poczekam, moze cos wymyslisz\n\rCzy slyszales, ze w Akademii Magii udzielaja dzisiaj darmowych lekcji, moze powinienes sie tam udac.";
        els[0][0][2] = "null";
        els[0][0][3] = "0";
        els[0][0][4] = "1";
        
        els[0][1][0] = "zwalone_drzewo";
        els[0][1][1] = "Drzewo najwyrazniej zwalilo sie niedawno\n\rNiestety upadajac zatarasowalo droga w gory\n\rMoze udalo by sie je jakos przesunac?";
        els[0][1][2] = "null";
        els[0][1][3] = "1";
        els[0][1][4] = "1";
        
        els[2][0][0] = "uszkodzony_most";
        els[2][0][1] = "Uszkodzony most, lepiej na niego nie wchodzic,\n\rw kazdej chwili moze sie zawalic... hmmm cos napewno da sie z nim zrobic...";
        els[2][0][2] = "null";
        els[2][0][3] = "0";
        els[2][0][4] = "1";
        
        els[4][0][0] = "tajemnicza_skrzynia";
        els[4][0][1] = "Ostroznie otwierasz stara zelazna skrzynie\n\rW srodku znajdujesz zwoj pergaminu na ktorym zostalo napisane: \n\r'Jezeli pragniesz poznac prawde kryjaca sie za zwojem nalezy rzucic zaklecie\n\rwyswietlenia prawdy, a znaki zamienia sie w obrazy'.\n\r";
        els[4][0][2] = "null";
        els[4][0][3] = "1";
        els[4][0][4] = "1";
        
        els[5][0][0] = "nauczyciel";
        els[5][0][1] = "Witaj, mlody czlowieku\n\rChcesz sie uczyc magii prawa?\n\rTaak teraz Cie poznaje mieszkasz w tym domku niedaleko Akademii\n\rDobrze zacznijmy lekcje wiec\n\rDzisiaj sprobujemy nauczyc sie czaru dzieki ktoremu bedziesz mogl unicestwic wybrane przez Ciebie elementy\n\r(po godzinie nauki zaczynasz lapac o co w tym chodzi)\n\rSwietnie, swietnie o to chodzi, moze kiedy bedzie z Ciebie wielki czarodziej.\n\r";
        els[5][0][2] = "rm";
        els[5][0][3] = "0";
        els[5][0][4] = "1";
        
        els[6][0][0] = "pudelko#1";
        els[6][0][1] = "Pudelko przeznaczone do cwiczenia czarow usuwania.\n\r";
        els[6][0][2] = "null";
        els[6][0][3] = "1";
        els[6][0][4] = "1";
        
        els[6][1][0] = "pudelko#2";
        els[6][1][1] = "Pudelko przeznaczone do cwiczenia czarow usuwania.\n\r";
        els[6][1][2] = "null";
        els[6][1][3] = "1";
        els[6][1][4] = "1";
        
        els[6][2][0] = "pudelko#3";
        els[6][2][1] = "Pudelko przeznaczone do cwiczenia czarow usuwania.\n\r";
        els[6][2][2] = "null";
        els[6][2][3] = "1";
        els[6][2][4] = "1";
        

        
    }
    
    
}
