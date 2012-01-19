package st.energy2live.rdf;

import java.io.File;
import java.io.IOException;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.http.HTTPRepository;

public class RDFController {
	
	public static void clearAndLoadIDs() throws Exception {
		
        String sesameServer = "http://localhost:8080/openrdf-sesame";
        String repositoryID = "1";
        
        Repository myRepository = new HTTPRepository(sesameServer, repositoryID);
        myRepository.initialize();

        RepositoryConnection con = myRepository.getConnection();
        con.clear();
        
        File rdf = new File("../RDFStructure/example_data/data_ids.xml");
        con.add(rdf, null, RDFFormat.RDFXML);
        
        System.out.println("ID Loading done ...");
	}
	
	
    public static void clearAndLoadExamples() throws Exception {
    	
        String sesameServer = "http://localhost:8080/openrdf-sesame";
        String repositoryID = "1";
        
        Repository myRepository = new HTTPRepository(sesameServer, repositoryID);
        myRepository.initialize();

        RepositoryConnection con = myRepository.getConnection();
        con.clear();
        
        File rdf = new File("../RDFStructure/example_data/data_ids.xml");
        con.add(rdf, null, RDFFormat.RDFXML);
        
        rdf = new File("../RDFStructure/example_data/data_tracks.xml");
        con.add(rdf, null, RDFFormat.RDFXML);

        rdf = new File("../RDFStructure/example_data/data_users.xml");
        con.add(rdf, null, RDFFormat.RDFXML);  
        
        rdf = new File("../RDFStructure/example_data/data_relations.xml");
        con.add(rdf, null, RDFFormat.RDFXML);  
        
        rdf = new File("../RDFStructure/example_data/data_vehicles.xml");
        con.add(rdf, null, RDFFormat.RDFXML);  
        
        System.out.println("Example Loading done ...");
    }
    
    public static void clear() throws Exception {
        String sesameServer = "http://localhost:8080/openrdf-sesame";
        String repositoryID = "1";
        
        Repository myRepository = new HTTPRepository(sesameServer, repositoryID);
        myRepository.initialize();

        RepositoryConnection con = myRepository.getConnection();
        con.clear();
        
        System.out.println("Loading done ...");
    }
    
}
