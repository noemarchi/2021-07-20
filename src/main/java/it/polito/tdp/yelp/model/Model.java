package it.polito.tdp.yelp.model;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model 
{
	private YelpDao dao;
	private Graph<User, DefaultWeightedEdge> grafo;
	private List<User> vertici;
	private Simulatore sim;
	
	public Model()
	{
		this.dao = new YelpDao();
	}
	
	public String creaGrafo(int minReviews, int anno)
	{
		// creo grafo
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		// aggiungo vertici
		vertici = dao.getVertici(minReviews);
		Graphs.addAllVertices(grafo, vertici);
		
		// aggiungo archi
		for(User u1: vertici)
		{
			for(User u2: vertici)
			{
				if(!u1.equals(u2) && u1.getUserId().compareTo(u2.getUserId()) <0)
				{
					int peso = dao.getPesoArcoByUsers(u1, u2, anno);
					
					if(peso > 0)
					{
						Graphs.addEdge(grafo, u1, u2, peso);
					}
				}
			}
		}
		
		return "Grafo creato con " + grafo.vertexSet().size() + " vertici e " 
				+ grafo.edgeSet().size() + " archi!\n";
	}
	
	
	public List<User> getVertici()
	{
		return this.vertici;
	}
	
	public Integer nVertici()
	{
		if(this.grafo == null)
			return null;
		
		return this.grafo.vertexSet().size();
	}
	
	public List<User> getSimili(User user)
	{
		List<User> simili = new LinkedList<User>();
		
		double max = -1;
		
		for(DefaultWeightedEdge e: grafo.edgesOf(user))
		{
			if(grafo.getEdgeWeight(e) > max)
			{
				max = grafo.getEdgeWeight(e);
			}
		}
		
		for(DefaultWeightedEdge e: grafo.edgesOf(user))
		{
			if(grafo.getEdgeWeight(e) == max)
			{
				User simile = Graphs.getOppositeVertex(grafo, e, user);
				simili.add(simile);
			}
		}
		
		return simili;
	}
	
	public void simula(int x1, int x2)
	{
		sim = new Simulatore(this.grafo);
		sim.init(x1, x2);
		sim.run();
	}
	
	public List<Giornalista> getGiornalisti() 
	{
		return sim.getGiornalisti();
	}
	
	public int getNumeroGiorniSimulazione() 
	{
		return sim.getNumeroGiorni();
	}
}
