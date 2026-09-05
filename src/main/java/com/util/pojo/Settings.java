
package com.util.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="application")
public class Settings {
    
    int minthread;
    int maxthread;
    int timeoutmilis;
    String salt;
    int port;
    String mc_host;
    int mc_port;
    
    String keystore_filepath;
    String keystore_password;
    String truststore_filepath;
    String truststore_password;
            
    public int getMinthread() {
            return minthread;
    }

    @XmlElement(name="minthread")
    public void setMinthread(int minthread) {
            this.minthread = minthread;
    }
    
    public int getMaxthread() {
            return maxthread;
    }

    @XmlElement(name="maxthread")
    public void setMaxthread(int maxthread) {
            this.maxthread = maxthread;
    }
    
    public int getTimeoutmilis() {
            return timeoutmilis;
    }

    @XmlElement(name="timeoutmilis")
    public void setTimeoutmilis(int timeoutmilis) {
            this.timeoutmilis = timeoutmilis;
    }
    
    public String getSalt() {
            return salt;
    }

    @XmlElement(name="salt")
    public void setSalt(String salt) {
            this.salt = salt;
    }
    
    public int getPort() {
            return port;
    }

    @XmlElement(name="port")
    public void setPort(int port) {
            this.port = port;
    }
    

    public String getKeystore_filepath() {
        return keystore_filepath;
    }

    @XmlElement(name="keystore_filepath")
    public void setKeystore_filepath(String keystore_filepath) {
        this.keystore_filepath = keystore_filepath;
    }

    public String getKeystore_password() {
        return keystore_password;
    }

    @XmlElement(name="keystore_password")
    public void setKeystore_password(String keystore_password) {
        this.keystore_password = keystore_password;
    }

    public String getTruststore_filepath() {
        return truststore_filepath;
    }

    @XmlElement(name="truststore_filepath")
    public void setTruststore_filepath(String truststore_filepath) {
        this.truststore_filepath = truststore_filepath;
    }

    public String getTruststore_password() {
        return truststore_password;
    }

    @XmlElement(name="truststore_password")
    public void setTruststore_password(String truststore_password) {
        this.truststore_password = truststore_password;
    }

    
    public String getMcHost() {
            return mc_host;
    }
    
    @XmlElement(name="mc_host")
    public void setMcHost(String mc_host) {
            this.mc_host = mc_host;
    }
    
    public int getMcPort() {
            return mc_port;
    }

    int respawn_setting = 60;

    @XmlElement(name="mc_port")
    public void setMcPort(int mc_port) {
            this.mc_port = mc_port;
    }

    public int getRespawnSetting() {
            return respawn_setting;
    }

    @XmlElement(name="respawn_setting")
    public void setRespawnSetting(int respawn_setting) {
            this.respawn_setting = respawn_setting;
    }
    
}
