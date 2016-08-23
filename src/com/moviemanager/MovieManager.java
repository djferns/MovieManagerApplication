package com.moviemanager;

import java.util.List;
import java.util.Scanner;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import com.moviemanager.entity.Movie;

public class MovieManager {

	SessionFactory sessionFactory;
	
	public void setup(){
		Configuration configuration = new Configuration();
		configuration.configure();
		ServiceRegistryBuilder srBuilder = new ServiceRegistryBuilder();
		srBuilder.applySettings(configuration.getProperties());
		ServiceRegistry serviceRegistry = srBuilder.buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}
	
	public void findMovies(Session session, String addedBy){
		Query query = session.createQuery("from Movie where addedBy = :addedUser");
		query.setParameter("addedUser", addedBy);
		
		List<Movie> moviesList = query.list();

		if(moviesList != null && moviesList.size() > 0){
			System.out.println("The movies in your wishlist are as below:");
			for(Movie movie : moviesList){
				System.out.print("\nMovie Name: " + movie.getTitle() + "\n");
				System.out.println("Year: " + movie.getYear());
				System.out.println("Director: " + movie.getDirector());
				System.out.println("Synopsis: " + movie.getSynopsis());
			}
		}else{
			System.out.println("Oops!! There are no movies in your wishlist");
		}
		
	}
	
	public Movie findMovie(Session session, String movieName, int year, String addedBy){
		
		Query query = session.createQuery("from Movie where title = :titleName and year = :movieYear and addedBy = :addedUser");
		query.setParameter("titleName", movieName);
		query.setParameter("movieYear", year);
		query.setParameter("addedUser", addedBy);
		
		Movie movie = (Movie) query.uniqueResult();
		
		return  movie;
	}
	
	public void saveMovieDetails(Session session, String movieName, int year, String directorName, String synopsis, String addedBy){

		Movie movie = findMovie(session, movieName, year,addedBy);

		if(movie == null){
			movie = new Movie();
			movie.setTitle(movieName);
			movie.setYear(year);
			movie.setDirector(directorName);
			movie.setSynopsis(synopsis);
			movie.setAddedBy(addedBy);
			session.save(movie);
			System.out.println("\n" + movie.getTitle() + " was successfully added to your wishlist");
		}else{
			System.out.println("Movie was added earlier");
		}

	}
	
	public void updateMovie(Session session, String movieName, int year, String newSynopsis, String addedBy){
		
		Movie movie = findMovie(session, movieName, year,addedBy);
		
		if(movie == null){
			System.out.println("Movie does not exist");
		}else{
			movie.setSynopsis(newSynopsis);
			System.out.println(movie.getTitle() + " was successfully updated");
		}
		
	}
	
	public void deleteMovie(Session session, String movieName, int year, String addedBy){
		
		Movie movie = findMovie(session, movieName, year,addedBy);
		
		if(movie != null){
			session.delete(movie);
			System.out.println(movie.getTitle() + " was successfully deleted from your wishlist");
		}else{
			System.out.println("Invalid parameters");
		}
		
	}
	
	public static void main(String[] args) {

		MovieManager manager = new MovieManager();
		manager.setup();
		
		Session session = manager.sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		
		int input = 0;
		Scanner reader = new Scanner(System.in);

		System.out.println("1. Add a Movie to your wishlist\n2. Find Movies in your wishlist\n3. Update a Movie in your wishlist\n4. Delete a Movie in your wishlist");
		System.out.println("Please choose the action: ");
		input = reader.nextInt();

		switch (input) {
		case 1 : manager.saveMovieDetails(session, "Top Gun", 2000, "Tony Scott", "When maverick encounters a pair og MiGs..", "Dexter");
				 manager.saveMovieDetails(session, "Jaws", 2002, "Steven Spielberg", "A tale of a white shark!", "Dexter");
				 manager.saveMovieDetails(session, "Conjuring", 2014, "James Wan", "It's a new level to horror", "Dexter");
				 break;

		case 2 : manager.findMovies(session, "Dexter");
				 break;

		case 3 : manager.updateMovie(session, "Top Gun", 2000, "This is the updated synopsis", "Dexter");
				 break;

		case 4 : manager.deleteMovie(session, "Jaws", 2002, "Dexter");
				 break;

		default: System.out.println("Wrong option selected");
				 break;

		}
		
		transaction.commit();
		session.close();
		
	}

}
