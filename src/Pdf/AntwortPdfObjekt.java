/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pdf;

/**
 *
 * @author Marcel
 */
public class AntwortPdfObjekt {

    String vorname, nachname, frage, antwortS, richtigeAntwort, fachKuerzel, fachName, kategorieName, zustLehrerVorname, zustLehrerNachname;

    public AntwortPdfObjekt(String vorname, String nachname, String frage, String antwortS, String richtigeAntwort, String fachKuerzel, String fachName, String kategorieName, String zustLehrerVorname, String zustLehrerNachname) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.frage = frage;
        this.antwortS = antwortS;
        this.richtigeAntwort = richtigeAntwort;
        this.fachKuerzel = fachKuerzel;
        this.fachName = fachName;
        this.kategorieName = kategorieName;
        this.zustLehrerVorname = zustLehrerVorname;
        this.zustLehrerNachname = zustLehrerNachname;
    }

    public String getZustLehrerNachname() {
        return zustLehrerNachname;
    }

    public void setZustLehrerNachname(String zustLehrerNachname) {
        this.zustLehrerNachname = zustLehrerNachname;
    }

    public String getZustLehrerVorname() {
        return zustLehrerVorname;
    }

    public void setZustLehrerVorname(String zustLehrerVorname) {
        this.zustLehrerVorname = zustLehrerVorname;
    }

    public String getFachKuerzel() {
        return fachKuerzel;
    }

    public void setFachKuerzel(String fachKuerzel) {
        this.fachKuerzel = fachKuerzel;
    }

    public String getFachName() {
        return fachName;
    }

    public void setFachName(String fachName) {
        this.fachName = fachName;
    }

    public String getKategorieName() {
        return kategorieName;
    }

    public void setKategorieName(String kategorieName) {
        this.kategorieName = kategorieName;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getFrage() {
        return frage;
    }

    public void setFrage(String frage) {
        this.frage = frage;
    }

    public String getAntwortS() {
        return antwortS;
    }

    public void setAntwortS(String antwortS) {
        this.antwortS = antwortS;
    }

    public String getRichtigeAntwort() {
        return richtigeAntwort;
    }

    public void setRichtigeAntwort(String richtigeAntwort) {
        this.richtigeAntwort = richtigeAntwort;
    }
}
