/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ch.orioninformatique.gstocktdm;

public class Commandes {
        int id;
        String ean;
        String numero;
        String designation;
        int qt;
        int numligne;
        int livre;


    public Commandes(Integer id, String ean, String numero, Integer qt, Integer livre, String designation, Integer numligne) {
            this.id = id;
            this.ean = ean;
            this.numero = numero;
            this.qt = qt;
            this.livre = livre;
            this.designation = designation;
            this.numligne = numligne;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public int getNumligne() {
            return numligne;
        }

        public void setNumligne(int numligne) {
            this.numligne = numligne;
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

        public int getQt() {
            return qt;
        }

        public void setQt(int qt) {
            this.qt = qt;
        }

        public int getLivre() {
            return livre;
        }

        public void setLivre(int livre) {
            this.livre = livre;
        }

    }

