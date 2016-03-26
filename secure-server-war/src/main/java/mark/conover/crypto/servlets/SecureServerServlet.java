package mark.conover.crypto.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecureServerServlet extends HttpServlet {
	private static final long serialVersionUID = -7682825864889092242L;

	private String message = null;
	
	private static final Logger LOG = LoggerFactory.getLogger(
		SecureServerServlet.class);

	public void init() throws ServletException {
		// Do required initialization
		message = "Hello World";
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Set response content type
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h1>" + message + "</h1>"); 
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		LOG.debug("doPost() method has been called.");
		String publicKey = req.getParameter("publicKey");
		LOG.debug("publicKey is: " + publicKey);	
		
	}

	public void destroy() {
		// do nothing.
	}
}
