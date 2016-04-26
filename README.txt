WARNING:  This java code is not the cleanest/most organized code because it was
          developed quickly for a graduate school final project.  However, the
          functionality still works.

Note: "/etc/opt/secure-client" (Unix path) is equivalent to "C:\etc\opt\secure-client" (Windows path)

Setup Instructions for Microsoft Windows Operating System Environment:
1. Create folder "/etc/opt/secure-client".
2. Create folder "/etc/opt/secure-server".
3. Copy "rsa-keys" folder ("secure-client-and-server/secure-server-war/src/main/resources/rsa-keys")
   to "/etc/opt/secure-server/rsa-keys".
4. Install jdk7/jre7 (other jre's may work too).
5. Copy the files inside the folder
   "secure-client-and-server/miscellaneous-resources/jre7_java-cryptography-extension-jce-unlimited-strength-jurisdiction-policy-files/UnlimitedJCEPolicyJDK7/UnlimitedJCEPolicy" 
   to your jdk7's/jre7's "security" folder (e.g. "C:\Program Files\Java\jdk1.7.0_79\jre\lib\security").
   These files may not be needed but to be safe perform this step.
6. Copy the server "config.properties" file ("secure-client-and-server/secure-server-war/src/main/resources/config.properties")
   to "/etc/opt/secure-server/config.properties".
   
Compile/Run Instructions:
1. Compile the project using Apache Maven version 3.3.3 i.e. "mvn clean install".
2. Deploy "secure-server-0.0.1-SNAPSHOT.war" to Apache Tomcat 7 by executing the
   following Apache Maven command within the "secure-server-war" folder:
   mvn clean install tomcat7:run
3. "secure-server-0.0.1-SNAPSHOT.war" will now be deployed locally at 
   "http://localhost:8080/secure-server"
4. Run SecureClient.java in your IDE (e.g. Eclipse IDE) with your jre7.
5. The client and server will perform the interaction mentioned below.

The client/server encrypt messages using a shared AES symmetric key via the 
following process:
1. Client makes a HTTP Post request to the server.   
2. Server sends the server's public key to the client.
3. Client encrypts the 128-bit AES symmetric key using the server's public key.
4. Client sends the RSA encrypted 128-bit AES key to the server.
5. Server decrypts the encrypted 128-bit AES key using the server's private key.
6. Server tells the client it received the shared 128-bit AES symmetric key.
7. Client sends AES encrypted message "What up?" to the server.
8. Server decrypts the encrypted message.
9. Server responds back to client by encrypting the message 
   "Nothing.  Just Chillin.  Hbu?" using AES encryption.
10. Client decrypts the AES encrypted message and sees that the server replied 
    back "Nothing.  Just Chillin.  Hbu?".
   
The client public/private keys and the server public/private keys were 
generated via the following openssl commands:
1. Server generates public key and a private key using openssl
    * Cygwin OpenSSL version 1.0.2c 12 Jun 2015 (Mark-Desktop = server)
        ** Generate public key file and private key file via the following openssl commands:
            *** $ openssl genrsa -out server-keypair_2048.pem 2048
            *** $ openssl rsa -in server-keypair_2048.pem -outform DER -pubout -out server-public-key_2048.der
            *** $ openssl pkcs8 -topk8 -nocrypt -in server-keypair_2048.pem -outform DER -out server-private-key_2048.der
        ** Backed up keys in /secure-server-war/src/main/resources/rsa-keys
           
2. Client generates public key and a private key using openssl.
    * Cygwin OpenSSL version 1.0.2c 12 Jun 2015 (Mark-Desktop = client)
        ** Generate public key file and private key file via the following openssl commands:
            *** $ openssl genrsa -out client-keypair_2048.pem 2048
            *** $ openssl rsa -in client-keypair_2048.pem -outform DER -pubout -out client-public-key_2048.der
            *** $ openssl pkcs8 -topk8 -nocrypt -in client-keypair_2048.pem -outform DER -out client-private-key_2048.der
        ** Backed up keys in /secure-client-jar/src/main/resources/rsa-keys