package ch.orioninformatique.gstocktdm;

public class Parametres {

    int id;
    String adresse;
    int port;

    public Parametres (int id, String adresse, Integer port){
        this.id = id;
        this.adresse = adresse;
        this.port = port;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
