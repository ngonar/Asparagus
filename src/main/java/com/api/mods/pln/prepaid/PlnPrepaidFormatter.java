/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.mods.pln.prepaid;

import com.util.pojo.DataGagal;
import com.util.pojo.TransactionsResponseGagal;
import com.util.pojo.TrxGagal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class PlnPrepaidFormatter {
    
    private static Logger logger = Logger.getLogger(PlnPrepaidFormatter.class);
    
    public String inquiry(String output, String trx_id, String prod_id, String trx_type, String format){
        String result = "";
        Unmarshaller jaxbUnmarshaller = null;
        Marshaller jaxbMarshaller = null;
        InternalResponsePlnPrepaid trx_respon_internal = null;
        
        //adjust response as in legacy API
        try {
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create();



            JAXBContext jaxbContext = JAXBContext.newInstance(InternalResponsePlnPrepaid.class);
            InputStream stream = new ByteArrayInputStream(output.getBytes());

            jaxbUnmarshaller = jaxbContext.createUnmarshaller(); 
            trx_respon_internal = (InternalResponsePlnPrepaid) jaxbUnmarshaller.unmarshal(stream);

            
            String rc = "";
            if (trx_respon_internal.getRc()==null){
                rc = trx_respon_internal.getCode();
            }
            else {
                rc = trx_respon_internal.getRc();
            }
            logger.info("RC : "+rc);
            //isi data respon
            Object obj = null;
            if (rc.equalsIgnoreCase("0000")) {
                com.util.SwitchPriorityManager.recordOutcome("PLN20", "MAIN_SWITCH", true);
                TransactionsResponsePrepaid trx_respon = new TransactionsResponsePrepaid();
                DataPrepaid dtx = new DataPrepaid();
                TrxPlnPrepaid trxs = new TrxPlnPrepaid();
                trxs.setRc(rc);
                trxs.setTrx_id(trx_id);
                trxs.setStan(trx_respon_internal.getStan());
                trxs.setDatetime(trx_respon_internal.getDatetime());
                trxs.setMerchantCode(trx_respon_internal.getMerchant_code());
                trxs.setBankCode(trx_respon_internal.getBankCode());
                trxs.setTerminalId(trx_respon_internal.getTerminalId());
                trxs.setMaterialNumber(trx_respon_internal.getMaterialNumber());
                trxs.setSubscriberId(trx_respon_internal.getSubscriberId());
                trxs.setPlnRefno(trx_respon_internal.getPlnRefno());
                trxs.setSwitcherRefno(trx_respon_internal.getSwitcherRefno());
                trxs.setSubscriberName(trx_respon_internal.getSubscriberName());
                trxs.setSubscriberSegmentation(trx_respon_internal.getSubscriberSegmentation());
                trxs.setPower(String.valueOf(Integer.parseInt(trx_respon_internal.getPower())));
                trxs.setAdminCharge(String.valueOf(Integer.parseInt(trx_respon_internal.getAdminCharge())));
                trxs.setDistributionCode(trx_respon_internal.getDistributionCode());
                trxs.setServiceUnit(trx_respon_internal.getServiceUnit());
                trxs.setServiceUnitPhone(trx_respon_internal.getServiceUnitPhone());
                trxs.setMaxKwhUnit(trx_respon_internal.getMaxKwhUnit());
                trxs.setTotalRepeat(trx_respon_internal.getTotalRepeat());
                
                if(trx_respon_internal.getPowerPurchaseUnsold() == null) {
                    trxs.setPowerPurchaseUnsold("");
                }
                else {
                    trxs.setPowerPurchaseUnsold(trx_respon_internal.getPowerPurchaseUnsold());
                }
                
                if(trx_respon_internal.getPowerPurchaseUnsold2() == null) {
                    trxs.setPowerPurchaseUnsold2("");
                }
                else {
                    trxs.setPowerPurchaseUnsold2(trx_respon_internal.getPowerPurchaseUnsold2());
                }
                
                trxs.setBit11(trx_respon_internal.getStan());
                trxs.setBit12(trx_respon_internal.getDatetime());
                trxs.setBit48(trx_respon_internal.getBit48());
                trxs.setBit62(trx_respon_internal.getBit62());
                trxs.setSaldo("");
                
                dtx.setTrx(trxs);
                
                trx_respon.setData(dtx);
                
                //jika responnya dalam format json
                if(format.equalsIgnoreCase("json")) {
                    logger.info(gson.toJson(trx_respon));
                    result = gson.toJson(trx_respon);
                }
                else {//jika respon dalam format xml
                    JAXBContext jaxbContext2 = JAXBContext.newInstance(TransactionsResponsePrepaid.class);
                    jaxbMarshaller = jaxbContext2.createMarshaller();
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    StringWriter resultx = new StringWriter();
                    jaxbMarshaller.marshal(trx_respon, resultx);
                    result = resultx.toString();
                    logger.info(result);
                }
            }
            else {
                com.util.SwitchPriorityManager.recordOutcome("PLN20", "MAIN_SWITCH", false);
                TransactionsResponseGagal trx_respon = new TransactionsResponseGagal();
                DataGagal dtx = new DataGagal();
                TrxGagal trxs = new TrxGagal();
                trxs.setRc(rc);
                trxs.setTrx_id(trx_id);
                trxs.setStatus("GAGAL");
                trxs.setDesc(trx_respon_internal.getDesc());
                dtx.setTrx(trxs);
                trx_respon.setData(dtx);
                
                //jika responnya dalam format json
                if(format.equalsIgnoreCase("json")) {
                    logger.info(gson.toJson(trx_respon));
                    result = gson.toJson(trx_respon);
                }
                else {//jika respon dalam format xml
                    JAXBContext jaxbContext2 = JAXBContext.newInstance(TransactionsResponseGagal.class);
                    jaxbMarshaller = jaxbContext2.createMarshaller();
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    StringWriter resultx = new StringWriter();
                    jaxbMarshaller.marshal(trx_respon, resultx);
                    result = resultx.toString();
                    logger.info(result);
                }
            }
            
            

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public String payment(String output, String trx_id, String prod_id, String trx_type, String format){
        String result = "";
        Unmarshaller jaxbUnmarshaller = null;
        Marshaller jaxbMarshaller = null;
        InternalResponsePlnPrepaid trx_respon_internal = null;
        
        //adjust response as in legacy API
        try {
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create();



            JAXBContext jaxbContext = JAXBContext.newInstance(InternalResponsePlnPrepaid.class);
            InputStream stream = new ByteArrayInputStream(output.getBytes());

            jaxbUnmarshaller = jaxbContext.createUnmarshaller(); 
            trx_respon_internal = (InternalResponsePlnPrepaid) jaxbUnmarshaller.unmarshal(stream);

            
            String rc = "";
            if (trx_respon_internal.getRc()==null){
                rc = trx_respon_internal.getCode();
            }
            else {
                rc = trx_respon_internal.getRc();
            }
            logger.info("RC : "+rc);
            //isi data respon
            Object obj = null;
            if (rc.equalsIgnoreCase("0000")) {
                com.util.SwitchPriorityManager.recordOutcome("PLN20", "MAIN_SWITCH", true);
                TransactionsResponsePrepaidPurchase trx_respon = new TransactionsResponsePrepaidPurchase();
                DataPrepaidPurchase dtx = new DataPrepaidPurchase();
                TrxPlnPrepaidPurchase trxs = new TrxPlnPrepaidPurchase();
                trxs.setRc(rc);
                trxs.setTrx_id(trx_id);
                trxs.setStan(trx_respon_internal.getStan());
                trxs.setDatetime(trx_respon_internal.getDatetime());
                trxs.setMerchantCode(trx_respon_internal.getMerchant_code());
                trxs.setBankCode(trx_respon_internal.getBankCode());
                trxs.setTerminalId(trx_respon_internal.getTerminalId());
                trxs.setMaterialNumber(trx_respon_internal.getMaterialNumber());
                trxs.setSubscriberId(trx_respon_internal.getSubscriberId());
                trxs.setPlnRefno(trx_respon_internal.getPlnRefno());
                trxs.setSwitcherRefno(trx_respon_internal.getSwitcherRefno());
                trxs.setSubscriberName(trx_respon_internal.getSubscriberName());
                trxs.setSubscriberSegmentation(trx_respon_internal.getSubscriberSegmentation());
                trxs.setPower(String.valueOf(Integer.parseInt(trx_respon_internal.getPower())));
                trxs.setAdminCharge(trx_respon_internal.getAdminCharge());
                trxs.setDistributionCode(trx_respon_internal.getDistributionCode());
                trxs.setServiceUnit(trx_respon_internal.getServiceUnit());
                trxs.setServiceUnitPhone(trx_respon_internal.getServiceUnitPhone());
                
                
                if (trx_respon_internal.getMaxKwhUnit()==null) {
                    trxs.setMaxKwhUnit("");
                }
                else {
                    trxs.setMaxKwhUnit(trx_respon_internal.getMaxKwhUnit());
                }
                
                trxs.setToken(trx_respon_internal.getToken());
                trxs.setAmount(trx_respon_internal.getAmount());
                
                Double angsuran_formatted = Double.parseDouble(trx_respon_internal.getAngsuran().substring(0, (trx_respon_internal.getAngsuran()).length()-Integer.parseInt(trx_respon_internal.getMinorAngsuran())));
                trxs.setAngsuran(String.valueOf(angsuran_formatted));
                
                Double power_purchase_formatted = Double.parseDouble(trx_respon_internal.getPowerPurchase().substring(0, (trx_respon_internal.getPowerPurchase()).length()-Integer.parseInt(trx_respon_internal.getMinorPowerPurchase())));
                trxs.setPowerPurchase(String.valueOf(power_purchase_formatted));
                
                Double jml_kwh_formatted = Double.parseDouble(trx_respon_internal.getJmlKwh().substring(0, (trx_respon_internal.getJmlKwh()).length()-Integer.parseInt(trx_respon_internal.getMinorPurchaseKwh())));
                trxs.setJmlKwh(String.valueOf(jml_kwh_formatted));
                
                Double ppn_formatted = Double.parseDouble(trx_respon_internal.getPpn().substring(0, (trx_respon_internal.getPpn()).length()-Integer.parseInt(trx_respon_internal.getMinorPpn())));
                trxs.setPpn(String.valueOf(ppn_formatted));
                
                Double ppj_formatted = Double.parseDouble(trx_respon_internal.getPpj().substring(0, (trx_respon_internal.getPpj()).length()-Integer.parseInt(trx_respon_internal.getMinorPpj())));
                trxs.setPpj(String.valueOf(ppj_formatted));
                
                Double meterai_formatted = Double.parseDouble(trx_respon_internal.getMeterai().substring(0, (trx_respon_internal.getMeterai()).length()-Integer.parseInt(trx_respon_internal.getMinorMeterai())));
                trxs.setMeterai(String.valueOf(meterai_formatted));
                
                trxs.setTotalRepeat(trx_respon_internal.getTotalRepeat());
                
                if(trx_respon_internal.getPowerPurchaseUnsold() == null) {
                    trxs.setPowerPurchaseUnsold("");
                }
                else {
                    trxs.setPowerPurchaseUnsold(trx_respon_internal.getPowerPurchaseUnsold());
                }
                
                if(trx_respon_internal.getPowerPurchaseUnsold2() == null) {
                    trxs.setPowerPurchaseUnsold2("");
                }
                else {
                    trxs.setPowerPurchaseUnsold2(trx_respon_internal.getPowerPurchaseUnsold2());
                }
                
                trxs.setBit11(trx_respon_internal.getStan());
                trxs.setBit12(trx_respon_internal.getDatetime());
                trxs.setBit48(trx_respon_internal.getBit48());
                trxs.setBit62(trx_respon_internal.getBit62());
                trxs.setInfoText(trx_respon_internal.getInfoText());
                trxs.setHarga(trx_respon_internal.getHarga());
                trxs.setSaldo(trx_respon_internal.getSaldo());
                
                dtx.setTrx(trxs);
                
                trx_respon.setData(dtx);
                
                //jika responnya dalam format json
                if(format.equalsIgnoreCase("json")) {
                    logger.info(gson.toJson(trx_respon));
                    result = gson.toJson(trx_respon);
                }
                else {//jika respon dalam format xml
                    JAXBContext jaxbContext2 = JAXBContext.newInstance(TransactionsResponsePrepaidPurchase.class);
                    jaxbMarshaller = jaxbContext2.createMarshaller();
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    StringWriter resultx = new StringWriter();
                    jaxbMarshaller.marshal(trx_respon, resultx);
                    result = resultx.toString();
                    logger.info(result);
                }
            }
            else {
                com.util.SwitchPriorityManager.recordOutcome("PLN20", "MAIN_SWITCH", false);
                TransactionsResponseGagal trx_respon = new TransactionsResponseGagal();
                DataGagal dtx = new DataGagal();
                TrxGagal trxs = new TrxGagal();
                trxs.setRc(rc);
                trxs.setTrx_id(trx_id);
                trxs.setStatus("GAGAL");
                trxs.setDesc(trx_respon_internal.getDesc());
                dtx.setTrx(trxs);
                trx_respon.setData(dtx);
                
                //jika responnya dalam format json
                if(format.equalsIgnoreCase("json")) {
                    logger.info(gson.toJson(trx_respon));
                    result = gson.toJson(trx_respon);
                }
                else {//jika respon dalam format xml
                    JAXBContext jaxbContext2 = JAXBContext.newInstance(TransactionsResponseGagal.class);
                    jaxbMarshaller = jaxbContext2.createMarshaller();
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    StringWriter resultx = new StringWriter();
                    jaxbMarshaller.marshal(trx_respon, resultx);
                    result = resultx.toString();
                    logger.info(result);
                }
            }
            
            

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
}
