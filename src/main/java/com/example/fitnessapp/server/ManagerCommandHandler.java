package com.example.fitnessapp.server;

import com.example.fitnessapp.dao.*;
import com.example.fitnessapp.model.*;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ManagerCommandHandler {

    private final PromoCampaignDAO promoCampaignDao;
    private final PaymentDAO paymentDao;
    private final GymUserDAO userDao;
    private final DiscountDAO discountDao;
    private final CampaignPassDAO campaignPassDao;
    private final PassTypeDAO passTypeDao;

    public ManagerCommandHandler(PromoCampaignDAO promoCampaignDao, PaymentDAO paymentDao, GymUserDAO userDao,
                                 DiscountDAO discountDao, CampaignPassDAO campaignPassDao, PassTypeDAO passTypeDao) {
        this.promoCampaignDao = promoCampaignDao;
        this.paymentDao = paymentDao;
        this.userDao = userDao;
        this.discountDao = discountDao;
        this.campaignPassDao = campaignPassDao;
        this.passTypeDao = passTypeDao;
    }

    public boolean handle(String command, String[] tokens, PrintWriter out) {
        switch (command) {
            case "GET_CAMPAIGNS": return handleGetCampaigns(out);
            case "ADD_CAMPAIGN": return handleAddCampaign(tokens, out);
            case "DELETE_CAMPAIGN": return handleDeleteCampaign(tokens, out);
            case "GET_MGR_STATS": return handleGetMgrStats(out);
            case "ADD_DISCOUNT": return handleAddDiscount(tokens, out);
            case "GET_DISCOUNTS": return handleGetDiscounts(out);
            case "DELETE_DISCOUNT": return handleDeleteDiscount(tokens, out);
            case "ADD_CAMPAIGN_PASS": return handleAddCampaignPass(tokens, out);
            case "GET_CAMPAIGN_PASSES": return handleGetCampaignPasses(out);
            case "DELETE_CAMPAIGN_PASS": return handleDeleteCampaignPass(tokens, out);
            default: return false;
        }
    }

    private boolean handleGetCampaigns(PrintWriter out) {
        try {
            List<PromoCampaign> list = promoCampaignDao.findAll();
            StringBuilder sb = new StringBuilder("CAMPAIGNS_OK");
            for (PromoCampaign c : list) {
                sb.append(";").append(c.getId()).append("|").append(c.getName()).append("|")
                        .append(c.getTargetGroup() != null ? c.getTargetGroup() : "Brak").append("|")
                        .append(c.getBudget() != null ? c.getBudget().toString() : "0").append("|")
                        .append(c.getStartDate().toString()).append("|").append(c.getEndDate().toString());
            }
            out.println(sb.toString());
            return true;
        } catch (Exception e) { out.println("CAMPAIGNS_ERROR;Blad: " + e.getMessage()); return true; }
    }

    private boolean handleAddCampaign(String[] tokens, PrintWriter out) {
        try {
            PromoCampaign campaign = new PromoCampaign(0, tokens[1], tokens[2], new BigDecimal(tokens[3]), 
                                                       LocalDate.parse(tokens[4]), LocalDate.parse(tokens[5]));
            promoCampaignDao.save(campaign);
            out.println("ADD_CAMPAIGN_OK;Kampania została pomyślnie dodana!");
            return true;
        } catch (Exception e) { out.println("ADD_CAMPAIGN_ERROR;Blad zapisu: " + e.getMessage()); return true; }
    }

    private boolean handleDeleteCampaign(String[] tokens, PrintWriter out) {
        try {
            if(tokens.length < 2 || tokens[1].isEmpty()) {
                out.println("DELETE_CAMPAIGN_ERROR;Podaj poprawne ID kampanii!");
                return true;
            }
            int id = Integer.parseInt(tokens[1]);
            promoCampaignDao.delete(id);
            out.println("DELETE_CAMPAIGN_OK;Kampania o ID " + id + " została usunięta.");
            return true;
        } catch (NumberFormatException ne) {
            out.println("DELETE_CAMPAIGN_ERROR;ID musi byc liczba!");
            return true;
        } catch (Exception e) { 
            out.println("DELETE_CAMPAIGN_ERROR;Nie mozna usunac! Sprawdz czy do kampanii nie sa przypisane znizki/karnety."); 
            return true; 
        }
    }

    private boolean handleGetMgrStats(PrintWriter out) {
        try {
            List<Payment> payments = paymentDao.findAll();
            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (Payment p : payments) {
                if (p.getAmount() != null) totalRevenue = totalRevenue.add(p.getAmount());
            }
            int totalUsers = userDao.findAll().size();
            out.println(String.format("MGR_STATS_OK;%s;%d;%d", totalRevenue.toString(), totalUsers, payments.size()));
            return true;
        } catch (Exception e) { out.println("MGR_STATS_ERROR;Blad: " + e.getMessage()); return true; }
    }

    private boolean handleAddDiscount(String[] tokens, PrintWriter out) {
        try {
            int campaignId = Integer.parseInt(tokens[1]);
            String type = tokens[2];
            BigDecimal value = new BigDecimal(tokens[3]);
            discountDao.save(new Discount(campaignId, type, value));
            out.println("ADD_DISCOUNT_OK;Zniżka dodana pomyślnie.");
            return true;
        } catch (Exception e) { out.println("ADD_DISCOUNT_ERROR;Upewnij sie, ze ID kampanii istnieje!"); return true; }
    }

    private boolean handleGetDiscounts(PrintWriter out) {
        try {
            List<Discount> list = discountDao.findAll();
            StringBuilder sb = new StringBuilder("DISCOUNTS_OK");
            for (Discount d : list) {
                String cName = promoCampaignDao.findById(d.getCampaignId()).map(PromoCampaign::getName).orElse("Nieznana");
                sb.append(";ID:").append(d.getId()).append(" | Kampania: ").append(cName)
                  .append(" | Typ: ").append(d.getDiscountType()).append(" | Wartosc: ").append(d.getDiscountValue());
            }
            out.println(sb.toString());
            return true;
        } catch (Exception e) { out.println("DISCOUNTS_ERROR;Blad: " + e.getMessage()); return true; }
    }

    private boolean handleDeleteDiscount(String[] tokens, PrintWriter out) {
        try {
            if(tokens.length < 2 || tokens[1].isEmpty()) {
                out.println("DELETE_DISCOUNT_ERROR;Podaj ID znizki!");
                return true;
            }
            int id = Integer.parseInt(tokens[1]);
            discountDao.delete(id);
            out.println("DELETE_DISCOUNT_OK;Zniżka o ID " + id + " została usunięta.");
            return true;
        } catch (Exception e) { out.println("DELETE_DISCOUNT_ERROR;Blad bazy przy usuwaniu znizki."); return true; }
    }

    private boolean handleAddCampaignPass(String[] tokens, PrintWriter out) {
        try {
            int campaignId = Integer.parseInt(tokens[1]);
            int passTypeId = Integer.parseInt(tokens[2]);
            campaignPassDao.save(new CampaignPass(campaignId, passTypeId));
            out.println("ADD_CPASS_OK;Karnet przypisany do kampanii.");
            return true;
        } catch (Exception e) { out.println("ADD_CPASS_ERROR;Sprawdz czy ID kampanii oraz ID karnetu sa poprawne."); return true; }
    }

    private boolean handleGetCampaignPasses(PrintWriter out) {
        try {
            List<CampaignPass> list = campaignPassDao.findAll();
            StringBuilder sb = new StringBuilder("CPASSES_OK");
            for (CampaignPass cp : list) {
                String cName = promoCampaignDao.findById(cp.getCampaignId()).map(PromoCampaign::getName).orElse("Nieznana");
                String pName = passTypeDao.findById(cp.getPassTypeId()).map(PassType::getName).orElse("Nieznany");
                sb.append(";ID:").append(cp.getId()).append(" | Kampania: ").append(cName).append(" -> Karnet: ").append(pName);
            }
            out.println(sb.toString());
            return true;
        } catch (Exception e) { out.println("CPASSES_ERROR;Blad: " + e.getMessage()); return true; }
    }

    private boolean handleDeleteCampaignPass(String[] tokens, PrintWriter out) {
        try {
            if(tokens.length < 2 || tokens[1].isEmpty()) {
                out.println("DELETE_CPASS_ERROR;Podaj ID powiazania!");
                return true;
            }
            int id = Integer.parseInt(tokens[1]);
            campaignPassDao.delete(id);
            out.println("DELETE_CPASS_OK;Powiązanie o ID " + id + " zostało usunięte.");
            return true;
        } catch (Exception e) { out.println("DELETE_CPASS_ERROR;Blad bazy przy usuwaniu powiazania."); return true; }
    }
}