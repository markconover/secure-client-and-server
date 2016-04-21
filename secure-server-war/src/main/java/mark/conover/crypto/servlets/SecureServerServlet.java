package mark.conover.crypto.servlets;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mark.conover.crypto.config.SecureServerConfig;

import org.apache.commons.io.IOUtils;
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
		String clientPublicKey = req.getParameter("publicKey");		
		
		LOG.debug("Client's publicKey is: " + clientPublicKey);			
		
		// Send the server's public key back
        BufferedOutputStream bos = new BufferedOutputStream(
                resp.getOutputStream());
        IOUtils.write("server public key: " + 
            SecureServerConfig.SECURE_SERVER_PUBLIC_KEY, bos, "UTF-8");
        bos.flush();
        bos.close();		
	}

	public void destroy() {
		// do nothing.
	}
}
