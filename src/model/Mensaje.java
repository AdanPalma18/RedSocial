package model;

public class Mensaje {
    private String remitente;
    private String destinatario;
    private String contenido;
    private String fecha;
    private boolean esSticker;
    private boolean leido;

    public Mensaje(String remitente, String destinatario, String contenido, String fecha) {
        this(remitente, destinatario, contenido, fecha, false, false);
    }

    public Mensaje(String remitente, String destinatario, String contenido, String fecha, boolean esSticker) {
        this(remitente, destinatario, contenido, fecha, esSticker, false);
    }

    public Mensaje(String remitente, String destinatario, String contenido, String fecha, boolean esSticker, boolean leido) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.contenido = contenido;
        this.fecha = fecha;
        this.esSticker = esSticker;
        this.leido = leido;
    }

    public String getRemitente() {
        return remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getContenido() {
        return contenido;
    }

    public String getFecha() {
        return fecha;
    }

    public boolean esSticker() {
        return esSticker;
    }

    public boolean isLeido() {
        return leido;
    }

    public void marcarComoLeido() {
        this.leido = true;
    }
}
