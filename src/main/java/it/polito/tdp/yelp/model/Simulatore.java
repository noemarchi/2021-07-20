package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.yelp.model.Evento.TipoEvento;

public class Simulatore 
{
	// INPUT
	private int x1;
	private int x2;
	
	// OUTPUT
	private List<Giornalista> giornalisti;
	// giornalisti rappresentati da un numero compreso tra 0 e X1-1
	private int numeroGiorni;
	
	// STATO DEL MONDO
	private Set<User> intervistati;
	private Graph<User, DefaultWeightedEdge> grafo;
	
	// CODA
	private PriorityQueue<Evento> coda;
	
	// -------------------------------------------
	
	// COSTRUTTORE
	public Simulatore(Graph<User, DefaultWeightedEdge> grafo)
	{
		this.grafo = grafo;
	}
	
	// INIZIALIZZAZIONE
	public void init(int x1, int x2)
	{
		// input
		this.x1 = x1;
		this.x2 = x2;
		
		// stato mondo
		this.intervistati = new HashSet<User>();
		
		// output
		this.numeroGiorni = 0;
		
		this.giornalisti = new ArrayList<Giornalista>();
		for(int id=0; id<this.x1; id++)
		{
			this.giornalisti.add(new Giornalista(id));
		}
		
		// coda
		this.coda = new PriorityQueue<Evento>();
		
		// creo gli eventi del primo giorno e li aggiungo alla coda
		for(Giornalista giornalista: this.giornalisti)
		{
			User intervistato = this.selezionaIntervistato(grafo.vertexSet());
			
			// l'intervistato va aggiunto
			this.intervistati.add(intervistato);
			
			// aggiorno gli intervistati del giornalista
			giornalista.plusNumIntervistati();
			
			// aggiungo il nuovo evento alla coda
			coda.add(new Evento(
					TipoEvento.DA_INTERVISTARE, 
					1, 
					intervistato,
					giornalista));
		}
		
		// lancio la simulazione
	}
	
	// ESECUZIONE
	public void run()
	{
		while(!coda.isEmpty() && this.intervistati.size() < this.x2)
		{
			// estraggo evento
			Evento evento = coda.poll();
			
			// aggiorno numero giorni
			this.numeroGiorni = evento.getGiorno();
			
			// elaboro l'evento
			processEvent(evento);
			
		}
	}
	
	// METODI SUPPORTO
	
	/**
	 * Seleziona un intervistato dalla lista specificata,
	 * evitando di selezionare quelli che sono già in this.intervistati
	 * @param lista da cui scegliere
	 * @return User da intervistare
	 */
	private User selezionaIntervistato(Collection<User> lista)
	{
		// insieme dei potenziali candidati da intervistare
		Set<User> candidati = new HashSet<User>(lista);
		candidati.removeAll(this.intervistati);
		
		// devo sceglierne uno a caso
		Random rand = new Random();
		int scelto = rand.nextInt(candidati.size());
		
		return (new ArrayList<User>(candidati)).get(scelto);
	}
	
	/**
	 * dato un utente, cerca l'utente vicino con cui c'è affinità maggiore
	 * @param utente
	 * @return null se non ci sono vicini da intervistare,
	 * altrimenti utente da intervistare
	 */
	private User selezionaAdiacente(User utente)
	{
		List<User> vicini = Graphs.neighborListOf(this.grafo, utente); 
		
		// dai vicini, tolgo quelli gia intervistati
		vicini.removeAll(this.intervistati);
		
		if(vicini.size() == 0)
		{
			// se utente era isolato
			// o se tutti adiacenti già intervistati
			return null;
		}
		
		// cerco quello/i con affinità maggiore
		double max = 0;
		for(User vicino: vicini)
		{
			double peso = this.grafo.getEdgeWeight(this.grafo.getEdge(utente, vicino));
			
			if(peso > max)
			{
				max = peso;
			}
		}
		
		List<User> migliori = new ArrayList<User>();
		for(User vicino: vicini)
		{
			double peso = this.grafo.getEdgeWeight(this.grafo.getEdge(utente, vicino));
			
			if(peso == max)
			{
				migliori.add(vicino);
			}
		}
		
		// scelgo uno a caso tra i migliori
		Random rand = new Random();
		int scelto = rand.nextInt(migliori.size());
		
		return migliori.get(scelto);
	}
	
	private void processEvent(Evento evento) 
	{
		switch(evento.getTipo())
		{
		case DA_INTERVISTARE:
			
			double caso = Math.random();
			
			if(caso < 0.6)
			{
				// cerco tra gli adiacenti
				User vicino = this.selezionaAdiacente(evento.getIntervistato());
				
				// se non lo trovo tra gli adiacenti,
				// lo cerco tra tutti i vertici
				if(vicino == null)
				{
					vicino = this.selezionaIntervistato(grafo.vertexSet());
				}
				
				coda.add(new Evento(
						TipoEvento.DA_INTERVISTARE,
						evento.getGiorno()+1,
						vicino,
						evento.getGiornalista()));
				
				// aggiorno gli intervistati
				this.intervistati.add(vicino);
				
				evento.getGiornalista().plusNumIntervistati();
				
				System.out.println(String.format("INTERVISTA | %s | %s | %s", evento.getGiorno(),vicino,evento.getGiornalista()));
				
			}
			else if(caso < 0.8)
			{
				// ferie il giorno dopo
				coda.add(new Evento(
						TipoEvento.FERIE,
						evento.getGiorno()+1,
						evento.getIntervistato(),
						evento.getGiornalista()));
				
				// non scelgo ora chi devo intervistare dopodomani
				
				System.out.println(String.format("FERIE | %s | %s | %s", evento.getGiorno(),evento.getIntervistato(),evento.getGiornalista()));
			}
			else
			{
				// domani intervisto lo stesso utente
				coda.add(new Evento(
						TipoEvento.DA_INTERVISTARE,
						evento.getGiorno()+1,
						evento.getIntervistato(),
						evento.getGiornalista()));
				
				// non devo aggiornare gli intervistati
				// neanche quelli del giornalista
				
				System.out.println(String.format("STESSO USER | %s | %s | %s", evento.getGiorno(),evento.getIntervistato(),evento.getGiornalista()));
			}
			
			break;
			
		case FERIE:
			
			// il giorno successivo intervisto sicuramente qualcuno
			// come caso 1 60%
			
			// cerco tra gli adiacenti
			User vicino = this.selezionaAdiacente(evento.getIntervistato());
			
			// se non lo trovo tra gli adiacenti,
			// lo cerco tra tutti i vertici
			if(vicino == null)
			{
				vicino = this.selezionaIntervistato(grafo.vertexSet());
			}
			
			coda.add(new Evento(
					TipoEvento.DA_INTERVISTARE,
					evento.getGiorno()+1,
					vicino,
					evento.getGiornalista()));
			
			// aggiorno gli intervistati
			this.intervistati.add(vicino);
			
			evento.getGiornalista().plusNumIntervistati();
			
			System.out.println(String.format("INTERVISTA | %s | %s | %s", evento.getGiorno(),vicino,evento.getGiornalista()));
		}
		
		
	}

	// GET OUTPUT
	public List<Giornalista> getGiornalisti() 
	{
		return giornalisti;
	}

	public int getNumeroGiorni() 
	{
		return numeroGiorni;
	}
}
