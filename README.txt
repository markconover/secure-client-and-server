The client/server encrypt messages a shared AES symmetric key via the following
process:
1. Server generates public key and a private key using openssl
    * http://gnuwin32.sourceforge.net/packages/openssl.htm
    * Cygwin OpenSSL version 1.0.2c 12 Jun 2015 (Mark-Desktop = server)
        ** Private key
            *** OpenSSL> genrsa -des3 -out server.key 1024
            *** passphrase = "password" (without quotes)
            *** -----BEGIN RSA PRIVATE KEY-----
                Proc-Type: 4,ENCRYPTED
                DEK-Info: DES-EDE3-CBC,D74B899844498677
                
                QhWLYiELF36qeCBctxZhC49NSHS3yUaoNF/ZkmlewQN3u8JBOJgxFQVVaaBbW13B
                /vLdTAdr0YvkPMQ2x2IctVALGwhR8V5vScnT7W79ucWJyPJmBXwF3dFnDmyhW/Kw
                jyDctk8XKZkhXcm3efiDLJIEkxim3A22T3awlziKcsBlvPxUzTt2gWU3kyTSwUOa
                84KvkgPQ67hXKjzXo9XGUq/IHNMeEBBI3HibLzPCSPyPNBqZrP457hjwrF+TyXBI
                iexo4S8MNjvDsJCRzR+71Xbex/8LvtUkwq+tChnAeX03QIHkndIHpS2QvW8Gndew
                9uLhb2J33k6VRzkZPyar4hitKkTJOMAS4OH+hyDdY8U7kkcK7jvdBa+umqZO62tW
                zTfxa0aLhzpoAhPTupqCZWi3BkwW7EzS5XhYfIE4Yrx7nVx8nptCfLFei6U6qhW6
                mryB5r9fYYDOoc+Iya2lQ58A5a/FHIuU0W870x1gkdXTxGowiaqTWqLZ8+2xCKdM
                la8Wpff6VwxWfmeORm1FvuSsWGrI9Vgq6zkrV2V//HjRd/zzogsfkfD6FGa+QgCr
                hJny//6Cl50iGMKeX8BJbOQHo4CLE7iuevJCTK24dt0tq3D5SrUfPrVb9/adIP1m
                MnDFol4WeUUpBs5zjzVZY3HwqFl4lIr8aDL/3V+uLQCnJtYevfEgBYagerxngXbs
                gTE87pIrsClE+R4IpEDJB43vuUiqoZtJ8m3nlcYfxiqyTgM9Gsvc8VnfTZ/a4ktR
                3ETgfa/1nT3msFzo4KRZMmxfMst5vpX2xlvpd7deLwolNKh7pUp/5Q==
                -----END RSA PRIVATE KEY-----
        ** Public key (generate a public key from the private key "server.key")
            *** $ openssl rsa -in server.key -pubout
                Enter pass phrase for server.key:
                writing RSA key
                -----BEGIN PUBLIC KEY-----
                MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDFXhbyBYhSPaf1tOJlGWWcFdrG
                A8C7xY9UmigBQdSoEdSzc+1GgYKxughwVuXEkdWq13lyfwXVxI40xPrZ7lux387t
                7yTr/+mDYQOjdp8qvZ5coUa901iTnVJSFulhK+/0PFnYYAZ1wwgo4bkrStMBtJw1
                7jSpjOT3SDSLgTbOiQIDAQAB
                -----END PUBLIC KEY-----
            
            
2. Client generates public key and a private key using openssl.
    * http://gnuwin32.sourceforge.net/packages/openssl.htm
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