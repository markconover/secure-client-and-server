The client/server encrypt messages a shared AES symmetric key via the following
process:
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
    
3. Server sends the server's public key to the client.
4. Client encrypts the client's public key with the server's public key and
   sends it to the server.
5. Server decrypts the client's message using the server's private key to obtain
   the client's public key.
6. Server generates a 128-bit AES key and encrypts the key using the client's
   public key.
7. Server sends the encrypted 128-bit AES key to the client.
8. Client decrypts the encrypted 128-bit AES key using the client's private key.
9. Client and Server encrypt/decrypt all future messages using the 
   shared 128-bit AES key.