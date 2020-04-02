package ch.orioninformatique.gstocktdm;

public class Inventaire {
    int id;
    String ean;
    String numero;
    String designation;
    int qt;
    int qtstock;

    public Inventaire (String ean,String numero,String designation,Integer qt,Integer qtstock){
        this.designation = designation;
        this.ean = ean;
        this.numero = numero;
        this.qt = qt;
        this.qtstock = qtstock;
    }

    public Inventaire (int id, String ean, String numero, String designation, Integer qt, Integer qtstock){
        this.id = id;
        this.designation = designation;
        this.ean = ean;
        this.numero = numero;
        this.qt = qt;
        this.qtstock = qtstock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public int getQt() {
        return qt;
    }

    public void setQt(Integer qt) {
        this.qt = qt;
    }

    public int getQtstock() {
        return qtstock;
    }

    public void setQtstock(Integer qtstock) {
        this.qtstock = qtstock;
    }



}
