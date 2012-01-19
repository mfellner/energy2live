package st.energy2live.controller;

import javax.servlet.http.HttpSession;

import st.energy2live.data.user.User;
import st.energy2live.rdf.DataConnection;
import st.energy2live.rdf.RDFController;

import org.springframework.context.ApplicationContext; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
@RequestMapping("/api")
public class ApiController {
	
	@RequestMapping("/user/{username}")
    public String checkUser(Model model, @PathVariable String username) {
        String message = "<status>available</status>";
		
        DataConnection con;
		try {
			con = new DataConnection();
	        if(con.userExists(username))
	        	message = "<status>notavailable</status>";
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		model.addAttribute("message", message);
        return "response";
	}

	@RequestMapping(value = "/login")
	public String login(Model model, @RequestParam String nickname, @RequestParam String password) {
        String message = "<status>failed</status>";
        
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        
        User user = (User) session.getAttribute("user");
        
        if(user != null)
        {
        	message = "<status>ok</status>";
        	model.addAttribute("message", message);
            return "response";
        }
           
		DataConnection con;
		try {
			con = new DataConnection();
	        if(con.login(nickname, password))
	        {	
	        	message = "<status>ok</status>";
	        	session.setAttribute("user", new User(nickname, password, null, null, null, 0));
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("message", message);	
        return "response";
	}
	
	
	
	@RequestMapping(value = "/tracklog")
	public String createTrackLog(@RequestParam String tracklog, Model model) {
        String message = "<status>failed</status>";
        
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        User user = (User) session.getAttribute("user");
        
        if(user != null)
        {
        	model.addAttribute("message", "<status>ok</status>");
            return "response";
        }
		
		model.addAttribute("message", message);
		return "response";
	}
	
	
	@RequestMapping(value = "/register")
	public String registerUser(@RequestParam String user, Model model) {
        String message = "<status>failed</status>";
        
		DataConnection con;
		try {
			con = new DataConnection();
	        if(con.insertNewUser(new User(user, "123", "Testuser", "test@mail.com", "http://www.user1.com", 0)))
	        	message = "<status>ok</status>";

		} catch (Exception e) {
			e.printStackTrace();
		}
        
		model.addAttribute("message", message);
		return "response";
	}

	
	@RequestMapping(value = "/dataset")
	public String dataset(Model model) throws Exception {
		RDFController.clearAndLoadExamples();
	
		model.addAttribute("message", "Exampledata loaded!");
		return "response";
	}
	
	@RequestMapping(value = "/clear")
	public String clear(Model model) throws Exception {
		RDFController.clear();
	
		model.addAttribute("message", "Data cleared");
		return "response";
	}
	
}
