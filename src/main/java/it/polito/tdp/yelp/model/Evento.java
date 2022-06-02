package it.polito.tdp.yelp.model;

public class Evento implements Comparable<Evento>
{
	public enum TipoEvento
	{
		DA_INTERVISTARE, // c'è una persona da intervistare (nuova o vecchia)
		FERIE
	}
	
	private TipoEvento tipo;
	private Integer giorno;
	private User intervistato;
	private Giornalista giornalista;
	
	
	public Evento(TipoEvento tipo, Integer giorno, User intervistato, Giornalista giornalista) {
		super();
		this.tipo = tipo;
		this.giorno = giorno;
		this.intervistato = intervistato;
		this.giornalista = giornalista;
	}
	
	public Integer getGiorno() {
		return giorno;
	}
	public User getIntervistato() {
		return intervistato;
	}
	public Giornalista getGiornalista() {
		return giornalista;
	}
	public TipoEvento getTipo() {
		return tipo;
	}

	@Override
	public int compareTo(Evento other) 
	{
		return this.giorno.compareTo(other.giorno);
	}
	
	

}
