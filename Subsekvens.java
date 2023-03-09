class Subsekvens{
    private int antall = 1;
    public String data;

    public Subsekvens(String subsekvens){
        this.data = subsekvens;
    }

    public int hentAntall(){
        return antall;
    }

    public void settAntall(int antall){
        this.antall = antall;
    }

    public String hentData(){
        return data;
    }

    @Override
    public String toString(){
        return "("+data+","+antall+")";
    }

}