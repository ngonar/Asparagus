package com.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.util.pojo.Settings;
import static spark.Spark.*;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import model.*;
import javax.persistence.*;
import java.util.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.util.McAuth;
import com.util.RPCClient;
import com.util.Switching;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import net.spy.memcached.MemcachedClient;

public class Asparagus {

    private static Logger logger = Logger.getLogger(Asparagus.class);
    private static int uidx = 0;
    private static String userx = "";
    ClassLoader classLoader = getClass().getClassLoader();
    private static MemcachedClient mc = null;
    public static Map mpx = new HashMap();

    private static int respawnSetting = 60;

    public static int getRespawnSetting() {
        return respawnSetting;
    }

    public static void setRespawnSetting(int val) {
        respawnSetting = val;
    }

    public static void loadUserConfig(String host, int port) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("API", System.getProperties());
        EntityManager em = factory.createEntityManager();

        try {
            mc = new MemcachedClient(new InetSocketAddress(host, port));

            List dbUser = em.createNamedQuery("Users.findAll").getResultList();

            //format prefix -> user_id_xxx
            // > id
            // > username
            // > password
            // > key
//            for (Users m : (List<Users>) dbUser) {
//                if (m.getUsername() != null && m.getId() != null && m.getPassword() != null && m.getSecretKey() != null) {
//                    mc.add("user_" + m.getUsername(), 3600 * 24, m.getPassword());
//                    mc.add("user_" + m.getUsername() + "_id", 3600 * 24, m.getId());
//                    mc.add("user_" + m.getId() + "_username", 3600 * 24, m.getUsername());
//                    mc.add("user_" + m.getId() + "_password", 3600 * 24, m.getPassword());
//                    mc.add("user_" + m.getId() + "_key", 3600 * 24, m.getSecretKey());
//                    logger.info("Username : " + m.getUsername() + ", Password : " + m.getPassword() + ", Key : " + m.getSecretKey());
//                }
//            }

            em.close();
            factory.close();

        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
        } finally {

            if (em.isOpen()) {
                em.close();
            }

            if (factory.isOpen()) {
                factory.close();
            }

        }
    }

    public static void main(String[] args) {

        BasicConfigurator.configure();
        com.util.DatabaseInitializer.initializeDatabase();

        Switching sw = new Switching();

        sw.serviceMapping();

        int maxThreads = 100;
        int minThreads = 1;
        int timeOutMillis = 30000;
        int port = 4567;

        String mc_host = "localhost";
        int mc_port = 11211;

        //Read Configuration
        try {

            File file = new File("config/settings.xml");

            JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Settings settingx = (Settings) jaxbUnmarshaller.unmarshal(file);

            minThreads = settingx.getMinthread();
            logger.info("Min thread : " + settingx.getMinthread());
            maxThreads = settingx.getMaxthread();
            logger.info("Max thread : " + settingx.getMaxthread());
            timeOutMillis = settingx.getTimeoutmilis();
            logger.info("Timeout    : " + settingx.getTimeoutmilis());

            port = settingx.getPort();
            logger.info("Port       : " + settingx.getPort());

            mc_host = settingx.getMcHost();
            logger.info("MC Host       : " + settingx.getMcHost());
            mc_port = settingx.getMcPort();
            logger.info("MC Port       : " + settingx.getMcPort());
            respawnSetting = settingx.getRespawnSetting();
            logger.info("Respawn Setting : " + respawnSetting + " seconds");

            port(port);
//            secure(settingx.getKeystore_filepath(), settingx.getKeystore_password(), settingx.getTruststore_filepath(), settingx.getTruststore_password());

        } catch (Exception e) {
            e.printStackTrace();
        }

        threadPool(maxThreads, minThreads, timeOutMillis);

//        loadUserConfig(mc_host, mc_port);
        /*before((request, response) -> {
	    boolean authenticated = false;
            int user_idx = 0;
            String username = "";

            if (request.headers("Authorization") == null) {
                halt(403, "Access Denied");
            }
            
            CakePHP cake = new CakePHP(mc);
            
            user_idx = cake.CakeRESTAuth(request, response);
            if(user_idx>0) {
                authenticated = true;    
                setUid(user_idx);            
            }

            logger.info("Requester info => "+request.ip()+" "+request.host()+" "+request.userAgent());

            if (!authenticated) {
                logger.info("Access Denied");
                halt(403, "Access Denied");
            }
            
                logger.info("Access Granted");
	});*/
        
        get("/", (req, res) -> "Asparagus 1.0");

        get("/version", (req, res) -> "Asparagus 1.0");
        
        post("/transactions/:format", (request, response) -> {
            
            

            String outputRes = "";
            String[] reqParams = new String[8];

            String format = "xml";
            if (request.params(":format").equalsIgnoreCase("trx.json")) {
                format = "json";
            } else if (request.params(":format").equalsIgnoreCase("trx.xml")) {
                format = "xml";
            } else {
                halt(404, "Page Not Found");
            }
            reqParams[0] = format; //document type

            /*handle the parameters sent*/

            //cek trx id if duplicate then load the last response
            long idx = 0;
            //idx = isDuplicateTrx(trx_id,getUid(),Integer.parseInt(product_id));
            logger.info("Duplicate trx => inbox id : "+idx);

            String inbox_message = "";
            inbox_message = sw.getInternalMessageFormat(reqParams, mpx, request, uidx);
            
            logger.info("inbox message : "+inbox_message);
            
            String output = "";
            //if duplicate then just return the last message. no need to insert to db lah
            if (idx>0) {
                output = getMessageOutbox((int)idx);
            }
            else {
                //insert into inbox & queue            
//                idx = insertToInbox(getUserx(),inbox_message,getUid(),trx_id,Integer.parseInt(product_id),
//                    trx_type,cust_account_no, Integer.parseInt(product_nomination), mpx);
                logger.info("Inbox ID : "+idx);
            
                //waiting response from queue                
                RPCClient rpc = new RPCClient();
                output = rpc.call(String.valueOf(idx));
                rpc.close();
            }

            if (format.equalsIgnoreCase("json")) {
                response.type("application/json");
            } else {
                response.type("application/xml");
            }

            response.header("Cache-Control", "no-cache,no-store,max-age=0,must-revalidate");
            response.header("Pragma", "no-cache");
            response.header("Expires", "-1");

            logger.info("output : " + output);

            try {

                URL url = new URL("xxxx//destination url");

                install();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");                
                conn.setReadTimeout(30000);

                String input = "";

                /* json request */
                try {

                   
                    logger.info(input);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info(e.toString());
                }

                /* json request */
                logger.info(input);
                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                logger.info("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    logger.info(output);
                }

                conn.disconnect();

            } catch (MalformedURLException e) {
                logger.info(e.toString());
                e.printStackTrace();

            } catch (IOException e) {
                logger.info(e.toString());
                e.printStackTrace();

            } catch (Exception e) {
                logger.info(e.toString());
                e.printStackTrace();
            }

            return outputRes;

        });

        /*
        after((request, response) -> {
		    response.header("foo", "set by after filter");
		});*/
    }

    public static void setUid(int uid) {
        uidx = uid;
    }

    public static int getUid() {
        return uidx;
    }

    public static void setUserx(String user) {
        userx = user;
    }

    public static String getUserx() {
        return userx;
    }

    private static void install() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // Trust always
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // Trust always
                }
            }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        // Create empty HostnameVerifier
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };

        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    public static String getMessageOutbox(int id) {
        String result = "";
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("API", System.getProperties());
        EntityManager em = factory.createEntityManager();
        try {

            String sql = "SELECT o FROM Outboxes o "
                    + " where o.inboxId =:inbox_id order by o.inboxId asc";
            Query query = em.createQuery(sql);
            query.setParameter("inbox_id", id);
            query.setMaxResults(1);

            for (Outboxes m : (List<Outboxes>) query.getResultList()) {
                logger.info("Msg Outbox id : " + m.getId());

                result = m.getMessage();
                logger.info("Msg Outbox Result : " + result);
            }

            em.close();
            factory.close();
        } catch (Exception e) {
            logger.fatal(e.getMessage());
        } finally {

            if (em.isOpen()) {
                em.close();
            }
            if (factory.isOpen()) {
                factory.close();
            }

        }

        return result;
    }

    protected static long isDuplicateTrx(String trx_id, int user_id, int prod_id) {
        long result = 0;
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("API", System.getProperties());
        EntityManager em = factory.createEntityManager();
        try {
            logger.info("Trx id : " + trx_id + " User id : " + user_id);
            String sql = "SELECT o FROM Inboxes o where o.trxId =:trx_id "
                    + "and o.userId=:uid and o.prodId=:prod_id and o.trxType='2200' ";
            Query query = em.createQuery(sql);
            query.setParameter("uid", user_id);
            query.setParameter("trx_id", trx_id);
            query.setParameter("prod_id", prod_id);

            query.setMaxResults(1);

            for (Inboxes m : (List<Inboxes>) query.getResultList()) {
                result = m.getId();
            }

            em.close();
            factory.close();
        } catch (Exception e) {
            logger.fatal(e.toString());
        } finally {

            if (em.isOpen()) {
                em.close();
            }

            if (factory.isOpen()) {
                factory.close();
            }

        }

        return result;
    }

    public static void publishQ(String msg, String product_id, Map mrx) {

        String TASK_QUEUE_NAME = mrx.get(product_id + "_queue").toString();
        logger.info("task queue : " + TASK_QUEUE_NAME);
        String EXCHANGE_NAME = mrx.get(product_id + "_exchange").toString();
        logger.info("exchange : " + EXCHANGE_NAME);
        String ROUTING_KEY = mrx.get(product_id + "_route").toString();
        logger.info("routing : " + ROUTING_KEY);

        try {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            com.rabbitmq.client.Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

            String message = msg;

            if (!message.equalsIgnoreCase("0")) {

                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY,
                        MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());

                logger.info(" [x] Sent '" + message + "'");
            }

            channel.close();
            connection.close();

        } catch (Exception e) {
            logger.fatal(e.toString());
        }
    }

    protected static long insertToInbox(String sender, String msg, int user_id, String trx_id, int product_id,
            String trx_type, String idpel, String msisdn, int nominal, Map mrx) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("API", System.getProperties());
        EntityManager em = factory.createEntityManager();
        long idx = 0;
        try {

            logger.info("Saving to inbox");

            em.getTransaction().begin();

            Inboxes inbox = new Inboxes();

            Calendar currentDate = Calendar.getInstance();
            inbox.setCreateDate(currentDate.getTime());
            inbox.setSender(sender);
            inbox.setStatus(902);
            inbox.setMessage(msg);
            inbox.setUserId(user_id);
            inbox.setSenderType("API");
            inbox.setTrxId(trx_id);
            inbox.setProdId(product_id);
            inbox.setTrxType(trx_type);
            inbox.setMediaTypeId(5);
            inbox.setReceiver("API");
            inbox.setIdpel(idpel);
            inbox.setMsisdn(msisdn);
            inbox.setNominal(nominal);

            em.persist(inbox);
            em.flush();
            idx = inbox.getId();

            em.getTransaction().commit();

            //id;msg;sender;media_type_id;receiver;user_id;sender_type
            String kiriman = idx + ";" + msg + ";" + sender + "/XML" + ";" + "5" + ";" + "API" + ";" + user_id + ";" + "API;" + product_id + ";" + idx;

            publishQ(kiriman, String.valueOf(product_id), mrx);

            em.close();
            factory.close();

        } catch (Exception er) {
            logger.fatal(er.toString());
            er.printStackTrace();

        } finally {

            if (em.isOpen()) {
                em.close();
            }

            if (factory.isOpen()) {
                factory.close();
            }

        }

        return idx;

    }

}
